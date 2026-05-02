package br.com.officyna.administrative.customer.domain.service;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.AddressEntity;
import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.CustomerType;
import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.repository.CustomerRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
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

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    // Documentos normalizados (sem formatação) — representam dados no banco
    private static final String CPF_NORMALIZED   = "12345678909";
    private static final String CPF_ALT_NORMALIZED = "52998224725";
    // Documento formatado — representa entrada do usuário
    private static final String CPF_FORMATTED    = "123.456.789-09";
    private static final String CPF_ALT_FORMATTED = "529.982.247-25";

    private CustomerEntity createCustomerEntity(String id, String document, boolean active) {
        return CustomerEntity.builder()
                .id(id)
                .name("João Silva")
                .document(document)
                .type(CustomerType.INDIVIDUAL)
                .email("joao@email.com")
                .phone("99999-9999")
                .areaCode("11")
                .countryCode("+55")
                .address(AddressEntity.builder()
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

    private CustomerRequest createCustomerRequest(String document) {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerRequest("João Silva", document, CustomerType.INDIVIDUAL, "joao@email.com", "99999-9999", "11", "+55", address);
    }

    private CustomerResponse createCustomerResponse(String id, String document) {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerResponse(id, "João Silva", document, CustomerType.INDIVIDUAL, "joao@email.com", "99999-9999", "11", "+55", address, true, LocalDateTime.now());
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar todos os clientes ativos")
    void findAll_ShouldReturnActiveCustomers() {
        CustomerEntity entity1 = createCustomerEntity("1", CPF_NORMALIZED, true);
        CustomerEntity entity2 = createCustomerEntity("2", CPF_ALT_NORMALIZED, true);
        CustomerResponse response1 = createCustomerResponse("1", CPF_NORMALIZED);
        CustomerResponse response2 = createCustomerResponse("2", CPF_ALT_NORMALIZED);

        when(customerRepository.findByActiveTrue()).thenReturn(List.of(entity1, entity2));
        when(customerMapper.toResponse(entity1)).thenReturn(response1);
        when(customerMapper.toResponse(entity2)).thenReturn(response2);

        List<CustomerResponse> result = customerService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository).findByActiveTrue();
        verify(customerMapper, times(2)).toResponse(any(CustomerEntity.class));
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar um cliente pelo ID")
    void findById_ShouldReturnCustomerResponse() {
        String id = "123";
        CustomerEntity entity = createCustomerEntity(id, CPF_NORMALIZED, true);
        CustomerResponse response = createCustomerResponse(id, CPF_NORMALIZED);

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(response);

        CustomerResponse result = customerService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        verify(customerRepository).findById(id);
        verify(customerMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o cliente não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.findById(id));
        verify(customerRepository).findById(id);
        verify(customerMapper, never()).toResponse(any(CustomerEntity.class));
    }

    // ─── findByDocument ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar um cliente pelo documento normalizado")
    void findByDocument_ShouldReturnCustomerResponse() {
        CustomerEntity entity = createCustomerEntity("123", CPF_NORMALIZED, true);
        CustomerResponse response = createCustomerResponse("123", CPF_NORMALIZED);

        when(customerRepository.findByDocument(CPF_NORMALIZED)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(response);

        CustomerResponse result = customerService.findByDocument(CPF_NORMALIZED);

        assertNotNull(result);
        assertEquals(CPF_NORMALIZED, result.document());
        verify(customerRepository).findByDocument(CPF_NORMALIZED);
        verify(customerMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve normalizar documento formatado antes de buscar no repositório")
    void findByDocument_ShouldNormalizeFormattedDocument() {
        CustomerEntity entity = createCustomerEntity("123", CPF_NORMALIZED, true);
        CustomerResponse response = createCustomerResponse("123", CPF_NORMALIZED);

        when(customerRepository.findByDocument(CPF_NORMALIZED)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(response);

        customerService.findByDocument(CPF_FORMATTED); // entrada formatada

        // repositório deve ser chamado com o documento normalizado
        verify(customerRepository).findByDocument(CPF_NORMALIZED);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o cliente não for encontrado pelo documento")
    void findByDocument_ShouldThrowNotFoundException() {
        when(customerRepository.findByDocument(CPF_NORMALIZED)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.findByDocument(CPF_FORMATTED));
        verify(customerRepository).findByDocument(CPF_NORMALIZED);
        verify(customerMapper, never()).toResponse(any(CustomerEntity.class));
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar um novo cliente com sucesso")
    void create_ShouldReturnCreatedCustomerResponse() {
        CustomerRequest request = createCustomerRequest(CPF_FORMATTED);
        CustomerEntity entity = createCustomerEntity(null, CPF_NORMALIZED, true);
        CustomerEntity savedEntity = createCustomerEntity("newId", CPF_NORMALIZED, true);
        CustomerResponse response = createCustomerResponse("newId", CPF_NORMALIZED);

        when(customerRepository.existsByDocument(CPF_NORMALIZED)).thenReturn(false);
        when(customerMapper.toEntity(request)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(savedEntity);
        when(customerMapper.toResponse(savedEntity)).thenReturn(response);

        CustomerResponse result = customerService.create(request);

        assertNotNull(result);
        assertEquals("newId", result.id());
        // verifica que a checagem de duplicidade usa o documento normalizado
        verify(customerRepository).existsByDocument(CPF_NORMALIZED);
        verify(customerMapper).toEntity(request);
        verify(customerRepository).save(entity);
        verify(customerMapper).toResponse(savedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar cliente com documento já existente")
    void create_ShouldThrowDomainException_WhenDocumentExists() {
        CustomerRequest request = createCustomerRequest(CPF_FORMATTED);
        when(customerRepository.existsByDocument(CPF_NORMALIZED)).thenReturn(true);

        assertThrows(DomainException.class, () -> customerService.create(request));
        verify(customerRepository).existsByDocument(CPF_NORMALIZED);
        verify(customerMapper, never()).toEntity(any(CustomerRequest.class));
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar um cliente existente com sucesso quando o documento muda")
    void update_ShouldReturnUpdatedCustomerResponse() {
        String id = "123";
        CustomerRequest request = createCustomerRequest(CPF_ALT_FORMATTED);
        // entidade do banco já tem documento normalizado
        CustomerEntity existingEntity = createCustomerEntity(id, CPF_NORMALIZED, true);
        CustomerEntity updatedEntity = createCustomerEntity(id, CPF_ALT_NORMALIZED, true);
        CustomerResponse response = createCustomerResponse(id, CPF_ALT_NORMALIZED);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(customerRepository.existsByDocument(CPF_ALT_NORMALIZED)).thenReturn(false);
        doNothing().when(customerMapper).updateEntity(existingEntity, request);
        when(customerRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(customerMapper.toResponse(updatedEntity)).thenReturn(response);

        CustomerResponse result = customerService.update(id, request);

        assertNotNull(result);
        assertEquals(id, result.id());
        verify(customerRepository).findById(id);
        verify(customerRepository).existsByDocument(CPF_ALT_NORMALIZED);
        verify(customerMapper).updateEntity(existingEntity, request);
        verify(customerRepository).save(existingEntity);
        verify(customerMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Deve atualizar sem verificar duplicidade quando o documento normalizado não muda")
    void update_ShouldNotCheckDuplicate_WhenNormalizedDocumentUnchanged() {
        String id = "123";
        // usuário envia o documento formatado, mas o banco tem o normalizado
        CustomerRequest request = createCustomerRequest(CPF_FORMATTED);
        CustomerEntity existingEntity = createCustomerEntity(id, CPF_NORMALIZED, true);
        CustomerResponse response = createCustomerResponse(id, CPF_NORMALIZED);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(customerMapper).updateEntity(existingEntity, request);
        when(customerRepository.save(existingEntity)).thenReturn(existingEntity);
        when(customerMapper.toResponse(existingEntity)).thenReturn(response);

        customerService.update(id, request);

        // documento normalizado é igual ao do banco: não deve verificar duplicidade
        verify(customerRepository, never()).existsByDocument(any());
        verify(customerMapper).updateEntity(existingEntity, request);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar com documento já existente em outro cliente")
    void update_ShouldThrowDomainException_WhenDocumentExistsInAnotherCustomer() {
        String id = "123";
        CustomerRequest request = createCustomerRequest(CPF_ALT_FORMATTED);
        CustomerEntity existingEntity = createCustomerEntity(id, CPF_NORMALIZED, true);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(customerRepository.existsByDocument(CPF_ALT_NORMALIZED)).thenReturn(true);

        assertThrows(DomainException.class, () -> customerService.update(id, request));
        verify(customerRepository).findById(id);
        verify(customerRepository).existsByDocument(CPF_ALT_NORMALIZED);
        verify(customerMapper, never()).updateEntity(any(), any());
        verify(customerRepository, never()).save(any());
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve desativar um cliente ao invés de deletar fisicamente")
    void delete_ShouldDeactivateCustomer() {
        String id = "123";
        CustomerEntity entity = createCustomerEntity(id, CPF_NORMALIZED, true);
        CustomerEntity deactivatedEntity = createCustomerEntity(id, CPF_NORMALIZED, false);

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

        assertThrows(NotFoundException.class, () -> customerService.delete(id));
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }
}