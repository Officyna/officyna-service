package br.com.officyna.administrative.vehicle.domain.mapper;

import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleMapperTest {

    private final VehicleMapper mapper = new VehicleMapper();

    private CustomerEntity buildCustomer() {
        return CustomerEntity.builder()
                .id("cust-1")
                .name("Maria Souza")
                .build();
    }

    private VehicleRequest buildRequest(String plate) {
        return new VehicleRequest("cust-1", plate, "Toyota", "Corolla", 2020, "Prata");
    }

    private VehicleEntity buildEntity() {
        return VehicleEntity.builder()
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

    // ─────────────── toEntity ───────────────

    @Test
    @DisplayName("toEntity deve mapear todos os campos corretamente")
    void toEntity_DeveMappearTodosOsCampos() {
        VehicleRequest request = buildRequest("abc-1234");
        CustomerEntity customer = buildCustomer();

        VehicleEntity entity = mapper.toEntity(request, customer);

        assertEquals("cust-1", entity.getCustomerId());
        assertEquals("Maria Souza", entity.getCustomerName());
        assertEquals("Toyota", entity.getBrand());
        assertEquals("Corolla", entity.getModel());
        assertEquals(2020, entity.getYear());
        assertEquals("Prata", entity.getColor());
    }

    @Test
    @DisplayName("toEntity deve converter placa para maiúsculo")
    void toEntity_DeveConverterPlacaParaMaiusculo() {
        VehicleRequest request = buildRequest("abc-1234");
        CustomerEntity customer = buildCustomer();

        VehicleEntity entity = mapper.toEntity(request, customer);

        assertEquals("ABC-1234", entity.getPlate());
    }

    @Test
    @DisplayName("toEntity deve manter placa já em maiúsculo sem alteração")
    void toEntity_DeveManterPlacaJaEmMaiusculo() {
        VehicleRequest request = buildRequest("XYZ-9876");
        CustomerEntity customer = buildCustomer();

        VehicleEntity entity = mapper.toEntity(request, customer);

        assertEquals("XYZ-9876", entity.getPlate());
    }

    @Test
    @DisplayName("toEntity deve definir active como true")
    void toEntity_DeveDefinirActiveTrue() {
        VehicleRequest request = buildRequest("ABC-1234");
        CustomerEntity customer = buildCustomer();

        VehicleEntity entity = mapper.toEntity(request, customer);

        assertTrue(entity.isActive());
    }

    // ─────────────── toResponse ───────────────

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente")
    void toResponse_DeveMappearTodosOsCampos() {
        VehicleEntity entity = buildEntity();

        VehicleResponse response = mapper.toResponse(entity);

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

    // ─────────────── updateEntity ───────────────

    @Test
    @DisplayName("updateEntity deve atualizar todos os campos do entity")
    void updateEntity_DeveAtualizarTodosOsCampos() {
        VehicleEntity entity = buildEntity();
        VehicleRequest request = new VehicleRequest("cust-2", "DEF-5678", "Honda", "Civic", 2022, "Branco");
        CustomerEntity newCustomer = CustomerEntity.builder().id("cust-2").name("João Silva").build();

        mapper.updateEntity(entity, request, newCustomer);

        assertEquals("cust-2", entity.getCustomerId());
        assertEquals("João Silva", entity.getCustomerName());
        assertEquals("DEF-5678", entity.getPlate());
        assertEquals("Honda", entity.getBrand());
        assertEquals("Civic", entity.getModel());
        assertEquals(2022, entity.getYear());
        assertEquals("Branco", entity.getColor());
    }

    @Test
    @DisplayName("updateEntity deve converter placa para maiúsculo")
    void updateEntity_DeveConverterPlacaParaMaiusculo() {
        VehicleEntity entity = buildEntity();
        VehicleRequest request = new VehicleRequest("cust-1", "def-5678", "Honda", "Civic", 2022, "Branco");
        CustomerEntity customer = buildCustomer();

        mapper.updateEntity(entity, request, customer);

        assertEquals("DEF-5678", entity.getPlate());
    }
}