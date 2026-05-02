package br.com.officyna.administrative.labor.domain.mapper;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.LaborEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LaborMapperTest {

    private final LaborMapper mapper = new LaborMapper();

    private LaborRequest buildRequest(String name, Boolean active) {
        return new LaborRequest(name, "Descrição do serviço", new BigDecimal("120.00"), 2, active);
    }

    private LaborEntity buildEntity() {
        return LaborEntity.builder()
                .id("lab-1")
                .name("Troca de óleo")
                .description("Troca de óleo com filtro")
                .price(new BigDecimal("120.00"))
                .executionTimeInDays(1)
                .active(true)
                .build();
    }

    // ─────────────── toEntity ───────────────

    @Test
    @DisplayName("toEntity deve mapear todos os campos corretamente")
    void toEntity_DeveMappearTodosOsCampos() {
        LaborRequest request = buildRequest("Troca de óleo", true);

        LaborEntity entity = mapper.toEntity(request);

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

        LaborEntity entity = mapper.toEntity(request);

        assertFalse(entity.getActive());
    }

    // ─────────────── toResponse ───────────────

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente")
    void toResponse_DeveMappearTodosOsCampos() {
        LocalDateTime now = LocalDateTime.now();
        LaborEntity entity = LaborEntity.builder()
                .id("lab-1")
                .name("Troca de óleo")
                .description("Troca de óleo com filtro")
                .price(new BigDecimal("120.00"))
                .executionTimeInDays(1)
                .active(true)
                .build();

        LaborResponse response = mapper.toResponse(entity);

        assertEquals("lab-1", response.id());
        assertEquals("Troca de óleo", response.name());
        assertEquals("Troca de óleo com filtro", response.description());
        assertEquals(new BigDecimal("120.00"), response.price());
        assertEquals(1, response.executionTimeInDays());
        assertTrue(response.active());
    }

    // ─────────────── updateEntity ───────────────

    @Test
    @DisplayName("updateEntity deve atualizar todos os campos do entity")
    void updateEntity_DeveAtualizarTodosOsCampos() {
        LaborEntity entity = buildEntity();
        LaborRequest request = new LaborRequest("Balanceamento", "Balanceamento de rodas", new BigDecimal("80.00"), 1, true);

        mapper.updateEntity(entity, request);

        assertEquals("Balanceamento", entity.getName());
        assertEquals("Balanceamento de rodas", entity.getDescription());
        assertEquals(new BigDecimal("80.00"), entity.getPrice());
        assertEquals(1, entity.getExecutionTimeInDays());
        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("updateEntity deve desativar serviço quando active for false")
    void updateEntity_DeveDesativarServico() {
        LaborEntity entity = buildEntity();
        LaborRequest request = new LaborRequest("Troca de óleo", "Descrição", new BigDecimal("120.00"), 1, false);

        mapper.updateEntity(entity, request);

        assertFalse(entity.getActive());
    }
}