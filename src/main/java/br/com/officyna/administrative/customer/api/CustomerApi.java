package br.com.officyna.administrative.customer.api;

import br.com.officyna.customer.api.resources.CustomerRequest;
import br.com.officyna.customer.api.resources.CustomerResponse;
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

@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer management")
@SecurityRequirement(name = "bearerAuth")
public interface CustomerApi {

    @GetMapping
    @Operation(summary = "List all active customers")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<CustomerResponse>> findAll();

    @GetMapping("/{id}")
    @Operation(summary = "Find customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<CustomerResponse> findById(
            @Parameter(description = "Customer ID") @PathVariable String id);

    @GetMapping("/document/{document}")
    @Operation(summary = "Find customer by CPF/CNPJ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<CustomerResponse> findByDocument(
            @Parameter(description = "Customer CPF or CNPJ") @PathVariable String document);

    @PostMapping
    @Operation(summary = "Create new customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or document already registered")
    })
    ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<CustomerResponse> update(
            @Parameter(description = "Customer ID") @PathVariable String id,
            @Valid @RequestBody CustomerRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate customer (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Customer ID") @PathVariable String id);
}
