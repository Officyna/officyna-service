package br.com.officyna.administrative.vehicle.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Data for vehicle creation or update")
public record VehicleRequest(

    @Schema(description = "Owner customer ID", example = "customer-id")
    @NotNull(message = "Customer is required")
    String customerId,

    @Schema(description = "Vehicle plate (format ABC-1234 or ABC1D23)", example = "ABC-1234")
    @NotBlank(message = "Plate is required")
    @Size(max = 10)
    String plate,

    @Schema(description = "Vehicle brand", example = "Toyota")
    @NotBlank(message = "Brand is required")
    @Size(max = 50)
    String brand,

    @Schema(description = "Vehicle model", example = "Corolla")
    @NotBlank(message = "Model is required")
    @Size(max = 80)
    String model,

    @Schema(description = "Manufacturing year", example = "2020")
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Invalid year")
    @Max(value = 2100, message = "Invalid year")
    Integer year,

    @Schema(description = "Vehicle color", example = "Silver")
    @Size(max = 30)
    String color

) {}