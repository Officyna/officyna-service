package br.com.officyna.administrative.user.api.resources;

import br.com.officyna.administrative.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @Schema(description = "Nome completo do usuário", example = "João da Silva")
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String name,

        @Schema(description = "Email do usuário", example = "joao.silva@example.com")
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
        String email,

        @Schema(description = "Senha do usuário", example = "SenhaSegura123")
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
        String password,

        @Schema(description = "Função/cargo do usuário", example = "ATTENDANT")
        @NotNull(message = "Função do usuário é obrigatória")
        UserRole userRole
) {}
