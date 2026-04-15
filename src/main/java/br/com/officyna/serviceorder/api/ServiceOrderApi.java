package br.com.officyna.serviceorder.api;

import br.com.officyna.serviceorder.api.resources.ExistServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.IdListRequest;
import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/serviceorder")
@Tag(name = "ServiceOrder", description = "Service Order management")
@SecurityRequirement(name = "bearerAuth")
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
        )
})
public interface ServiceOrderApi {

    @GetMapping
    @Operation(summary = "List all service orders")
    @ApiResponses({
            @ApiResponse(
                    responseCode ="200",description ="List returned successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ServiceOrderResponse.class))
                    )
            )
    })
    ResponseEntity<List<ServiceOrderResponse>> findAll();

    @GetMapping("/{id}")
    @Operation(summary = "Find Service Order by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service order found",
                    content = @Content(
                            schema = @Schema(implementation = ServiceOrderResponse.class)
                    ))
    })
    ResponseEntity<ServiceOrderResponse> findById(@Parameter(description = "Service order ID") @PathVariable String id);

    @PostMapping()
    @Operation(summary = "Create a new Service order")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Service order created",
                    content = @Content(
                            schema = @Schema(implementation = ServiceOrderResponse.class)
                    )
            )
    })
    ResponseEntity<ServiceOrderResponse> createServiceOrder(@Valid NewServiceOrderRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Update a Service order")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service order updated",
                    content = @Content(
                            schema = @Schema(implementation = ServiceOrderResponse.class)
                    )
            )
    })
    ResponseEntity<ServiceOrderResponse> updateServiceOrder(@Param("Service order ID") @PathVariable String id, @Valid ExistServiceOrderRequest request);


    @PutMapping("/{id}/add-labors")
    @Operation(summary = "Add labors to a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labors added to the service order")
    })
    ResponseEntity<ServiceOrderResponse> addLaborInServiceOrder(@Param("Service order ID") String id, @RequestBody List<IdListRequest> laborsIdList);

    @DeleteMapping("/{id}/labors/{laborId}")
    @Operation(summary = "Delete labor from a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labor removed from the service order")
    })
    ResponseEntity<ServiceOrderResponse> removeLaborFromServiceOrder(@Param("Service order ID") String id, @Param("Labor ID") String laborId);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Service order")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Service order deleted"
        )
    })
    ResponseEntity<Void> deleteServiceOrder(@Parameter(description = "Service order ID") @PathVariable String id);
}
