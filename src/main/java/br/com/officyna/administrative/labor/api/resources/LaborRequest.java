package br.com.officyna.administrative.labor.api.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LaborRequest(
    String name,
    String description,
    BigDecimal price,
    @NotNull
    @Positive
    Integer executionTimeInDays,
    Boolean active
) {}
