package br.com.officyna.administrative.customer.domain;

import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    @DisplayName("Deve validar a criação da entidade via Builder e Getters")
    void builderAndGetters_ShouldWorkCorrectly() {
        String id = "id-123";
        String name = "João Silva";
        String document = "123.456.789-09";
        String email = "joao@email.com";
        String phone = "99999-9999";
        String areaCode = "11";
        String countryCode = "+55";
        LocalDateTime now = LocalDateTime.now();

        Address address = Address.builder()
                .street("Rua das Flores")
                .number("100")
                .complement("Apto 10")
                .neighborhood("Centro")
                .city("São Paulo")
                .state("SP")
                .zipCode("01310-100")
                .country("Brasil")
                .build();

        Customer entity = Customer.builder()
                .id(id)
                .name(name)
                .document(document)
                .type(CustomerType.INDIVIDUAL)
                .email(email)
                .phone(phone)
                .areaCode(areaCode)
                .countryCode(countryCode)
                .address(address)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertAll(
                () -> assertEquals(id, entity.getId()),
                () -> assertEquals(name, entity.getName()),
                () -> assertEquals(document, entity.getDocument()),
                () -> assertEquals(CustomerType.INDIVIDUAL, entity.getType()),
                () -> assertEquals(email, entity.getEmail()),
                () -> assertEquals(phone, entity.getPhone()),
                () -> assertEquals(areaCode, entity.getAreaCode()),
                () -> assertEquals(countryCode, entity.getCountryCode()),
                () -> assertNotNull(entity.getAddress()),
                () -> assertEquals("Rua das Flores", entity.getAddress().getStreet()),
                () -> assertTrue(entity.getActive()),
                () -> assertEquals(now, entity.getCreatedAt()),
                () -> assertEquals(now, entity.getUpdatedAt())
        );
    }
}