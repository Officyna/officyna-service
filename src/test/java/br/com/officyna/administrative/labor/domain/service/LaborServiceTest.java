package br.com.officyna.administrative.labor.domain.service;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.administrative.labor.domain.exception.LaborBusinessException;
import br.com.officyna.administrative.labor.domain.exception.LaborNotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
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
    private LaborMonitoringService laborMonitoringService;

    @InjectMocks
    private LaborService laborService;

    private Labor createLaborEntity(String id, String name, boolean active) {
        return Labor.builder()
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

    @Test
    @DisplayName("Deve retornar todos os serviços ativos")
    void findAll_ShouldReturnActiveLabors() {
        Labor entity1 = createLaborEntity("1", "Labor 1", true);
        Labor entity2 = createLaborEntity("2", "Labor 2", true);

        when(laborRepository.findByActiveTrue()).thenReturn(List.of(entity1, entity2));

        List<Labor> result = laborService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(laborRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Deve retornar um serviço pelo ID")
    void findById_ShouldReturnLabor() {
        String id = "123";
        Labor entity = createLaborEntity(id, "Test Labor", true);

        when(laborRepository.findById(id)).thenReturn(Optional.of(entity));

        Labor result = laborService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(laborRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o serviço não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(laborRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LaborNotFoundException.class, () -> laborService.findById(id));
        verify(laborRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve criar um novo serviço e inicializar o monitoramento")
    void create_ShouldReturnCreatedLabor() {
        Labor incoming = createLaborEntity(null, "New Labor", true);
        Labor savedEntity = createLaborEntity("newId", "New Labor", true);

        when(laborRepository.existsByName("New Labor")).thenReturn(false);
        when(laborRepository.save(incoming)).thenReturn(savedEntity);

        Labor result = laborService.create(incoming);

        assertNotNull(result);
        assertEquals("newId", result.getId());
        verify(laborRepository, times(1)).existsByName("New Labor");
        verify(laborRepository, times(1)).save(incoming);
        verify(laborMonitoringService, times(1))
                .initializeFromEstimate("newId", "New Labor", savedEntity.getDescription(), 1);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar serviço com nome já existente")
    void create_ShouldThrowDomainException_WhenNameExists() {
        Labor incoming = createLaborEntity(null, "Existing Labor", true);
        when(laborRepository.existsByName("Existing Labor")).thenReturn(true);

        assertThrows(LaborBusinessException.class, () -> laborService.create(incoming));
        verify(laborRepository, times(1)).existsByName("Existing Labor");
        verify(laborRepository, never()).save(any(Labor.class));
        verify(laborMonitoringService, never()).initializeFromEstimate(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve atualizar um serviço existente com sucesso")
    void update_ShouldReturnUpdatedLabor() {
        String id = "123";
        Labor existingEntity = createLaborEntity(id, "Original Labor Name", true);
        Labor changes = createLaborEntity(null, "Updated Labor Name", true);

        when(laborRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(laborRepository.existsByName("Updated Labor Name")).thenReturn(false);
        when(laborRepository.save(existingEntity)).thenReturn(existingEntity);

        Labor result = laborService.update(id, changes);

        assertNotNull(result);
        assertEquals("Updated Labor Name", existingEntity.getName());
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, times(1)).existsByName("Updated Labor Name");
        verify(laborRepository, times(1)).save(existingEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar serviço com nome já existente")
    void update_ShouldThrowDomainException_WhenNameExists() {
        String id = "123";
        Labor existingEntity = createLaborEntity(id, "Original Labor Name", true);
        Labor changes = createLaborEntity(null, "Existing Labor Name", true);

        when(laborRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(laborRepository.existsByName("Existing Labor Name")).thenReturn(true);

        assertThrows(LaborBusinessException.class, () -> laborService.update(id, changes));
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, times(1)).existsByName("Existing Labor Name");
        verify(laborRepository, never()).save(any(Labor.class));
    }

    @Test
    @DisplayName("Deve desativar um serviço ao invés de deletar fisicamente")
    void delete_ShouldDeactivateLabor() {
        String id = "123";
        Labor entity = createLaborEntity(id, "Labor to Delete", true);
        Labor deactivatedEntity = createLaborEntity(id, "Labor to Delete", false);

        when(laborRepository.findById(id)).thenReturn(Optional.of(entity));
        when(laborRepository.save(entity)).thenReturn(deactivatedEntity);

        laborService.delete(id);

        assertFalse(entity.getActive());
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar desativar serviço inexistente")
    void delete_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(laborRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LaborNotFoundException.class, () -> laborService.delete(id));
        verify(laborRepository, times(1)).findById(id);
        verify(laborRepository, never()).save(any(Labor.class));
    }
}