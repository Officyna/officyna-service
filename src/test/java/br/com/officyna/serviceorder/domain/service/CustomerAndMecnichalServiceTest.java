package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.serviceorder.domain.dto.CustomerDTO;
import br.com.officyna.serviceorder.domain.dto.MechanicDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAndMecnichalServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerAndMecnichalService service;

    @Test
    @DisplayName("Deve buscar cliente e mapear para DTO corretamente")
    void getCustomer_ShouldReturnCustomerDTO() {
        String id = "1";

        Customer customer = Customer.builder()
                .id("1")
                .name("Ricardo Almeida")
                .document("342.155.890-12")
                .type(CustomerType.INDIVIDUAL)
                .email("ricardo.almeida@email.com")
                .phone("98765-4321")
                .areaCode("11")
                .countryCode("+55")
                .address(Address.builder()
                        .street("Rua Flaviano de Melo")
                        .number("500")
                        .complement("Bloco B, Apt 12")
                        .neighborhood("Centro")
                        .city("Mogi das Cruzes")
                        .state("SP")
                        .zipCode("08710-000")
                        .country("Brazil")
                        .build())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(customerService.findById(id)).thenReturn(customer);

        CustomerDTO result = service.getCustomer(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFullName()).isEqualTo("Ricardo Almeida");
        assertThat(result.getFullAdress()).isEqualTo("Rua Flaviano de Melo, 500 - Centro, Mogi das Cruzes - SP, 08710-000");
    }

    @Test
    @DisplayName("Deve buscar mecânico e mapear para DTO corretamente")
    void getMechanic_ShouldReturnMechanicDTO() {
        String id = "mech-1";
        User user = User.builder().id(id).name("Mecânico Master").build();

        when(userService.findById(id)).thenReturn(user);

        MechanicDTO result = service.getMechanic(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Mecânico Master");
    }
}