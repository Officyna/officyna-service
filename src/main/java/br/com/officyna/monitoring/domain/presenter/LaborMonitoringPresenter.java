package br.com.officyna.monitoring.domain.presenter;

import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import org.springframework.stereotype.Component;

@Component
public class LaborMonitoringPresenter {

    // 1 dia útil = 8 horas = 28800 segundos
    private static final int WORK_DAY_SECONDS_INT = 28800;

    public LaborMonitoringResponse toResponse(LaborMonitoring entity) {
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
}