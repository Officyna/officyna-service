package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.CustomerType;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.infrastructure.exception.NotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        CustomerResponse response = new CustomerResponse("1",
                "Ricardo Almeida",
                "342.155.890-12",
                CustomerType.INDIVIDUAL,
                "ricardo.almeida@email.com",
                "98765-4321",
                "11",
                "+55",
                new AddressDTO("Rua Flaviano de Melo",
                        "500",
                        "Bloco B, Apt 12",
                        "Centro",
                        "Mogi das Cruzes",
                        "SP",
                        "08710-000",
                        "Brazil"),
                true,
                LocalDateTime.now()
        );

        when(customerService.findById(id)).thenReturn(response);

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
        UserResponse response = UserResponse.builder().id(id).name("Mecânico Master").build();

        when(userService.findById(id)).thenReturn(response);

        MechanicDTO result = service.getMechanic(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Mecânico Master");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar cliente não encontrado")
    void getCustomer_ShouldThrowNotFound_WhenCustomerNotFound() {
        String id = "non-existent";

        when(customerService.findById(id)).thenThrow(new NotFoundException("Cliente não encontrado"));

        assertThatThrownBy(() -> service.getCustomer(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Cliente não encontrado");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar mecânico não encontrado")
    void getMechanic_ShouldThrowNotFound_WhenMechanicNotFound() {
        String id = "non-existent";

        when(userService.findById(id)).thenThrow(new NotFoundException("Mecânico não encontrado"));

        assertThatThrownBy(() -> service.getMechanic(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Mecânico não encontrado");
    }

    @Test
    @DisplayName("Deve buscar cliente por documento e retornar response")
    void getCustomerByDocument_ShouldReturnCustomerResponse() {
        String document = "342.155.890-12";
        CustomerResponse response = new CustomerResponse("1",
                "Ricardo Almeida",
                document,
                CustomerType.INDIVIDUAL,
                "ricardo.almeida@email.com",
                "98765-4321",
                "11",
                "+55",
                new AddressDTO("Rua Flaviano de Melo", "500", "Bloco B, Apt 12", "Centro", "Mogi das Cruzes", "SP", "08710-000", "Brazil"),
                true,
                LocalDateTime.now()
        );

        when(customerService.findByDocument(document)).thenReturn(response);

        CustomerResponse result = service.getCustomerByDocument(document);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Ricardo Almeida");
        assertThat(result.id()).isEqualTo("1");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar cliente por documento não encontrado")
    void getCustomerByDocument_ShouldThrowNotFound_WhenDocumentNotFound() {
        String document = "999.999.999-99";

        when(customerService.findByDocument(document)).thenThrow(new NotFoundException("Cliente não encontrado"));

        assertThatThrownBy(() -> service.getCustomerByDocument(document))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Deve mapear todas as informações do cliente corretamente")
    void getCustomer_ShouldMapAllCustomerInformation() {
        String id = "cust-2";
        AddressDTO addressDTO = new AddressDTO("Rua de Teste", "123", "Apt 45", "Bairro", "Cidade", "ST", "12345-678", "País");
        CustomerResponse response = new CustomerResponse(id, "João Silva", "123.456.789-00", CustomerType.INDIVIDUAL,
                "joao@email.com", "99999-8888", "21", "+55", addressDTO, true, LocalDateTime.now());

        when(customerService.findById(id)).thenReturn(response);

        CustomerDTO result = service.getCustomer(id);

        assertThat(result)
                .isNotNull()
                .extracting("id", "fullName", "phoneNumber")
                .contains(id, "João Silva", "99999-8888");
        assertThat(result.getFullAdress()).contains("Rua de Teste", "123", "Bairro", "Cidade", "ST", "12345-678");
    }
}