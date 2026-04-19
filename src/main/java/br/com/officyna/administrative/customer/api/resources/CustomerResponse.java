package br.com.officyna.customer.api.resources;

import br.com.officyna.customer.domain.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Customer data returned by the API")
public record CustomerResponse(

        @Schema(description = "Customer unique ID")
        String id,

        @Schema(description = "Full name")
        String name,

        @Schema(description = "CPF or CNPJ document")
        String document,

        @Schema(description = "Customer type: INDIVIDUAL or COMPANY")
        CustomerType type,

        @Schema(description = "Email")
        String email,

        @Schema(description = "Phone number")
        String phone,

        @Schema(description = "Area code")
        String areaCode,

        @Schema(description = "Country code")
        String countryCode,

        @Schema(description = "Address")
        AddressDTO address,

        @Schema(description = "Is customer active?")
        Boolean active,

        @Schema(description = "Creation date")
        LocalDateTime createdAt

) {}