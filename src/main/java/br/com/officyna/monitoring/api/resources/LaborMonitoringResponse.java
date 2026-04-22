package br.com.officyna.monitoring.api.resources;

import java.time.LocalDateTime;

public record LaborMonitoringResponse(
        String laborId,
        String laborName,
        String laborDescription,
        Double averageExecutionTimeInDays,
        Integer totalExecutions,
        LocalDateTime updatedAt
) {}