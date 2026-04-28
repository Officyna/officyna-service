package br.com.officyna.serviceorder.api;

import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/serviceorder")
@Tag(name = "CustomerServiceOrder", description = "Customer management service order")
@ApiResponses({
        @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
                )
        )
})
public interface CustomerServiceOrderApi {

    @GetMapping("/customer/{document}")
    @Operation(summary = "Get all service orders for a customer by document, optionally filtered by status")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service orders found",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ServiceOrderResponse.class))
                    )
            )
    })
    ResponseEntity<List<ServiceOrderResponse>> findByCustomerDocument(
            @Parameter(description = "Customer document") @PathVariable String document,
            @Parameter(description = "Service order status") @RequestParam(value = "status", required = false) ServiceOrderStatus status
    );

    @PatchMapping("/aproval-labors/{id}")
    @Operation(summary = "Aproval or regect labors")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "situation modifyed",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ServiceOrderResponse.class))
                    )
            )
    })
    ResponseEntity<ServiceOrderResponse> aprovalLabors(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @RequestBody(required = true) List<ModifySituationRequest> request
            );
}
