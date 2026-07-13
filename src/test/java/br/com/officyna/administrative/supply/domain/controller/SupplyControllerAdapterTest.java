package br.com.officyna.administrative.supply.domain.controller;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.domain.presenter.SupplyPresenter;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyControllerAdapterTest {

    @Mock
    private SupplyService service;

    @Mock
    private SupplyMapper mapper;

    @Mock
    private SupplyPresenter presenter;

    @InjectMocks
    private SupplyControllerAdapter adapter;

    private SupplyRequest buildRequest() {
        return new SupplyRequest("Óleo", "Óleo sintético", SupplyType.SUPPLY,
                new BigDecimal("45.90"), new BigDecimal("30.00"), 50, 10, 3);
    }

    @Test
    @DisplayName("create deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void create_ShouldMapInvokeUseCaseAndPresent() {
        SupplyRequest request = buildRequest();
        Supply mapped = mock(Supply.class);
        Supply created = mock(Supply.class);
        SupplyResponse response = mock(SupplyResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.create(mapped)).thenReturn(created);
        when(presenter.toResponse(created)).thenReturn(response);

        assertSame(response, adapter.create(request));
        verify(service).create(mapped);
    }

    @Test
    @DisplayName("update deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void update_ShouldMapInvokeUseCaseAndPresent() {
        String id = "123";
        SupplyRequest request = buildRequest();
        Supply mapped = mock(Supply.class);
        Supply updated = mock(Supply.class);
        SupplyResponse response = mock(SupplyResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.update(id, mapped)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.update(id, request));
        verify(service).update(id, mapped);
    }

    @Test
    @DisplayName("findAll deve apresentar cada item retornado pelo use case")
    void findAll_ShouldPresentEach() {
        Supply supply = mock(Supply.class);
        SupplyResponse response = mock(SupplyResponse.class);

        when(service.findAll()).thenReturn(List.of(supply));
        when(presenter.toResponse(supply)).thenReturn(response);

        List<SupplyResponse> result = adapter.findAll();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("findByType deve apresentar cada item retornado pelo use case")
    void findByType_ShouldPresentEach() {
        Supply supply = mock(Supply.class);
        SupplyResponse response = mock(SupplyResponse.class);

        when(service.findByType(SupplyType.SUPPLY)).thenReturn(List.of(supply));
        when(presenter.toResponse(supply)).thenReturn(response);

        List<SupplyResponse> result = adapter.findByType(SupplyType.SUPPLY);

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("findById deve apresentar o item retornado pelo use case")
    void findById_ShouldPresent() {
        Supply supply = mock(Supply.class);
        SupplyResponse response = mock(SupplyResponse.class);

        when(service.findById("123")).thenReturn(supply);
        when(presenter.toResponse(supply)).thenReturn(response);

        assertSame(response, adapter.findById("123"));
    }

    @Test
    @DisplayName("delete deve delegar ao use case")
    void delete_ShouldDelegate() {
        adapter.delete("123");
        verify(service).delete("123");
    }
}