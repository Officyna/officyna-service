package br.com.officyna.serviceorder.api;

import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
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
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Not found",
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
    ResponseEntity<ServiceOrderResponse> findById(
            @Parameter(description = "Service order ID") @PathVariable String id);

    @GetMapping("/number/{serviceOrderNumber}")
    @Operation(summary = "Find Service Order by number")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service order found",
                    content = @Content(
                            schema = @Schema(implementation = ServiceOrderResponse.class)
                    ))
    })
    ResponseEntity<ServiceOrderResponse> findByServiceOrderNumber(
            @Parameter(description = "Service order number") @PathVariable Long serviceOrderNumber);

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

    @PatchMapping("/{id}")
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
    ResponseEntity<ServiceOrderResponse> updateServiceOrder(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @Valid @RequestBody ExistServiceOrderRequest request);


    @PutMapping("/{id}/add-labors")
    @Operation(summary = "Add labors to a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labors added to the service order")
    })
    ResponseEntity<ServiceOrderResponse> addLaborInServiceOrder(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @RequestBody List<LaborsRequest> laborsIdList);

    @PutMapping("/{id}/remove-labors/{laborId}")
    @Operation(summary = "Delete labor from a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labor removed from the service order")
    })
    ResponseEntity<ServiceOrderResponse> removeLaborFromServiceOrder(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @Parameter(description = "Labor ID") @PathVariable String laborId);

    @PutMapping("/{id}/add-supply")
    @Operation(summary = "Add supplys to a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplys added to the service order")
    })
    ResponseEntity<ServiceOrderResponse> addSupplyInServiceOrder(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @RequestBody List<SupplysRequest> laborsIdList);

    @PutMapping("/{id}/remove-supply/{supplyId}")
    @Operation(summary = "Delete supplys from a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supply removed from the service order")
    })
    ResponseEntity<ServiceOrderResponse> removeSupplyFromServiceOrder(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @Parameter(description = "Supply ID") @PathVariable String supplyId);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Service order")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Service order deleted"
        )
    })
    ResponseEntity<Void> deleteServiceOrder(
            @Parameter(description = "Service order ID") @PathVariable String id);

    @PutMapping("/{id}/start-labor/{laborId}")
    @Operation(summary = "Start a labor in a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labor started in the service order")
    })
    ResponseEntity<ServiceOrderResponse> startLabor(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @Parameter(description = "Labor ID") @PathVariable String laborId);

    @PutMapping("/{id}/finish-labor/{laborId}")
    @Operation(summary = "Finish a labor in a Service order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labor finished in the service order")
    })
    ResponseEntity<ServiceOrderResponse> finishLabor(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @Parameter(description = "Labor ID") @PathVariable String laborId);

    @PatchMapping("/{id}/update-status/")
    @Operation(summary = "Status updated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated")
    })
    ResponseEntity<ServiceOrderResponse> updateStatus(
            @Parameter(description = "Service order ID") @PathVariable String id,
            @Parameter(description = "Status") @RequestParam ServiceOrderStatus status);

    @PostMapping("{id}/send-os")
    @Operation(summary = "Service order send to customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesfull")
    })
    ResponseEntity<SendToCustomerResponse> sendToCustomer(@Parameter(description = "Service order ID" )@PathVariable String id);
}
