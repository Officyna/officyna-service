package br.com.officyna.administrative.customer.api.controller;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.CustomerType;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Deve retornar lista de todos os clientes")
    void findAll_ShouldReturnOk() throws Exception {
        var response = createResponse();
        when(customerService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(customerService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um cliente por ID")
    void findById_ShouldReturnCustomer() throws Exception {
        String id = "123";
        var response = createResponse();
        when(customerService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isOk());

        verify(customerService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar um cliente pelo documento")
    void findByDocument_ShouldReturnCustomer() throws Exception {
        String document = "123.456.789-09";
        var response = createResponse();
        when(customerService.findByDocument(document)).thenReturn(response);

        mockMvc.perform(get("/api/customers/document/{document}", document))
                .andExpect(status().isOk());

        verify(customerService, times(1)).findByDocument(document);
    }

    @Test
    @DisplayName("Deve criar um novo cliente com sucesso")
    void create_ShouldReturnCreated() throws Exception {
        var request = createRequest();
        var response = createResponse();
        when(customerService.create(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(customerService, times(1)).create(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Deve atualizar um cliente existente")
    void update_ShouldReturnOk() throws Exception {
        String id = "123";
        var request = createRequest();
        var response = createResponse();
        when(customerService.update(eq(id), any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).update(eq(id), any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Deve deletar um cliente e retornar No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        String id = "123";
        doNothing().when(customerService).delete(id);

        mockMvc.perform(delete("/api/customers/{id}", id))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(id);
    }

    private CustomerRequest createRequest() {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerRequest("João Silva", "123.456.789-09", CustomerType.INDIVIDUAL, "joao@email.com", "99999-9999", "11", "+55", address);
    }

    private CustomerResponse createResponse() {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerResponse(
                "123", "João Silva", "123.456.789-09",
                CustomerType.INDIVIDUAL, "joao@email.com",
                "99999-9999", "11", "+55",
                address, true,
                LocalDateTime.now());
    }
}