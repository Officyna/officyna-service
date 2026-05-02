package br.com.officyna.administrative.labor.api;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
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

@RequestMapping("/api/labors")
@Tag(name = "Labors", description = "Labor management")
@SecurityRequirement(name = "bearerAuth")
public interface LaborApi {

    @GetMapping
    @Operation(summary = "List all labors")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<LaborResponse>> findAll();

    @GetMapping("/{id}")
    @Operation(summary = "Find labor by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labor found"),
            @ApiResponse(responseCode = "404", description = "Labor not found",
                    content = @Content(schema=@Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<LaborResponse> findById(
            @Parameter(description = "Labor ID") @PathVariable String id);

    @PostMapping
    @Operation(summary = "Create new labor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Labor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or name already registered",
                    content = @Content(schema=@Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<LaborResponse> create(@Valid @RequestBody LaborRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Update labor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Labor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema=@Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Labor not found", content = @Content(schema=@Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<LaborResponse> update(
            @Parameter(description = "Labor ID") @PathVariable String id,
            @Valid @RequestBody LaborRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete labor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Labor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Labor not found", content = @Content(schema=@Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Labor ID") @PathVariable String id);
}
