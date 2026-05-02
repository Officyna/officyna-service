package br.com.officyna.administrative.vehicle.api;

import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle management")
@SecurityRequirement(name = "bearerAuth")
public interface VehicleApi {

    @GetMapping
    @Operation(summary = "List all vehicles")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<VehicleResponse>> findAll();

    @GetMapping("/{id}")
    @Operation(summary = "Find vehicle by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle found"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    ResponseEntity<VehicleResponse> findById(
            @Parameter(description = "Vehicle ID") @PathVariable String id);

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List vehicles by customer")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<VehicleResponse>> findByCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId);

    @PostMapping
    @Operation(summary = "Register new vehicle")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data or plate already registered"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    ResponseEntity<VehicleResponse> update(
            @Parameter(description = "Vehicle ID") @PathVariable String id,
            @Valid @RequestBody VehicleRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate vehicle (soft delete)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Vehicle deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Vehicle ID") @PathVariable String id);
}