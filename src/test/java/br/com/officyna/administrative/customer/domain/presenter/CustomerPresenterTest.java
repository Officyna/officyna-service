package br.com.officyna.administrative.customer.domain.presenter;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerPresenterTest {

    private final CustomerPresenter presenter = new CustomerPresenter();

    private Address buildAddressEntity() {
        return Address.builder()
                .street("Av. Paulista")
                .number("1000")
                .complement("Apto 42")
                .neighborhood("Bela Vista")
                .city("São Paulo")
                .state("SP")
                .zipCode("01310-000")
                .country("Brasil")
                .build();
    }

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente com endereço")
    void toResponse_ShouldMapAllFields_WithAddress() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 3, 10, 9, 0);
        Customer entity = Customer.builder()
                .id("cust-1")
                .name("João Silva")
                .document("12345678909")
                .type(CustomerType.INDIVIDUAL)
                .email("joao@email.com")
                .phone("99999-9999")
                .areaCode("11")
                .countryCode("+55")
                .address(buildAddressEntity())
                .active(true)
                .createdAt(createdAt)
                .build();

        CustomerResponse response = presenter.toResponse(entity);

        assertEquals("cust-1", response.id());
        assertEquals("João Silva", response.name());
        assertEquals("12345678909", response.document());
        assertEquals(CustomerType.INDIVIDUAL, response.type());
        assertEquals("joao@email.com", response.email());
        assertEquals("99999-9999", response.phone());
        assertEquals("11", response.areaCode());
        assertEquals("+55", response.countryCode());
        assertTrue(response.active());
        assertEquals(createdAt, response.createdAt());
    }

    @Test
    @DisplayName("toResponse deve mapear endereço corretamente")
    void toResponse_ShouldMapAddress() {
        Customer entity = Customer.builder()
                .id("cust-1")
                .name("João Silva")
                .document("12345678909")
                .type(CustomerType.INDIVIDUAL)
                .active(true)
                .address(buildAddressEntity())
                .build();

        CustomerResponse response = presenter.toResponse(entity);

        assertNotNull(response.address());
        assertEquals("Av. Paulista", response.address().street());
        assertEquals("1000", response.address().number());
        assertEquals("Apto 42", response.address().complement());
        assertEquals("Bela Vista", response.address().neighborhood());
        assertEquals("São Paulo", response.address().city());
        assertEquals("SP", response.address().state());
        assertEquals("01310-000", response.address().zipCode());
        assertEquals("Brasil", response.address().country());
    }

    @Test
    @DisplayName("toResponse deve retornar address null quando entidade não tem endereço")
    void toResponse_ShouldReturnNullAddress_WhenEntityHasNoAddress() {
        Customer entity = Customer.builder()
                .id("cust-1")
                .name("Maria")
                .document("12345678909")
                .type(CustomerType.INDIVIDUAL)
                .active(true)
                .address(null)
                .build();

        CustomerResponse response = presenter.toResponse(entity);

        assertNull(response.address());
    }
}