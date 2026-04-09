package br.com.officyna.administrative.user.api;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
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

@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management")
@SecurityRequirement(name = "bearerAuth")
public interface UserApi {

    @GetMapping
    @Operation(summary = "List all active users")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    ResponseEntity<List<UserResponse>> findAll();

    @GetMapping("/{id}")
    @Operation(summary = "Find user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<UserResponse> findById(
            @Parameter(description = "User ID") @PathVariable String id);

    @GetMapping("/email/{email}")
    @Operation(summary = "Find user by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<UserResponse> findByEmail(
            @Parameter(description = "User email") @PathVariable String email);

    @PostMapping
    @Operation(summary = "Create new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or email already registered")
    })
    ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<UserResponse> update(
            @Parameter(description = "User ID") @PathVariable String id,
            @Valid @RequestBody UserRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "User ID") @PathVariable String id);
}
