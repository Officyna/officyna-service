package br.com.officyna.infrastructure.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Email do usuário", example = "joao.silva@example.com")
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @Schema(description = "Senha do usuário", example = "SenhaSegura123")
        @NotBlank(message = "Senha é obrigatória")
        String password
) {}