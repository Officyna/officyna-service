package br.com.officyna.administrative.vehicle.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Vehicle data returned by the API")
public record VehicleResponse(

    @Schema(description = "Vehicle unique ID")
    String id,

    @Schema(description = "Owner customer ID")
    String customerId,

    @Schema(description = "Owner customer name")
    String customerName,

    @Schema(description = "Vehicle plate")
    String plate,

    @Schema(description = "Brand")
    String brand,

    @Schema(description = "Model")
    String model,

    @Schema(description = "Manufacturing year")
    Integer year,

    @Schema(description = "Color")
    String color,

    @Schema(description = "Is vehicle active?")
    Boolean active,

    @Schema(description = "Creation date")
    LocalDateTime createdAt

) {}