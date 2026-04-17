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

    @Test
    @DisplayName("Deve retornar todos os clientes ativos")
    void findAll_ShouldReturnActiveCustomers() {
        CustomerEntity entity1 = createCustomerEntity("1", "111.111.111-11", true);
        CustomerEntity entity2 = createCustomerEntity("2", "222.222.222-22", true);
        CustomerResponse response1 = createCustomerResponse("1", "111.111.111-11");
        CustomerResponse response2 = createCustomerResponse("2", "222.222.222-22");

        when(customerRepository.findByActiveTrue()).thenReturn(List.of(entity1, entity2));
        when(customerMapper.toResponse(entity1)).thenReturn(response1);
        when(customerMapper.toResponse(entity2)).thenReturn(response2);

        List<CustomerResponse> result = customerService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("111.111.111-11", result.get(0).document());
        assertEquals("222.222.222-22", result.get(1).document());
        verify(customerRepository, times(1)).findByActiveTrue();
        verify(customerMapper, times(2)).toResponse(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Deve retornar um cliente pelo ID")
    void findById_ShouldReturnCustomerResponse() {
        String id = "123";
        CustomerEntity entity = createCustomerEntity(id, "123.456.789-09", true);
        CustomerResponse response = createCustomerResponse(id, "123.456.789-09");

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(response);

        CustomerResponse result = customerService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("123.456.789-09", result.document());
        verify(customerRepository, times(1)).findById(id);
        verify(customerMapper, times(1)).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o cliente não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.findById(id));
        verify(customerRepository, times(1)).findById(id);
        verify(customerMapper, never()).toResponse(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Deve retornar um cliente pelo documento")
    void findByDocument_ShouldReturnCustomerResponse() {
        String document = "123.456.789-09";
        CustomerEntity entity = createCustomerEntity("123", document, true);
        CustomerResponse response = createCustomerResponse("123", document);

        when(customerRepository.findByDocument(document)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(response);

        CustomerResponse result = customerService.findByDocument(document);

        assertNotNull(result);
        assertEquals(document, result.document());
        verify(customerRepository, times(1)).findByDocument(document);
        verify(customerMapper, times(1)).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o cliente não for encontrado pelo documento")
    void findByDocument_ShouldThrowNotFoundException() {
        String document = "000.000.000-00";
        when(customerRepository.findByDocument(document)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.findByDocument(document));
        verify(customerRepository, times(1)).findByDocument(document);
        verify(customerMapper, never()).toResponse(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Deve criar um novo cliente com sucesso")
    void create_ShouldReturnCreatedCustomerResponse() {
        CustomerRequest request = createCustomerRequest("123.456.789-09");
        CustomerEntity entity = createCustomerEntity(null, "123.456.789-09", true);
        CustomerEntity savedEntity = createCustomerEntity("newId", "123.456.789-09", true);
        CustomerResponse response = createCustomerResponse("newId", "123.456.789-09");

        when(customerRepository.existsByDocument(request.document())).thenReturn(false);
        when(customerMapper.toEntity(request)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(savedEntity);
        when(customerMapper.toResponse(savedEntity)).thenReturn(response);

        CustomerResponse result = customerService.create(request);

        assertNotNull(result);
        assertEquals("newId", result.id());
        assertEquals("123.456.789-09", result.document());
        verify(customerRepository, times(1)).existsByDocument(request.document());
        verify(customerMapper, times(1)).toEntity(request);
        verify(customerRepository, times(1)).save(entity);
        verify(customerMapper, times(1)).toResponse(savedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar cliente com documento já existente")
    void create_ShouldThrowDomainException_WhenDocumentExists() {
        CustomerRequest request = createCustomerRequest("123.456.789-09");
        when(customerRepository.existsByDocument(request.document())).thenReturn(true);

        assertThrows(DomainException.class, () -> customerService.create(request));
        verify(customerRepository, times(1)).existsByDocument(request.document());
        verify(customerMapper, never()).toEntity(any(CustomerRequest.class));
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar um cliente existente com sucesso")
    void update_ShouldReturnUpdatedCustomerResponse() {
        String id = "123";
        CustomerRequest request = createCustomerRequest("999.999.999-99");
        CustomerEntity existingEntity = createCustomerEntity(id, "123.456.789-09", true);
        CustomerEntity updatedEntity = createCustomerEntity(id, "999.999.999-99", true);
        CustomerResponse response = createCustomerResponse(id, "999.999.999-99");

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(customerRepository.existsByDocument(request.document())).thenReturn(false);
        doNothing().when(customerMapper).updateEntity(existingEntity, request);
        when(customerRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(customerMapper.toResponse(updatedEntity)).thenReturn(response);

        CustomerResponse result = customerService.update(id, request);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("999.999.999-99", result.document());
        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, times(1)).existsByDocument(request.document());
        verify(customerMapper, times(1)).updateEntity(existingEntity, request);
        verify(customerRepository, times(1)).save(existingEntity);
        verify(customerMapper, times(1)).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar cliente com documento já existente")
    void update_ShouldThrowDomainException_WhenDocumentExists() {
        String id = "123";
        CustomerRequest request = createCustomerRequest("999.999.999-99");
        CustomerEntity existingEntity = createCustomerEntity(id, "123.456.789-09", true);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(customerRepository.existsByDocument(request.document())).thenReturn(true);

        assertThrows(DomainException.class, () -> customerService.update(id, request));
        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, times(1)).existsByDocument(request.document());
        verify(customerMapper, never()).updateEntity(any(CustomerEntity.class), any(CustomerRequest.class));
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Deve desativar um cliente ao invés de deletar fisicamente")
    void delete_ShouldDeactivateCustomer() {
        String id = "123";
        CustomerEntity entity = createCustomerEntity(id, "123.456.789-09", true);
        CustomerEntity deactivatedEntity = createCustomerEntity(id, "123.456.789-09", false);

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerRepository.save(entity)).thenReturn(deactivatedEntity);

        customerService.delete(id);

        assertFalse(entity.getActive());
        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar desativar cliente inexistente")
    void delete_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.delete(id));
        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }
}