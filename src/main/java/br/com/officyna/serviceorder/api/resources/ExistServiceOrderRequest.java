package br.com.officyna.serviceorder.api.resources;

import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados para atualização de Ordem de Serviço")
public class ExistServiceOrderRequest {

    @Schema(description = "Texto de informação/observação do cliente ou diagnóstico inicial")
    @Size(max = 1000, message = "O texto de informação deve ter no máximo 1000 caracteres")
    private String informationText;

    @Schema(description = "ID do mecanico", example = "60d5ecb8b392d40015f69e1a")
    private String mechanicId;

    @Schema(description = "Status da O.S", example = "EM_EXECUCAO")
    private ServiceOrderStatus status;

}
