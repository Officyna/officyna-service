package br.com.officyna.administrative.supply.api.resources;

import br.com.officyna.administrative.supply.domain.SupplyType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Supply/part data returned by the API")
public record SupplyResponse(

        @Schema(description = "Supply unique ID")
        String id,

        @Schema(description = "Name")
        String name,

        @Schema(description = "Description")
        String description,

        @Schema(description = "Type: PART or SUPPLY")
        SupplyType type,

        @Schema(description = "Purchase price")
        BigDecimal purchasePrice,

        @Schema(description = "Sale price (calculated from purchase price + markup)")
        BigDecimal salePrice,

        @Schema(description = "Markup percentage used to calculate sale price")
        BigDecimal markupPercentage,

        @Schema(description = "Current stock quantity")
        Integer stockQuantity,

        @Schema(description = "Minimum stock quantity before reorder alert")
        Integer minimumQuantity,

        @Schema(description = "Quantity reserved for open work orders")
        Integer reservedQuantity,

        @Schema(description = "Available quantity (stock - reserved)")
        Integer availableQuantity,

        @Schema(description = "Whether stock is below minimum quantity")
        Boolean belowMinimumStock,

        @Schema(description = "Is supply active?")
        Boolean active,

        @Schema(description = "Registration date")
        LocalDateTime createdAt,

        @Schema(description = "Last update date")
        LocalDateTime updatedAt

) {}