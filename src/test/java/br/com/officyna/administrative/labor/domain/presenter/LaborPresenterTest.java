package br.com.officyna.administrative.labor.domain.presenter;

import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.entity.Labor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LaborPresenterTest {

    private final LaborPresenter presenter = new LaborPresenter();

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente")
    void toResponse_DeveMappearTodosOsCampos() {
        Labor entity = Labor.builder()
                .id("lab-1")
                .name("Troca de óleo")
                .description("Troca de óleo com filtro")
                .price(new BigDecimal("120.00"))
                .executionTimeInDays(1)
                .active(true)
                .build();

        LaborResponse response = presenter.toResponse(entity);

        assertEquals("lab-1", response.id());
        assertEquals("Troca de óleo", response.name());
        assertEquals("Troca de óleo com filtro", response.description());
        assertEquals(new BigDecimal("120.00"), response.price());
        assertEquals(1, response.executionTimeInDays());
        assertTrue(response.active());
    }
}