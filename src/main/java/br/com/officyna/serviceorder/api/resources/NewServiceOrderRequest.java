package br.com.officyna.serviceorder.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados para criação de Ordem de Serviço")
public class NewServiceOrderRequest {

    @Schema(description = "ID do Cliente", example = "60d5ecb8b392d40015f69e1a")
    @NotBlank(message = "ID do Cliente é obrigatório")
    private String customerId;

    @Schema(description = "ID do Veículo", example = "60d5ecb8b392d40015f69e1b")
    @NotBlank(message = "ID do Veículo é obrigatório")
    private String vehicleId;

    @Schema(description = "Lista de IDs de Serviços (Mão de Obra)")
    @NotEmpty(message = "A ordem de serviço deve ter pelo menos um serviço")
    private List<String> laborIds;

    @Schema(description = "Texto de informação/observação do cliente ou diagnóstico inicial")
    @Size(max = 1000, message = "O texto de informação deve ter no máximo 1000 caracteres")
    private String informationText;
}
