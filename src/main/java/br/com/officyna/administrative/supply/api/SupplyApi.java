package br.com.officyna.administrative.supply.api;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.infrastructure.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/supplies")
@Tag(name = "Supplies", description = "Supplies and parts management")
@SecurityRequirement(name = "bearerAuth")
public interface SupplyApi {

    @GetMapping
    @Operation(summary = "List all active supplies and parts")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<SupplyResponse>> findAll();

    @GetMapping("/type/{type}")
    @Operation(summary = "List supplies or parts by type")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<SupplyResponse>> findByType(
            @Parameter(description = "Supply type: PART or SUPPLY") @PathVariable SupplyType type);

    @GetMapping("/{id}")
    @Operation(summary = "Find supply by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supply found"),
            @ApiResponse(responseCode = "404", description = "Supply not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<SupplyResponse> findById(
            @Parameter(description = "Supply ID") @PathVariable String id);

    @PostMapping
    @Operation(summary = "Create new supply or part")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supply created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or name already registered",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<SupplyResponse> create(@Valid @RequestBody SupplyRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Update supply or part")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supply updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or name already registered",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Supply not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<SupplyResponse> update(
            @Parameter(description = "Supply ID") @PathVariable String id,
            @Valid @RequestBody SupplyRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supply or part (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supply deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supply not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Supply ID") @PathVariable String id);
}