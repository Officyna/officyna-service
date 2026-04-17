package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.repository.SupplyRepository;
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
class SupplyServiceTest {

    @Mock
    private SupplyRepository supplyRepository;

    @Mock
    private SupplyMapper supplyMapper;

    @InjectMocks
    private SupplyService supplyService;

    private SupplyEntity createSupplyEntity(String id, String name, SupplyType type, boolean active) {
        return SupplyEntity.builder()
                .id(id)
                .name(name)
                .description("Descrição de " + name)
                .type(type)
                .purchasePrice(new BigDecimal("45.90"))
                .salePrice(new BigDecimal("59.67"))
                .stockQuantity(50)
                .minimumQuantity(10)
                .reservedQuantity(3)
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private SupplyRequest createSupplyRequest(String name) {
        return new SupplyRequest(name, "Descrição de " + name, SupplyType.SUPPLY,
                new BigDecimal("45.90"), new BigDecimal("30.00"), 50, 10, 3);
    }

    private SupplyResponse createSupplyResponse(String id, String name) {
        return new SupplyResponse(id, name, "Descrição de " + name, SupplyType.SUPPLY,
                new BigDecimal("45.90"), new BigDecimal("59.67"), new BigDecimal("30.00"),
                50, 10, 3, 47, false, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve retornar todos os insumos e peças ativos")
    void findAll_ShouldReturnActiveSupplies() {
        SupplyEntity entity1 = createSupplyEntity("1", "Óleo Motor", SupplyType.SUPPLY, true);
        SupplyEntity entity2 = createSupplyEntity("2", "Pastilha de Freio", SupplyType.PART, true);
        SupplyResponse response1 = createSupplyResponse("1", "Óleo Motor");
        SupplyResponse response2 = createSupplyResponse("2", "Pastilha de Freio");

        when(supplyRepository.findByActiveTrue()).thenReturn(List.of(entity1, entity2));
        when(supplyMapper.toResponse(entity1)).thenReturn(response1);
        when(supplyMapper.toResponse(entity2)).thenReturn(response2);

        List<SupplyResponse> result = supplyService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Óleo Motor", result.get(0).name());
        assertEquals("Pastilha de Freio", result.get(1).name());
        verify(supplyRepository, times(1)).findByActiveTrue();
        verify(supplyMapper, times(2)).toResponse(any(SupplyEntity.class));
    }

    @Test
    @DisplayName("Deve retornar insumos e peças filtrados por tipo")
    void findByType_ShouldReturnSuppliesByType() {
        SupplyEntity entity = createSupplyEntity("1", "Óleo Motor", SupplyType.SUPPLY, true);
        SupplyResponse response = createSupplyResponse("1", "Óleo Motor");

        when(supplyRepository.findByActiveTrueAndType(SupplyType.SUPPLY)).thenReturn(List.of(entity));
        when(supplyMapper.toResponse(entity)).thenReturn(response);

        List<SupplyResponse> result = supplyService.findByType(SupplyType.SUPPLY);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Óleo Motor", result.get(0).name());
        verify(supplyRepository, times(1)).findByActiveTrueAndType(SupplyType.SUPPLY);
        verify(supplyMapper, times(1)).toResponse(any(SupplyEntity.class));
    }

    @Test
    @DisplayName("Deve retornar um insumo pelo ID")
    void findById_ShouldReturnSupplyResponse() {
        String id = "123";
        SupplyEntity entity = createSupplyEntity(id, "Óleo Motor", SupplyType.SUPPLY, true);
        SupplyResponse response = createSupplyResponse(id, "Óleo Motor");

        when(supplyRepository.findById(id)).thenReturn(Optional.of(entity));
        when(supplyMapper.toResponse(entity)).thenReturn(response);

        SupplyResponse result = supplyService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Óleo Motor", result.name());
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyMapper, times(1)).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o insumo não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(supplyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> supplyService.findById(id));
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyMapper, never()).toResponse(any(SupplyEntity.class));
    }

    @Test
    @DisplayName("Deve criar um novo insumo com sucesso")
    void create_ShouldReturnCreatedSupplyResponse() {
        SupplyRequest request = createSupplyRequest("Novo Insumo");
        SupplyEntity entity = createSupplyEntity(null, "Novo Insumo", SupplyType.SUPPLY, true);
        SupplyEntity savedEntity = createSupplyEntity("newId", "Novo Insumo", SupplyType.SUPPLY, true);
        SupplyResponse response = createSupplyResponse("newId", "Novo Insumo");

        when(supplyRepository.existsByName(request.name())).thenReturn(false);
        when(supplyMapper.toEntity(request)).thenReturn(entity);
        when(supplyRepository.save(entity)).thenReturn(savedEntity);
        when(supplyMapper.toResponse(savedEntity)).thenReturn(response);

        SupplyResponse result = supplyService.create(request);

        assertNotNull(result);
        assertEquals("newId", result.id());
        assertEquals("Novo Insumo", result.name());
        verify(supplyRepository, times(1)).existsByName(request.name());
        verify(supplyMapper, times(1)).toEntity(request);
        verify(supplyRepository, times(1)).save(entity);
        verify(supplyMapper, times(1)).toResponse(savedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar insumo com nome já existente")
    void create_ShouldThrowDomainException_WhenNameExists() {
        SupplyRequest request = createSupplyRequest("Insumo Existente");
        when(supplyRepository.existsByName(request.name())).thenReturn(true);

        assertThrows(DomainException.class, () -> supplyService.create(request));
        verify(supplyRepository, times(1)).existsByName(request.name());
        verify(supplyMapper, never()).toEntity(any(SupplyRequest.class));
        verify(supplyRepository, never()).save(any(SupplyEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar um insumo existente com sucesso")
    void update_ShouldReturnUpdatedSupplyResponse() {
        String id = "123";
        SupplyRequest request = createSupplyRequest("Insumo Atualizado");
        SupplyEntity existingEntity = createSupplyEntity(id, "Insumo Original", SupplyType.SUPPLY, true);
        SupplyEntity updatedEntity = createSupplyEntity(id, "Insumo Atualizado", SupplyType.SUPPLY, true);
        SupplyResponse response = createSupplyResponse(id, "Insumo Atualizado");

        when(supplyRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(supplyRepository.existsByName(request.name())).thenReturn(false);
        doNothing().when(supplyMapper).updateEntity(existingEntity, request);
        when(supplyRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(supplyMapper.toResponse(updatedEntity)).thenReturn(response);

        SupplyResponse result = supplyService.update(id, request);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Insumo Atualizado", result.name());
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyRepository, times(1)).existsByName(request.name());
        verify(supplyMapper, times(1)).updateEntity(existingEntity, request);
        verify(supplyRepository, times(1)).save(existingEntity);
        verify(supplyMapper, times(1)).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar insumo com nome já existente")
    void update_ShouldThrowDomainException_WhenNameExists() {
        String id = "123";
        SupplyRequest request = createSupplyRequest("Nome Já Existente");
        SupplyEntity existingEntity = createSupplyEntity(id, "Insumo Original", SupplyType.SUPPLY, true);

        when(supplyRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(supplyRepository.existsByName(request.name())).thenReturn(true);

        assertThrows(DomainException.class, () -> supplyService.update(id, request));
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyRepository, times(1)).existsByName(request.name());
        verify(supplyMapper, never()).updateEntity(any(SupplyEntity.class), any(SupplyRequest.class));
        verify(supplyRepository, never()).save(any(SupplyEntity.class));
    }

    @Test
    @DisplayName("Deve desativar um insumo ao invés de deletar fisicamente")
    void delete_ShouldDeactivateSupply() {
        String id = "123";
        SupplyEntity entity = createSupplyEntity(id, "Insumo para Deletar", SupplyType.SUPPLY, true);
        SupplyEntity deactivatedEntity = createSupplyEntity(id, "Insumo para Deletar", SupplyType.SUPPLY, false);

        when(supplyRepository.findById(id)).thenReturn(Optional.of(entity));
        when(supplyRepository.save(entity)).thenReturn(deactivatedEntity);

        supplyService.delete(id);

        assertFalse(entity.getActive());
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar desativar insumo inexistente")
    void delete_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(supplyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> supplyService.delete(id));
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyRepository, never()).save(any(SupplyEntity.class));
    }
}