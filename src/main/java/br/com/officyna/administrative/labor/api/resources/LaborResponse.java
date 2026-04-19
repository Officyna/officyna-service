package br.com.officyna.administrative.labor.api.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LaborResponse(
    String id,
    String name,
    String description,
    BigDecimal price,
    Integer executionTimeInDays,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean active
) {}
