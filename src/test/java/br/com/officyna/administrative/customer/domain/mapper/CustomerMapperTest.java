package br.com.officyna.administrative.customer.domain.mapper;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    private CustomerRequest buildRequest(String document, CustomerType type) {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerRequest("João Silva", document, type, "joao@email.com", "99999-9999", "11", "+55", address);
    }

    private CustomerRequest buildRequestNullAddress(String document, CustomerType type) {
        return new CustomerRequest("João Silva", document, type, "joao@email.com", "99999-9999", "11", "+55", null);
    }

    // ─────────────── toEntity ───────────────

    @Test
    @DisplayName("toEntity deve mapear todos os campos do request para a entidade")
    void toEntity_ShouldMapAllFields() {
        CustomerRequest request = buildRequest("12345678909", CustomerType.INDIVIDUAL);

        Customer entity = mapper.toEntity(request);

        assertEquals("João Silva", entity.getName());
        assertEquals("12345678909", entity.getDocument());
        assertEquals(CustomerType.INDIVIDUAL, entity.getType());
        assertEquals("joao@email.com", entity.getEmail());
        assertEquals("99999-9999", entity.getPhone());
        assertEquals("11", entity.getAreaCode());
        assertEquals("+55", entity.getCountryCode());
    }

    @Test
    @DisplayName("toEntity não deve normalizar o documento (regra do use case)")
    void toEntity_ShouldKeepRawDocument() {
        CustomerRequest request = buildRequest("123.456.789-09", CustomerType.INDIVIDUAL);

        Customer entity = mapper.toEntity(request);

        assertEquals("123.456.789-09", entity.getDocument());
    }

    @Test
    @DisplayName("toEntity deve mapear o endereço corretamente")
    void toEntity_ShouldMapAddress() {
        CustomerRequest request = buildRequest("12345678909", CustomerType.INDIVIDUAL);

        Customer entity = mapper.toEntity(request);

        assertNotNull(entity.getAddress());
        assertEquals("Rua das Flores", entity.getAddress().getStreet());
        assertEquals("100", entity.getAddress().getNumber());
        assertEquals("Centro", entity.getAddress().getNeighborhood());
        assertEquals("São Paulo", entity.getAddress().getCity());
        assertEquals("SP", entity.getAddress().getState());
        assertEquals("01310-100", entity.getAddress().getZipCode());
        assertEquals("Brasil", entity.getAddress().getCountry());
    }

    @Test
    @DisplayName("toEntity deve manter address null quando request não tem endereço")
    void toEntity_ShouldReturnNullAddress_WhenRequestHasNullAddress() {
        CustomerRequest request = buildRequestNullAddress("12345678909", CustomerType.INDIVIDUAL);

        Customer entity = mapper.toEntity(request);

        assertNull(entity.getAddress());
    }
}