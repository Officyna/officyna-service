package br.com.officyna.administrative.customer.domain.controller;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.domain.presenter.CustomerPresenter;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerAdapterTest {

    @Mock
    private CustomerService service;

    @Mock
    private CustomerMapper mapper;

    @Mock
    private CustomerPresenter presenter;

    @InjectMocks
    private CustomerControllerAdapter adapter;

    private CustomerRequest buildRequest() {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerRequest("João Silva", "123.456.789-09", CustomerType.INDIVIDUAL, "joao@email.com", "99999-9999", "11", "+55", address);
    }

    @Test
    @DisplayName("create deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void create_ShouldMapInvokeUseCaseAndPresent() {
        CustomerRequest request = buildRequest();
        Customer mapped = mock(Customer.class);
        Customer created = mock(Customer.class);
        CustomerResponse response = mock(CustomerResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.create(mapped)).thenReturn(created);
        when(presenter.toResponse(created)).thenReturn(response);

        CustomerResponse result = adapter.create(request);

        assertSame(response, result);
        verify(mapper).toEntity(request);
        verify(service).create(mapped);
        verify(presenter).toResponse(created);
    }

    @Test
    @DisplayName("update deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void update_ShouldMapInvokeUseCaseAndPresent() {
        String id = "123";
        CustomerRequest request = buildRequest();
        Customer mapped = mock(Customer.class);
        Customer updated = mock(Customer.class);
        CustomerResponse response = mock(CustomerResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.update(id, mapped)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        CustomerResponse result = adapter.update(id, request);

        assertSame(response, result);
        verify(service).update(id, mapped);
    }

    @Test
    @DisplayName("findAll deve apresentar cada cliente retornado pelo use case")
    void findAll_ShouldPresentEachCustomer() {
        Customer customer = mock(Customer.class);
        CustomerResponse response = mock(CustomerResponse.class);

        when(service.findAll()).thenReturn(List.of(customer));
        when(presenter.toResponse(customer)).thenReturn(response);

        List<CustomerResponse> result = adapter.findAll();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("findById deve apresentar o cliente retornado pelo use case")
    void findById_ShouldPresentCustomer() {
        String id = "123";
        Customer customer = mock(Customer.class);
        CustomerResponse response = mock(CustomerResponse.class);

        when(service.findById(id)).thenReturn(customer);
        when(presenter.toResponse(customer)).thenReturn(response);

        assertSame(response, adapter.findById(id));
    }

    @Test
    @DisplayName("findByDocument deve apresentar o cliente retornado pelo use case")
    void findByDocument_ShouldPresentCustomer() {
        String document = "123.456.789-09";
        Customer customer = mock(Customer.class);
        CustomerResponse response = mock(CustomerResponse.class);

        when(service.findByDocument(document)).thenReturn(customer);
        when(presenter.toResponse(customer)).thenReturn(response);

        assertSame(response, adapter.findByDocument(document));
    }

    @Test
    @DisplayName("delete deve delegar ao use case")
    void delete_ShouldDelegateToUseCase() {
        adapter.delete("123");
        verify(service).delete("123");
    }
}