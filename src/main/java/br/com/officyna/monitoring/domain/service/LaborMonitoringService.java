package br.com.officyna.monitoring.domain.service;

import br.com.officyna.administrative.labor.domain.LaborEntity;
import br.com.officyna.administrative.labor.repository.LaborRepository;
import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.entity.LaborMonitoringEntity;
import br.com.officyna.monitoring.repository.LaborMonitoringRepository;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaborMonitoringService {

    // 1 dia útil = 8 horas = 28800 segundos
    private static final double WORK_DAY_SECONDS_DOUBLE = 28800.0;
    private static final int WORK_DAY_SECONDS_INT = 28800;

    private final LaborMonitoringRepository monitoringRepository;
    private final LaborRepository laborRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public List<LaborMonitoringResponse> findAll() {
        return monitoringRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Async
    public void updateExecutionTimeInDays(String laborId, LocalDateTime startDate, LocalDateTime endDate) {
        double durationInDays = ChronoUnit.SECONDS.between(startDate, endDate) / WORK_DAY_SECONDS_DOUBLE;

        if (durationInDays < 0) {
            log.warn("Ignorando atualização de tempo médio para laborId={}: endDate anterior ao startDate", laborId);
            return;
        }

        Optional<LaborMonitoringEntity> existing = monitoringRepository.findByLaborId(laborId);

        if (existing.isPresent()) {
            LaborMonitoringEntity entity = existing.get();
            double newAverage = calculateNewAverage(entity.getAverageExecutionTimeInDays(), entity.getTotalExecutions(), durationInDays);
            entity.setAverageExecutionTimeInDays(newAverage);
            entity.setTotalExecutions(entity.getTotalExecutions() + 1);
            monitoringRepository.save(entity);
        } else {
            Optional<LaborEntity> laborOpt = laborRepository.findById(laborId);
            if (laborOpt.isEmpty()) {
                log.warn("Labor não encontrado para laborId={}, ignorando atualização de monitoramento", laborId);
                return;
            }
            LaborEntity labor = laborOpt.get();
            LaborMonitoringEntity newEntity = LaborMonitoringEntity.builder()
                    .laborId(laborId)
                    .laborName(labor.getName())
                    .laborDescription(labor.getDescription())
                    .averageExecutionTimeInDays(durationInDays)
                    .totalExecutions(1)
                    .build();
            monitoringRepository.save(newEntity);
        }
    }

    public void initializeFromEstimate(String laborId, String laborName, String laborDescription, Integer estimatedDays) {
        if (estimatedDays == null) return;
        monitoringRepository.findByLaborId(laborId).ifPresentOrElse(
                entity -> {
                    entity.setLaborName(laborName);
                    entity.setLaborDescription(laborDescription);
                    monitoringRepository.save(entity);
                },
                () -> {
                    LaborMonitoringEntity entity = LaborMonitoringEntity.builder()
                            .laborId(laborId)
                            .laborName(laborName)
                            .laborDescription(laborDescription)
                            .averageExecutionTimeInDays((double) estimatedDays)
                            .totalExecutions(0)
                            .build();
                    monitoringRepository.save(entity);
                }
        );
    }

    public ForceRecalcResponse forceRecalc() {
        List<LaborEntity> labors = laborRepository.findByActiveTrue();
        int processed = 0;

        for (LaborEntity labor : labors) {
            List<Double> durations = serviceOrderRepository
                    .findByLaborIdWithCompletedExecutions(labor.getId())
                    .stream()
                    .flatMap(so -> so.getLabors().getLaborsDetails().stream())
                    .filter(detail -> labor.getId().equals(detail.getLaborId())
                            && detail.getStartDate() != null
                            && detail.getEndDate() != null)
                    .map(detail -> ChronoUnit.SECONDS.between(detail.getStartDate(), detail.getEndDate()) / WORK_DAY_SECONDS_DOUBLE)
                    .filter(duration -> duration >= 0)
                    .toList();

            if (durations.isEmpty())
                continue;

            LaborMonitoringEntity entity = monitoringRepository.findByLaborId(labor.getId())
                    .orElseGet(() -> LaborMonitoringEntity.builder().laborId(labor.getId()).build());

            entity.setLaborName(labor.getName());
            entity.setLaborDescription(labor.getDescription());
            entity.setAverageExecutionTimeInDays(
                    durations.stream().mapToDouble(Double::doubleValue).average().orElse(0)
            );
            entity.setTotalExecutions(durations.size());
            monitoringRepository.save(entity);
            processed++;
        }

        return new ForceRecalcResponse(processed);
    }

    private double calculateNewAverage(double currentAverage, int totalExecutions, double newDuration) {
        return (currentAverage * totalExecutions + newDuration) / (totalExecutions + 1);
    }

    private String formatDays(Double days) {
        if (days == null) return null;
        long totalSeconds = Math.round(days * WORK_DAY_SECONDS_INT);
        long d = totalSeconds / WORK_DAY_SECONDS_INT;
        long h = (totalSeconds % WORK_DAY_SECONDS_INT) / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        if (d > 0) {
            return String.format("%d dia%s %02d:%02d:%02d", d, d > 1 ? "s" : "", h, m, s);
        }
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private LaborMonitoringResponse toResponse(LaborMonitoringEntity entity) {
        return new LaborMonitoringResponse(
                entity.getLaborId(),
                entity.getLaborName(),
                entity.getLaborDescription(),
                entity.getAverageExecutionTimeInDays(),
                formatDays(entity.getAverageExecutionTimeInDays()),
                entity.getTotalExecutions(),
                entity.getUpdatedAt()
        );
    }
}