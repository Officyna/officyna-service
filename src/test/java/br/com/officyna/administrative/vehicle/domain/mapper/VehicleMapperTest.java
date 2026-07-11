package br.com.officyna.administrative.vehicle.domain.mapper;

import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleMapperTest {

    private final VehicleMapper mapper = new VehicleMapper();

    private VehicleRequest buildRequest(String plate) {
        return new VehicleRequest("cust-1", plate, "Toyota", "Corolla", 2020, "Prata");
    }

    @Test
    @DisplayName("toEntity deve mapear todos os campos corretamente")
    void toEntity_DeveMappearTodosOsCampos() {
        VehicleRequest request = buildRequest("abc-1234");

        Vehicle entity = mapper.toEntity(request);

        assertEquals("cust-1", entity.getCustomerId());
        assertEquals("Toyota", entity.getBrand());
        assertEquals("Corolla", entity.getModel());
        assertEquals(2020, entity.getYear());
        assertEquals("Prata", entity.getColor());
    }

    @Test
    @DisplayName("toEntity deve converter placa para maiúsculo")
    void toEntity_DeveConverterPlacaParaMaiusculo() {
        VehicleRequest request = buildRequest("abc-1234");

        Vehicle entity = mapper.toEntity(request);

        assertEquals("ABC-1234", entity.getPlate());
    }

    @Test
    @DisplayName("toEntity deve manter placa já em maiúsculo sem alteração")
    void toEntity_DeveManterPlacaJaEmMaiusculo() {
        VehicleRequest request = buildRequest("XYZ-9876");

        Vehicle entity = mapper.toEntity(request);

        assertEquals("XYZ-9876", entity.getPlate());
    }

    @Test
    @DisplayName("toEntity deve definir active como true")
    void toEntity_DeveDefinirActiveTrue() {
        VehicleRequest request = buildRequest("ABC-1234");

        Vehicle entity = mapper.toEntity(request);

        assertTrue(entity.isActive());
    }
}