package br.com.officyna.administrative.vehicle.api.controller;

import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
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
class VehicleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Deve retornar lista de todos os veículos")
    void findAll_ShouldReturnOk() throws Exception {
        var response = createResponse();
        when(vehicleService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(vehicleService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um veículo por ID")
    void findById_ShouldReturnVehicle() throws Exception {
        String id = "123";
        var response = createResponse();
        when(vehicleService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/vehicles/{id}", id))
                .andExpect(status().isOk());

        verify(vehicleService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar veículos pelo ID do cliente")
    void findByCustomer_ShouldReturnVehicles() throws Exception {
        String customerId = "customer-1";
        var response = createResponse();
        when(vehicleService.findByCustomer(customerId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/vehicles/customer/{customerId}", customerId))
                .andExpect(status().isOk());

        verify(vehicleService, times(1)).findByCustomer(customerId);
    }

    @Test
    @DisplayName("Deve criar um novo veículo com sucesso")
    void create_ShouldReturnCreated() throws Exception {
        var request = createRequest();
        var response = createResponse();
        when(vehicleService.create(any(VehicleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(vehicleService, times(1)).create(any(VehicleRequest.class));
    }

    @Test
    @DisplayName("Deve atualizar um veículo existente")
    void update_ShouldReturnOk() throws Exception {
        String id = "123";
        var request = createRequest();
        var response = createResponse();
        when(vehicleService.update(eq(id), any(VehicleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/vehicles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(vehicleService, times(1)).update(eq(id), any(VehicleRequest.class));
    }

    @Test
    @DisplayName("Deve deletar um veículo e retornar No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        String id = "123";
        doNothing().when(vehicleService).delete(id);

        mockMvc.perform(delete("/api/vehicles/{id}", id))
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1)).delete(id);
    }

    private VehicleRequest createRequest() {
        return new VehicleRequest("customer-1", "ABC-1234", "Toyota", "Corolla", 2020, "Prata");
    }

    private VehicleResponse createResponse() {
        return new VehicleResponse(
                "123", "customer-1", "João Silva",
                "ABC-1234", "Toyota", "Corolla",
                2020, "Prata", true,
                LocalDateTime.now());
    }
}