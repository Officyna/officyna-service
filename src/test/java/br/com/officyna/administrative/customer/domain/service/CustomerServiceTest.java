package br.com.officyna.administrative.customer.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.administrative.customer.domain.exception.CustomerBusinessException;
import br.com.officyna.administrative.customer.domain.exception.CustomerNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // Documentos normalizados (sem formatação) — representam dados no banco
    private static final String CPF_NORMALIZED   = "12345678909";
    private static final String CPF_ALT_NORMALIZED = "52998224725";
    // Documento formatado — representa entrada do usuário
    private static final String CPF_FORMATTED    = "123.456.789-09";
    private static final String CPF_ALT_FORMATTED = "529.982.247-25";

    private Customer createCustomerEntity(String id, String document, boolean active) {
        return Customer.builder()
                .id(id)
                .name("João Silva")
                .document(document)
                .type(CustomerType.INDIVIDUAL)
                .email("joao@email.com")
                .phone("99999-9999")
                .areaCode("11")
                .countryCode("+55")
                .address(Address.builder()
                        .street("Rua das Flores")
                        .number("100")
                        .neighborhood("Centro")
                        .city("São Paulo")
                        .state("SP")
                        .zipCode("01310-100")
                        .build())
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar todos os clientes ativos")
    void findAll_ShouldReturnActiveCustomers() {
        Customer entity1 = createCustomerEntity("1", CPF_NORMALIZED, true);
        Customer entity2 = createCustomerEntity("2", CPF_ALT_NORMALIZED, true);

        when(customerRepository.findByActiveTrue()).thenReturn(List.of(entity1, entity2));

        List<Customer> result = customerService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository).findByActiveTrue();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar um cliente pelo ID")
    void findById_ShouldReturnCustomer() {
        String id = "123";
        Customer entity = createCustomerEntity(id, CPF_NORMALIZED, true);

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));

        Customer result = customerService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(customerRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o cliente não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findById(id));
        verify(customerRepository).findById(id);
    }

    // ─── findByDocument ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar um cliente pelo documento normalizado")
    void findByDocument_ShouldReturnCustomer() {
        Customer entity = createCustomerEntity("123", CPF_NORMALIZED, true);

        when(customerRepository.findByDocument(CPF_NORMALIZED)).thenReturn(Optional.of(entity));

        Customer result = customerService.findByDocument(CPF_NORMALIZED);

        assertNotNull(result);
        assertEquals(CPF_NORMALIZED, result.getDocument());
        verify(customerRepository).findByDocument(CPF_NORMALIZED);
    }

    @Test
    @DisplayName("Deve normalizar documento formatado antes de buscar no repositório")
    void findByDocument_ShouldNormalizeFormattedDocument() {
        Customer entity = createCustomerEntity("123", CPF_NORMALIZED, true);

        when(customerRepository.findByDocument(CPF_NORMALIZED)).thenReturn(Optional.of(entity));

        customerService.findByDocument(CPF_FORMATTED); // entrada formatada

        // repositório deve ser chamado com o documento normalizado
        verify(customerRepository).findByDocument(CPF_NORMALIZED);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o cliente não for encontrado pelo documento")
    void findByDocument_ShouldThrowNotFoundException() {
        when(customerRepository.findByDocument(CPF_NORMALIZED)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findByDocument(CPF_FORMATTED));
        verify(customerRepository).findByDocument(CPF_NORMALIZED);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar um novo cliente normalizando o documento e ativando")
    void create_ShouldReturnCreatedCustomer() {
        // objeto de domínio já montado pelo adapter, com documento formatado
        Customer incoming = createCustomerEntity(null, CPF_FORMATTED, false);
        Customer savedEntity = createCustomerEntity("newId", CPF_NORMALIZED, true);

        when(customerRepository.existsByDocument(CPF_NORMALIZED)).thenReturn(false);
        when(customerRepository.save(incoming)).thenReturn(savedEntity);

        Customer result = customerService.create(incoming);

        assertNotNull(result);
        assertEquals("newId", result.getId());
        // documento normalizado e active definido pelo use case antes de salvar
        assertEquals(CPF_NORMALIZED, incoming.getDocument());
        assertTrue(incoming.getActive());
        verify(customerRepository).existsByDocument(CPF_NORMALIZED);
        verify(customerRepository).save(incoming);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar cliente com documento já existente")
    void create_ShouldThrowDomainException_WhenDocumentExists() {
        Customer incoming = createCustomerEntity(null, CPF_FORMATTED, false);
        when(customerRepository.existsByDocument(CPF_NORMALIZED)).thenReturn(true);

        assertThrows(CustomerBusinessException.class, () -> customerService.create(incoming));
        verify(customerRepository).existsByDocument(CPF_NORMALIZED);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar um cliente existente com sucesso quando o documento muda")
    void update_ShouldReturnUpdatedCustomer() {
        String id = "123";
        // entidade do banco já tem documento normalizado
        Customer existingEntity = createCustomerEntity(id, CPF_NORMALIZED, true);
        Customer changes = createCustomerEntity(null, CPF_ALT_FORMATTED, true);
        changes.setName("Maria Souza");

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(customerRepository.existsByDocument(CPF_ALT_NORMALIZED)).thenReturn(false);
        when(customerRepository.save(existingEntity)).thenReturn(existingEntity);

        Customer result = customerService.update(id, changes);

        assertNotNull(result);
        assertEquals(id, result.getId());
        // campos aplicados sobre a entidade existente, com documento normalizado
        assertEquals("Maria Souza", existingEntity.getName());
        assertEquals(CPF_ALT_NORMALIZED, existingEntity.getDocument());
        verify(customerRepository).findById(id);
        verify(customerRepository).existsByDocument(CPF_ALT_NORMALIZED);
        verify(customerRepository).save(existingEntity);
    }

    @Test
    @DisplayName("Deve atualizar sem verificar duplicidade quando o documento normalizado não muda")
    void update_ShouldNotCheckDuplicate_WhenNormalizedDocumentUnchanged() {
        String id = "123";
        Customer existingEntity = createCustomerEntity(id, CPF_NORMALIZED, true);
        // adapter envia o documento formatado, mas o banco tem o normalizado
        Customer changes = createCustomerEntity(null, CPF_FORMATTED, true);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(customerRepository.save(existingEntity)).thenReturn(existingEntity);

        customerService.update(id, changes);

        // documento normalizado é igual ao do banco: não deve verificar duplicidade
        verify(customerRepository, never()).existsByDocument(any());
        verify(customerRepository).save(existingEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar com documento já existente em outro cliente")
    void update_ShouldThrowDomainException_WhenDocumentExistsInAnotherCustomer() {
        String id = "123";
        Customer existingEntity = createCustomerEntity(id, CPF_NORMALIZED, true);
        Customer changes = createCustomerEntity(null, CPF_ALT_FORMATTED, true);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(customerRepository.existsByDocument(CPF_ALT_NORMALIZED)).thenReturn(true);

        assertThrows(CustomerBusinessException.class, () -> customerService.update(id, changes));
        verify(customerRepository).findById(id);
        verify(customerRepository).existsByDocument(CPF_ALT_NORMALIZED);
        verify(customerRepository, never()).save(any());
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve desativar um cliente ao invés de deletar fisicamente")
    void delete_ShouldDeactivateCustomer() {
        String id = "123";
        Customer entity = createCustomerEntity(id, CPF_NORMALIZED, true);
        Customer deactivatedEntity = createCustomerEntity(id, CPF_NORMALIZED, false);

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerRepository.save(entity)).thenReturn(deactivatedEntity);

        customerService.delete(id);

        assertFalse(entity.getActive());
        verify(customerRepository).findById(id);
        verify(customerRepository).save(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar desativar cliente inexistente")
    void delete_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.delete(id));
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}