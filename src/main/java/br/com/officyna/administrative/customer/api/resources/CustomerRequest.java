package br.com.officyna.administrative.customer.api.resources;

import br.com.officyna.administrative.customer.domain.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data for customer creation or update")
public record CustomerRequest(

        @Schema(description = "Customer full name", example = "John Doe")
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must have at most 150 characters")
        String name,

        @Schema(description = "CPF (000.000.000-00) or CNPJ (00.000.000/0000-00)", example = "123.456.789-09")
        @NotBlank(message = "Document is required")
        String document,

        @Schema(description = "Customer type: INDIVIDUAL or COMPANY", example = "INDIVIDUAL")
        @NotNull(message = "Customer type is required")
        CustomerType type,

        @Schema(description = "Customer email", example = "john@email.com")
        @Email(message = "Invalid email")
        @Size(max = 150)
        String email,

        @Schema(description = "Phone number", example = "99999-9999")
        @Size(max = 20)
        String phone,

        @Schema(description = "Area code", example = "11")
        @Size(max = 3)
        String areaCode,

        @Schema(description = "Country code", example = "+55")
        @Size(max = 3)
        String countryCode,

        @Schema(description = "Customer address")
        @Valid
        @NotNull(message = "Address is required")
        AddressDTO address

) {}
