package br.com.officyna.administrative.vehicle.domain.presenter;

import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehiclePresenterTest {

    private final VehiclePresenter presenter = new VehiclePresenter();

    private Vehicle buildEntity() {
        return Vehicle.builder()
                .id("veh-1")
                .customerId("cust-1")
                .customerName("Maria Souza")
                .plate("ABC-1234")
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .color("Prata")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente")
    void toResponse_DeveMappearTodosOsCampos() {
        Vehicle entity = buildEntity();

        VehicleResponse response = presenter.toResponse(entity);

        assertEquals("veh-1", response.id());
        assertEquals("cust-1", response.customerId());
        assertEquals("Maria Souza", response.customerName());
        assertEquals("ABC-1234", response.plate());
        assertEquals("Toyota", response.brand());
        assertEquals("Corolla", response.model());
        assertEquals(2020, response.year());
        assertEquals("Prata", response.color());
        assertTrue(response.active());
    }
}