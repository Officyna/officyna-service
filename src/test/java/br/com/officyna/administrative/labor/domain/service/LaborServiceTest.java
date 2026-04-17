package br.com.officyna.administrative.labor.domain.service;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.LaborEntity;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.repository.LaborRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaborServiceTest {

    @Mock
    private LaborRepository laborRepository;

    @Mock
    private LaborMapper laborMapper;

    @InjectMocks
    private LaborService laborService;

    private LaborEntity createLaborEntity(String id, String name, boolean active) {
        return LaborEntity.builder()
                .id(id)
                .name(name)
                .description("sbrubles " + name)
                .price(new BigDecimal("100.00"))
                .executionTimeInDays(1)
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private LaborRequest createLaborRequest(String name) {
        return new LaborRequest(name, "sbrubles " + name, new BigDecimal("100.00"), 1, true);
    }

    private LaborResponse createLaborResponse(String id, String name) {
        return new LaborResponse(id, name, "sbrubles " + name, new BigDecimal("100.00"), 1, LocalDateTime.now(), LocalDateTime.now(), true);
    }

    @Test
    @DisplayName("Deve retornar todos os serviços ativos")
    void findAll_ShouldReturnActiveLabors() {
        LaborEntity entity1 = createLaborEntity("1", "Labor 1", true);
        LaborEntity entity2 = createLaborEntity("2", "Labor 2", true);
        LaborResponse response1 = createLaborResponse("1", "Labor 1");
        LaborResponse response2 = createLaborResponse("2", "Labor 2");

        when(laborRepository.findByActiveTrue()).thenReturn(List.of(entity1, entity2));
        when(laborMapper.toResponse(entity1)).thenReturn(response1);
        when(laborMapper.toResponse(entity2)).thenReturn(response2);

        List<LaborResponse> result = laborService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Labor 1", result.get(0).name());
        assertEquals("Labor 2", result.get(1).name());
        verify(laborRepository, times(1)).findByActiveTrue();
        verify(laborMapper, times(2)).toResponse(any(LaborEntity.class));
    }

    @Test
    @DisplayName("Deve retornar um serviço pelo ID")
    void findById_ShouldReturnLaborResponse() {
        String id = "123";
        LaborEntity entity = createLaborEntity(id, "Test Labor", true);
        LaborResponse response = createLaborResponse(id, "Test Labor");

        when(laborRepository.findById(id)).thenReturn(Optional.of(entity));
        when(laborMapper.toResponse(entity)).thenReturn(response);

        LaborResponse result = laborService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Test Labor", result.name());
        verify(laborRepository, times(1)).findById(id);
        verify(laborMapper, times(1)).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o serviço não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(laborRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> laborService.findById(id));
        verify(laborRepository, times(1)).findById(id);
        verify(laborMapper, never()).toResponse(any(LaborEntity.class));
    }

    @Test
    @DisplayName("Deve criar um novo serviço com sucesso")
    void create_ShouldReturnCreatedLaborResponse() {
        LaborRequest request = createLaborRequest("New Labor");
        LaborEntity entity = createLaborEntity(null, "New Labor", true); // ID will be generated
        LaborEntity savedEntity = createLaborEntity("newId", "New Labor", true);
        LaborResponse response = createLaborResponse("newId", "New Labor");

        when(laborRepository.existsByName(request.name())).thenReturn(false);
        when(laborMapper.toEntity(request)).thenReturn(entity);
        when(laborRepository.save(entity)).thenReturn(savedEntity);
        when(laborMapper.toResponse(savedEntity)).thenReturn(response);

        LaborResponse result = laborService.create(request);

        assertNotNull(result);
        assertEquals("newId", result.id());
        assertEquals("New Labor", result.name());
        verify(laborRepository, times(1)).existsByName(request.name());
        verify(laborMapper, times(1)).toEntity(request);
        verify(laborRepository, times(1)).save(entity);
        verify(laborMapper, times(1)).toResponse(savedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar serviço com nome já existente")
    void create_ShouldThrowDomainException_WhenNameExists() {
        LaborRequest request = createLaborRequest("Existing Labor");
        when(laborRepository.existsByName(request.name())).thenReturn(true);

        assertThrows(DomainException.class, () -> laborService.create(request));
        verify(laborRepository, times(1)).existsByName(request.name());
        verify(laborMapper, never()).toEntity(any(LaborRequest.class));
        verify(laborRepository, never()).save(any(LaborEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar um serviço existente com sucesso")
    void update_ShouldReturnUpdatedLaborResponse() {
        String id = "123";
        LaborRequest request = createLaborRequest("Updated Labor Name");
        LaborEntity existingEntity = createLaborEntity(id, "Original Labor Name", true);
        LaborEntity updatedEntity = createLaborEntity(id, "Updated Labor Name", true);
        LaborResponse response = createLaborResponse(id, "Updated Labor Name");

        when(laborRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(laborRepository.existsByName(request.name())).thenReturn(false); // Name changed and not existing
        doNothing().when(laborMapper).updateEntity(existingEntity, request);
        when(laborRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(laborMapper.toResponse(updatedEntity)).thenReturn(response);

        LaborResponse result = laborService.update(id, request);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Updated Labor Name", result.name());
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, times(1)).existsByName(request.name());
        verify(laborMapper, times(1)).updateEntity(existingEntity, request);
        verify(laborRepository, times(1)).save(existingEntity);
        verify(laborMapper, times(1)).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar serviço com nome já existente")
    void update_ShouldThrowDomainException_WhenNameExists() {
        String id = "123";
        LaborRequest request = createLaborRequest("Existing Labor Name");
        LaborEntity existingEntity = createLaborEntity(id, "Original Labor Name", true);

        when(laborRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(laborRepository.existsByName(request.name())).thenReturn(true);

        assertThrows(DomainException.class, () -> laborService.update(id, request));
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, times(1)).existsByName(request.name());
        verify(laborMapper, never()).updateEntity(any(LaborEntity.class), any(LaborRequest.class));
        verify(laborRepository, never()).save(any(LaborEntity.class));
    }

    @Test
    @DisplayName("Deve desativar um serviço ao invés de deletar fisicamente")
    void delete_ShouldDeactivateLabor() {
        String id = "123";
        LaborEntity entity = createLaborEntity(id, "Labor to Delete", true);
        LaborEntity deactivatedEntity = createLaborEntity(id, "Labor to Delete", false);

        when(laborRepository.findById(id)).thenReturn(Optional.of(entity));
        when(laborRepository.save(entity)).thenReturn(deactivatedEntity);

        laborService.delete(id);

        assertFalse(entity.getActive()); // Verify that the entity's active status was set to false
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar desativar serviço inexistente")
    void delete_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(laborRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> laborService.delete(id));
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, never()).save(any(LaborEntity.class));
    }
}