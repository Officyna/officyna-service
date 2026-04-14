package br.com.officyna.administrative.labor.api.controller;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class LaborControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LaborService laborService;

    @InjectMocks
    private LaborController laborController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(laborController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Deve retornar lista de todos os serviços (labors)")
    void findAll_ShouldReturnOk() throws Exception {
        var response = createResponse();
        when(laborService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/labors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(laborService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um serviço por ID")
    void findById_ShouldReturnLabor() throws Exception {
        String id = "123";
        var response = createResponse();
        when(laborService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/labors/{id}", id))
                .andExpect(status().isOk());

        verify(laborService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve criar um novo serviço com sucesso")
    void create_ShouldReturnCreated() throws Exception {
        var request = createRequest();
        var response = createResponse();
        when(laborService.create(any(LaborRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/labors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(laborService, times(1)).create(any(LaborRequest.class));
    }

    @Test
    @DisplayName("Deve atualizar um serviço existente")
    void update_ShouldReturnOk() throws Exception {
        String id = "123";
        var request = createRequest();
        var response = createResponse();
        when(laborService.update(eq(id), any(LaborRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/labors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(laborService, times(1)).update(eq(id), any(LaborRequest.class));
    }

    @Test
    @DisplayName("Deve deletar um serviço e retornar No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        String id = "123";
        doNothing().when(laborService).delete(id);

        mockMvc.perform(delete("/api/labors/{id}", id))
                .andExpect(status().isNoContent());

        verify(laborService, times(1)).delete(id);
    }

    private LaborRequest createRequest() {
        return new LaborRequest("Troca de Óleo", "Troca de óleo e filtro", new BigDecimal("150.0"), 1, true);
    }

    private LaborResponse createResponse() {
        return new LaborResponse(
                "123", "Troca de Óleo", "Troca de óleo e filtro",
                new BigDecimal("150.0"), 1,
                LocalDateTime.now(), LocalDateTime.now(), true);
    }
}