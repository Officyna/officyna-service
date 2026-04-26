package br.com.officyna.administrative.customer.domain.mapper;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    private CustomerRequest buildRequest(String document, CustomerType type) {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerRequest("João Silva", document, type, "joao@email.com", "99999-9999", "11", "+55", address);
    }

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
}