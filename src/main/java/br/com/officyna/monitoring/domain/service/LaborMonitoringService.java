package br.com.officyna.monitoring.domain.service;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import br.com.officyna.monitoring.domain.repository.LaborMonitoringRepository;
import br.com.officyna.serviceorder.domain.repository.ServiceOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LaborMonitoringService {

    private final LaborMonitoringRepository repository;
    private final LaborRepository laborRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public LaborMonitoringService(
            LaborMonitoringRepository monitoringRepository,
            LaborRepository laborRepository,
            ServiceOrderRepository serviceOrderRepository) {
        this.repository = monitoringRepository;
        this.laborRepository = laborRepository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    // 1 dia útil = 8 horas = 28800 segundos
    private static final double WORK_DAY_SECONDS_DOUBLE = 28800.0;

    public List<LaborMonitoring> findAll() {
        log.info("Finding all labor monitoring records");

        List<LaborMonitoring> monitoring = repository.findAll();

        log.info("Labor monitoring records found: {}", monitoring.size());

        return monitoring;
    }

    @Async
    public void updateExecutionTimeInDays(
            String laborId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        log.info(
                "Updating labor execution time. laborId={}, startDate={}, endDate={}",
                laborId,
                startDate,
                endDate
        );

        double durationInDays = ChronoUnit.SECONDS.between(
                startDate.atOffset(ZoneOffset.UTC),
                endDate.atOffset(ZoneOffset.UTC)
        ) / WORK_DAY_SECONDS_DOUBLE;

        if (durationInDays < 0) {
            log.warn(
                    "Ignoring labor execution time update. laborId={}: endDate is before startDate",
                    laborId
            );
            return;
        }

        Optional<LaborMonitoring> existing =
                repository.findByLaborId(laborId);

        if (existing.isPresent()) {

            LaborMonitoring entity = existing.get();

            double newAverage = calculateNewAverage(
                    entity.getAverageExecutionTimeInDays(),
                    entity.getTotalExecutions(),
                    durationInDays
            );

            entity.setAverageExecutionTimeInDays(newAverage);
            entity.setTotalExecutions(entity.getTotalExecutions() + 1);

            repository.save(entity);

            log.info(
                    "Labor execution monitoring updated. laborId={}, durationInDays={}, newAverage={}, totalExecutions={}",
                    laborId,
                    durationInDays,
                    newAverage,
                    entity.getTotalExecutions()
            );

        } else {

            Optional<Labor> laborOpt =
                    laborRepository.findById(laborId);

            if (laborOpt.isEmpty()) {
                log.warn(
                        "Labor not found for monitoring update. laborId={}",
                        laborId
                );
                return;
            }

            Labor labor = laborOpt.get();

            LaborMonitoring newEntity = LaborMonitoring.builder()
                    .laborId(laborId)
                    .laborName(labor.getName())
                    .laborDescription(labor.getDescription())
                    .averageExecutionTimeInDays(durationInDays)
                    .totalExecutions(1)
                    .build();

            repository.save(newEntity);

            log.info(
                    "Labor execution monitoring initialized. laborId={}, durationInDays={}",
                    laborId,
                    durationInDays
            );
        }
    }

    public void initializeFromEstimate(
            String laborId,
            String laborName,
            String laborDescription,
            Integer estimatedDays) {

        log.info(
                "Initializing labor monitoring from estimate. laborId={}, estimatedDays={}",
                laborId,
                estimatedDays
        );

        if (estimatedDays == null) {
            log.debug(
                    "Skipping labor monitoring initialization because estimatedDays is null. laborId={}",
                    laborId
            );
            return;
        }

        repository.findByLaborId(laborId).ifPresentOrElse(
                entity -> {
                    entity.setLaborName(laborName);
                    entity.setLaborDescription(laborDescription);

                    repository.save(entity);

                    log.info(
                            "Labor monitoring information updated from estimate. laborId={}",
                            laborId
                    );
                },
                () -> {
                    LaborMonitoring entity = LaborMonitoring.builder()
                            .laborId(laborId)
                            .laborName(laborName)
                            .laborDescription(laborDescription)
                            .averageExecutionTimeInDays((double) estimatedDays)
                            .totalExecutions(0)
                            .build();

                    repository.save(entity);

                    log.info(
                            "Labor monitoring created from estimate. laborId={}, estimatedDays={}",
                            laborId,
                            estimatedDays
                    );
                }
        );
    }

    public int forceRecalc() {
        log.info("Starting labor monitoring force recalculation");

        List<Labor> labors = laborRepository.findByActiveTrue();

        log.info(
                "Active labors found for force recalculation: {}",
                labors.size()
        );

        int processed = 0;

        for (Labor labor : labors) {

            log.debug(
                    "Calculating execution time for labor. laborId={}, laborName={}",
                    labor.getId(),
                    labor.getName()
            );

            List<Double> durations = serviceOrderRepository
                    .findByLaborIdWithCompletedExecutions(labor.getId())
                    .stream()
                    .flatMap(so ->
                            so.getLabors()
                                    .getLaborsDetails()
                                    .stream()
                    )
                    .filter(detail ->
                            labor.getId().equals(detail.getLaborId())
                                    && detail.getStartDate() != null
                                    && detail.getEndDate() != null
                    )
                    .map(detail ->
                            ChronoUnit.SECONDS.between(
                                    detail.getStartDate().atOffset(ZoneOffset.UTC),
                                    detail.getEndDate().atOffset(ZoneOffset.UTC)
                            ) / WORK_DAY_SECONDS_DOUBLE
                    )
                    .filter(duration -> duration >= 0)
                    .toList();

            if (durations.isEmpty()) {
                log.debug(
                        "No completed executions found for labor. laborId={}",
                        labor.getId()
                );
                continue;
            }

            LaborMonitoring entity = repository
                    .findByLaborId(labor.getId())
                    .orElseGet(() ->
                            LaborMonitoring.builder()
                                    .laborId(labor.getId())
                                    .build()
                    );

            entity.setLaborName(labor.getName());
            entity.setLaborDescription(labor.getDescription());

            double average = durations.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);

            entity.setAverageExecutionTimeInDays(average);
            entity.setTotalExecutions(durations.size());

            repository.save(entity);

            processed++;

            log.debug(
                    "Labor monitoring recalculated. laborId={}, averageExecutionTimeInDays={}, totalExecutions={}",
                    labor.getId(),
                    average,
                    durations.size()
            );
        }

        log.info(
                "Labor monitoring force recalculation completed. Records processed: {}",
                processed
        );

        return processed;
    }

    private double calculateNewAverage(
            double currentAverage,
            int totalExecutions,
            double newDuration) {

        return (currentAverage * totalExecutions + newDuration)
                / (totalExecutions + 1);
    }
}