package br.com.officyna.infrastructure.auth;

import br.com.officyna.administrative.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponse(
        @Schema(description = "Token JWT")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String type,

        @Schema(description = "Tempo de expiração em milissegundos")
        long expiresIn,

        @Schema(description = "ID do usuário")
        String userId,

        @Schema(description = "Nome do usuário")
        String name,

        @Schema(description = "Função/cargo do usuário")
        UserRole role
) {}