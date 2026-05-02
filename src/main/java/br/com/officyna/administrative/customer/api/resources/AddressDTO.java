package br.com.officyna.administrative.customer.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Customer address")
public record AddressDTO(

    @Schema(description = "Street name", example = "Flower Street")
    @NotBlank(message = "Street is required")
    @Size(max = 200)
    String street,

    @Schema(description = "Street number", example = "123")
    @NotBlank(message = "Number is required")
    @Size(max = 10)
    String number,

    @Schema(description = "Complement", example = "Apt 42")
    @Size(max = 100)
    String complement,

    @Schema(description = "Neighborhood", example = "Downtown")
    @NotBlank(message = "Neighborhood is required")
    @Size(max = 100)
    String neighborhood,

    @Schema(description = "City", example = "São Paulo")
    @NotBlank(message = "City is required")
    @Size(max = 100)
    String city,

    @Schema(description = "State abbreviation", example = "SP")
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must have 2 characters")
    String state,

    @Schema(description = "Zip code", example = "01310-100")
    @NotBlank(message = "Zip code is required")
    @Size(max = 10)
    String zipCode,

    @Schema(description = "Country", example = "Brazil")
    @Size(max = 60)
    String country

) {}