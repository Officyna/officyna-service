package br.com.officyna.administrative.labor.api.resources;

import java.math.BigDecimal;

public record LaborRequest(
    String name,
    String description,
    BigDecimal price,
    Integer executionTimeInDays
) {}
