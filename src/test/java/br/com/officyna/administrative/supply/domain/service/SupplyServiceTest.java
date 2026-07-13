package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.administrative.supply.domain.exception.SupplyBusinessException;
import br.com.officyna.administrative.supply.domain.exception.SupplyNotFoundException;
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

    @InjectMocks
    private SupplyService supplyService;

    private Supply createSupplyEntity(String id, String name, SupplyType type, boolean active) {
        return Supply.builder()
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

    @Test
    @DisplayName("Deve retornar todos os insumos e peças ativos")
    void findAll_ShouldReturnActiveSupplies() {
        Supply entity1 = createSupplyEntity("1", "Óleo Motor", SupplyType.SUPPLY, true);
        Supply entity2 = createSupplyEntity("2", "Pastilha de Freio", SupplyType.PART, true);

        when(supplyRepository.findByActiveTrue()).thenReturn(List.of(entity1, entity2));

        List<Supply> result = supplyService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(supplyRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Deve retornar insumos e peças filtrados por tipo")
    void findByType_ShouldReturnSuppliesByType() {
        Supply entity = createSupplyEntity("1", "Óleo Motor", SupplyType.SUPPLY, true);

        when(supplyRepository.findByActiveTrueAndType(SupplyType.SUPPLY)).thenReturn(List.of(entity));

        List<Supply> result = supplyService.findByType(SupplyType.SUPPLY);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supplyRepository, times(1)).findByActiveTrueAndType(SupplyType.SUPPLY);
    }

    @Test
    @DisplayName("Deve retornar um insumo pelo ID")
    void findById_ShouldReturnSupply() {
        String id = "123";
        Supply entity = createSupplyEntity(id, "Óleo Motor", SupplyType.SUPPLY, true);

        when(supplyRepository.findById(id)).thenReturn(Optional.of(entity));

        Supply result = supplyService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(supplyRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o insumo não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(supplyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(SupplyNotFoundException.class, () -> supplyService.findById(id));
        verify(supplyRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve criar um novo insumo com sucesso")
    void create_ShouldReturnCreatedSupply() {
        Supply incoming = createSupplyEntity(null, "Novo Insumo", SupplyType.SUPPLY, true);
        Supply savedEntity = createSupplyEntity("newId", "Novo Insumo", SupplyType.SUPPLY, true);

        when(supplyRepository.existsByName("Novo Insumo")).thenReturn(false);
        when(supplyRepository.save(incoming)).thenReturn(savedEntity);

        Supply result = supplyService.create(incoming);

        assertNotNull(result);
        assertEquals("newId", result.getId());
        verify(supplyRepository, times(1)).existsByName("Novo Insumo");
        verify(supplyRepository, times(1)).save(incoming);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar insumo com nome já existente")
    void create_ShouldThrowDomainException_WhenNameExists() {
        Supply incoming = createSupplyEntity(null, "Insumo Existente", SupplyType.SUPPLY, true);
        when(supplyRepository.existsByName("Insumo Existente")).thenReturn(true);

        assertThrows(SupplyBusinessException.class, () -> supplyService.create(incoming));
        verify(supplyRepository, times(1)).existsByName("Insumo Existente");
        verify(supplyRepository, never()).save(any(Supply.class));
    }

    @Test
    @DisplayName("Deve atualizar um insumo existente com sucesso")
    void update_ShouldReturnUpdatedSupply() {
        String id = "123";
        Supply existingEntity = createSupplyEntity(id, "Insumo Original", SupplyType.SUPPLY, true);
        Supply changes = createSupplyEntity(null, "Insumo Atualizado", SupplyType.SUPPLY, true);

        when(supplyRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(supplyRepository.existsByName("Insumo Atualizado")).thenReturn(false);
        when(supplyRepository.save(existingEntity)).thenReturn(existingEntity);

        Supply result = supplyService.update(id, changes);

        assertNotNull(result);
        assertEquals("Insumo Atualizado", existingEntity.getName());
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyRepository, times(1)).existsByName("Insumo Atualizado");
        verify(supplyRepository, times(1)).save(existingEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar insumo com nome já existente")
    void update_ShouldThrowDomainException_WhenNameExists() {
        String id = "123";
        Supply existingEntity = createSupplyEntity(id, "Insumo Original", SupplyType.SUPPLY, true);
        Supply changes = createSupplyEntity(null, "Nome Já Existente", SupplyType.SUPPLY, true);

        when(supplyRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(supplyRepository.existsByName("Nome Já Existente")).thenReturn(true);

        assertThrows(SupplyBusinessException.class, () -> supplyService.update(id, changes));
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyRepository, times(1)).existsByName("Nome Já Existente");
        verify(supplyRepository, never()).save(any(Supply.class));
    }

    @Test
    @DisplayName("Deve desativar um insumo ao invés de deletar fisicamente")
    void delete_ShouldDeactivateSupply() {
        String id = "123";
        Supply entity = createSupplyEntity(id, "Insumo para Deletar", SupplyType.SUPPLY, true);
        Supply deactivatedEntity = createSupplyEntity(id, "Insumo para Deletar", SupplyType.SUPPLY, false);

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

        assertThrows(SupplyNotFoundException.class, () -> supplyService.delete(id));
        verify(supplyRepository, times(1)).findById(id);
        verify(supplyRepository, never()).save(any(Supply.class));
    }
}