package br.com.officyna.administrative.supply.api.controller;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SupplyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SupplyService supplyService;

    @InjectMocks
    private SupplyController supplyController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(supplyController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Deve retornar lista de todos os insumos e peças")
    void findAll_ShouldReturnOk() throws Exception {
        var response = createResponse();
        when(supplyService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/supplies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(supplyService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar insumos e peças filtrados por tipo")
    void findByType_ShouldReturnOk() throws Exception {
        var response = createResponse();
        when(supplyService.findByType(SupplyType.SUPPLY)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/supplies/type/{type}", SupplyType.SUPPLY))
                .andExpect(status().isOk());

        verify(supplyService, times(1)).findByType(SupplyType.SUPPLY);
    }

    @Test
    @DisplayName("Deve retornar um insumo por ID")
    void findById_ShouldReturnSupply() throws Exception {
        String id = "123";
        var response = createResponse();
        when(supplyService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/supplies/{id}", id))
                .andExpect(status().isOk());

        verify(supplyService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve criar um novo insumo com sucesso")
    void create_ShouldReturnCreated() throws Exception {
        var request = createRequest();
        var response = createResponse();
        when(supplyService.create(any(SupplyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/supplies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(supplyService, times(1)).create(any(SupplyRequest.class));
    }

    @Test
    @DisplayName("Deve atualizar um insumo existente")
    void update_ShouldReturnOk() throws Exception {
        String id = "123";
        var request = createRequest();
        var response = createResponse();
        when(supplyService.update(eq(id), any(SupplyRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/supplies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(supplyService, times(1)).update(eq(id), any(SupplyRequest.class));
    }

    @Test
    @DisplayName("Deve deletar um insumo e retornar No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        String id = "123";
        doNothing().when(supplyService).delete(id);

        mockMvc.perform(delete("/api/supplies/{id}", id))
                .andExpect(status().isNoContent());

        verify(supplyService, times(1)).delete(id);
    }

    private SupplyRequest createRequest() {
        return new SupplyRequest(
                "Óleo Motor 5W30", "Óleo sintético para motor a gasolina",
                SupplyType.SUPPLY, new BigDecimal("45.90"), new BigDecimal("30.00"),
                50, 10, 3);
    }

    private SupplyResponse createResponse() {
        return new SupplyResponse(
                "123", "Óleo Motor 5W30", "Óleo sintético para motor a gasolina",
                SupplyType.SUPPLY, new BigDecimal("45.90"), new BigDecimal("59.67"),
                new BigDecimal("30.00"), 50, 10, 3, 47, false, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}