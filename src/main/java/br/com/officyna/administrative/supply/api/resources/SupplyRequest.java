package br.com.officyna.administrative.supply.api.resources;

import br.com.officyna.administrative.supply.domain.SupplyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Data for supply/part creation or update")
public record SupplyRequest(

        @Schema(description = "Supply or part name", example = "Óleo Motor 5W30")
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must have at most 150 characters")
        String name,

        @Schema(description = "Detailed description", example = "Óleo sintético para motor a gasolina")
        @Size(max = 500, message = "Description must have at most 500 characters")
        String description,

        @Schema(description = "Type: PART or SUPPLY", example = "SUPPLY")
        @NotNull(message = "Type is required")
        SupplyType type,

        @Schema(description = "Purchase price", example = "45.90")
        @NotNull(message = "Purchase price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than zero")
        BigDecimal purchasePrice,

        @Schema(description = "Markup percentage applied over purchase price to calculate sale price", example = "30.00")
        @NotNull(message = "Markup percentage is required")
        @DecimalMin(value = "0.0", message = "Markup percentage must be zero or greater")
        BigDecimal markupPercentage,

        @Schema(description = "Current stock quantity", example = "50")
        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @Schema(description = "Minimum stock quantity before reorder alert", example = "10")
        @NotNull(message = "Minimum quantity is required")
        @Min(value = 0, message = "Minimum quantity cannot be negative")
        Integer minimumQuantity,

        @Schema(description = "Quantity reserved for open work orders", example = "3")
        @NotNull(message = "Reserved quantity is required")
        @Min(value = 0, message = "Reserved quantity cannot be negative")
        Integer reservedQuantity

) {}