package br.com.officyna.administrative.labor.domain.mapper;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.domain.entity.Labor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LaborMapperTest {

    private final LaborMapper mapper = new LaborMapper();

    private LaborRequest buildRequest(String name, Boolean active) {
        return new LaborRequest(name, "Descrição do serviço", new BigDecimal("120.00"), 2, active);
    }

    @Test
    @DisplayName("toEntity deve mapear todos os campos corretamente")
    void toEntity_DeveMappearTodosOsCampos() {
        LaborRequest request = buildRequest("Troca de óleo", true);

        Labor entity = mapper.toEntity(request);

        assertEquals("Troca de óleo", entity.getName());
        assertEquals("Descrição do serviço", entity.getDescription());
        assertEquals(new BigDecimal("120.00"), entity.getPrice());
        assertEquals(2, entity.getExecutionTimeInDays());
        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("toEntity deve respeitar o campo active enviado na request")
    void toEntity_DeveRespeitarCampoActive() {
        LaborRequest request = buildRequest("Troca de filtro", false);

        Labor entity = mapper.toEntity(request);

        assertFalse(entity.getActive());
    }
}