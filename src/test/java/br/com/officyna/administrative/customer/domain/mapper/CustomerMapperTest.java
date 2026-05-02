package br.com.officyna.administrative.customer.domain.mapper;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.AddressEntity;
import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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

    private AddressEntity buildAddressEntity() {
        return AddressEntity.builder()
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

    // ─────────────── toEntity ───────────────

    @Test
    @DisplayName("toEntity deve normalizar CPF formatado antes de persistir")
    void toEntity_ShouldNormalizeCpf() {
        CustomerRequest request = buildRequest("123.456.789-09", CustomerType.INDIVIDUAL);

        CustomerEntity entity = mapper.toEntity(request);

        assertEquals("12345678909", entity.getDocument());
    }

    @Test
    @DisplayName("toEntity deve normalizar CNPJ numérico formatado antes de persistir")
    void toEntity_ShouldNormalizeNumericCnpj() {
        CustomerRequest request = buildRequest("11.222.333/0001-81", CustomerType.COMPANY);

        CustomerEntity entity = mapper.toEntity(request);

        assertEquals("11222333000181", entity.getDocument());
    }

    @Test
    @DisplayName("toEntity deve normalizar CNPJ alfanumérico formatado antes de persistir")
    void toEntity_ShouldNormalizeAlphanumericCnpj() {
        CustomerRequest request = buildRequest("AB.123.456/0001-10", CustomerType.COMPANY);

        CustomerEntity entity = mapper.toEntity(request);

        assertEquals("AB123456000110", entity.getDocument());
    }

    @Test
    @DisplayName("toEntity deve manter documento já normalizado sem alteração")
    void toEntity_ShouldKeepAlreadyNormalizedDocument() {
        CustomerRequest request = buildRequest("12345678909", CustomerType.INDIVIDUAL);

        CustomerEntity entity = mapper.toEntity(request);

        assertEquals("12345678909", entity.getDocument());
    }

    @Test
    @DisplayName("toEntity deve definir active como true por padrão")
    void toEntity_ShouldSetActiveTrue() {
        CustomerRequest request = buildRequest("12345678909", CustomerType.INDIVIDUAL);

        CustomerEntity entity = mapper.toEntity(request);

        assertTrue(entity.getActive());
    }

    // ─────────────── toResponse ───────────────

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente com endereço")
    void toResponse_ShouldMapAllFields_WithAddress() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 3, 10, 9, 0);
        CustomerEntity entity = CustomerEntity.builder()
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
                .build();

        CustomerResponse response = mapper.toResponse(entity);

        assertEquals("cust-1", response.id());
        assertEquals("João Silva", response.name());
        assertEquals("12345678909", response.document());
        assertEquals(CustomerType.INDIVIDUAL, response.type());
        assertEquals("joao@email.com", response.email());
        assertEquals("99999-9999", response.phone());
        assertEquals("11", response.areaCode());
        assertEquals("+55", response.countryCode());
        assertTrue(response.active());
    }

    @Test
    @DisplayName("toResponse deve mapear endereço corretamente")
    void toResponse_ShouldMapAddress() {
        CustomerEntity entity = CustomerEntity.builder()
                .id("cust-1")
                .name("João Silva")
                .document("12345678909")
                .type(CustomerType.INDIVIDUAL)
                .active(true)
                .address(buildAddressEntity())
                .build();

        CustomerResponse response = mapper.toResponse(entity);

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
        CustomerEntity entity = CustomerEntity.builder()
                .id("cust-1")
                .name("Maria")
                .document("12345678909")
                .type(CustomerType.INDIVIDUAL)
                .active(true)
                .address(null)
                .build();

        CustomerResponse response = mapper.toResponse(entity);

        assertNull(response.address());
    }

    // ─────────────── updateEntity ───────────────

    @Test
    @DisplayName("updateEntity deve normalizar CPF formatado ao atualizar")
    void updateEntity_ShouldNormalizeCpf() {
        CustomerEntity entity = CustomerEntity.builder().document("52998224725").build();
        CustomerRequest request = buildRequest("123.456.789-09", CustomerType.INDIVIDUAL);

        mapper.updateEntity(entity, request);

        assertEquals("12345678909", entity.getDocument());
    }

    @Test
    @DisplayName("updateEntity deve normalizar CNPJ alfanumérico formatado ao atualizar")
    void updateEntity_ShouldNormalizeAlphanumericCnpj() {
        CustomerEntity entity = CustomerEntity.builder().document("11222333000181").build();
        CustomerRequest request = buildRequest("AB.123.456/0001-10", CustomerType.COMPANY);

        mapper.updateEntity(entity, request);

        assertEquals("AB123456000110", entity.getDocument());
    }

    @Test
    @DisplayName("updateEntity deve definir address como null quando request não tem endereço")
    void updateEntity_ShouldSetNullAddress_WhenRequestHasNullAddress() {
        AddressEntity existingAddress = buildAddressEntity();
        CustomerEntity entity = CustomerEntity.builder()
                .document("12345678909")
                .address(existingAddress)
                .build();
        CustomerRequest request = buildRequestNullAddress("12345678909", CustomerType.INDIVIDUAL);

        mapper.updateEntity(entity, request);

        assertNull(entity.getAddress());
    }

    @Test
    @DisplayName("updateEntity deve atualizar todos os campos de contato")
    void updateEntity_ShouldUpdateContactFields() {
        CustomerEntity entity = CustomerEntity.builder()
                .name("Antigo Nome")
                .email("antigo@email.com")
                .phone("88888-8888")
                .areaCode("21")
                .countryCode("+55")
                .document("12345678909")
                .build();
        CustomerRequest request = buildRequest("12345678909", CustomerType.INDIVIDUAL);

        mapper.updateEntity(entity, request);

        assertEquals("João Silva", entity.getName());
        assertEquals("joao@email.com", entity.getEmail());
        assertEquals("99999-9999", entity.getPhone());
        assertEquals("11", entity.getAreaCode());
        assertEquals("+55", entity.getCountryCode());
    }
}