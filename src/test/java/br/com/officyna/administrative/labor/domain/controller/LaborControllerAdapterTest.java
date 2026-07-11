package br.com.officyna.administrative.labor.domain.controller;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.domain.presenter.LaborPresenter;
import br.com.officyna.administrative.labor.domain.service.LaborService;
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
class LaborControllerAdapterTest {

    @Mock
    private LaborService service;

    @Mock
    private LaborMapper mapper;

    @Mock
    private LaborPresenter presenter;

    @InjectMocks
    private LaborControllerAdapter adapter;

    private LaborRequest buildRequest() {
        return new LaborRequest("Troca de óleo", "Troca de óleo e filtro", new BigDecimal("150.0"), 1, true);
    }

    @Test
    @DisplayName("create deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void create_ShouldMapInvokeUseCaseAndPresent() {
        LaborRequest request = buildRequest();
        Labor mapped = mock(Labor.class);
        Labor created = mock(Labor.class);
        LaborResponse response = mock(LaborResponse.class);

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
        LaborRequest request = buildRequest();
        Labor mapped = mock(Labor.class);
        Labor updated = mock(Labor.class);
        LaborResponse response = mock(LaborResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.update(id, mapped)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.update(id, request));
        verify(service).update(id, mapped);
    }

    @Test
    @DisplayName("findAll deve apresentar cada item retornado pelo use case")
    void findAll_ShouldPresentEach() {
        Labor labor = mock(Labor.class);
        LaborResponse response = mock(LaborResponse.class);

        when(service.findAll()).thenReturn(List.of(labor));
        when(presenter.toResponse(labor)).thenReturn(response);

        List<LaborResponse> result = adapter.findAll();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("findById deve apresentar o item retornado pelo use case")
    void findById_ShouldPresent() {
        Labor labor = mock(Labor.class);
        LaborResponse response = mock(LaborResponse.class);

        when(service.findById("123")).thenReturn(labor);
        when(presenter.toResponse(labor)).thenReturn(response);

        assertSame(response, adapter.findById("123"));
    }

    @Test
    @DisplayName("delete deve delegar ao use case")
    void delete_ShouldDelegate() {
        adapter.delete("123");
        verify(service).delete("123");
    }
}