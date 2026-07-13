package br.com.officyna.administrative.vehicle.domain.controller;

import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.domain.presenter.VehiclePresenter;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
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
class VehicleControllerAdapterTest {

    @Mock
    private VehicleService service;

    @Mock
    private VehicleMapper mapper;

    @Mock
    private VehiclePresenter presenter;

    @InjectMocks
    private VehicleControllerAdapter adapter;

    private VehicleRequest buildRequest() {
        return new VehicleRequest("customer-1", "ABC-1234", "Toyota", "Corolla", 2020, "Prata");
    }

    @Test
    @DisplayName("create deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void create_ShouldMapInvokeUseCaseAndPresent() {
        VehicleRequest request = buildRequest();
        Vehicle mapped = mock(Vehicle.class);
        Vehicle created = mock(Vehicle.class);
        VehicleResponse response = mock(VehicleResponse.class);

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
        VehicleRequest request = buildRequest();
        Vehicle mapped = mock(Vehicle.class);
        Vehicle updated = mock(Vehicle.class);
        VehicleResponse response = mock(VehicleResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.update(id, mapped)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.update(id, request));
        verify(service).update(id, mapped);
    }

    @Test
    @DisplayName("findAll deve apresentar cada item retornado pelo use case")
    void findAll_ShouldPresentEach() {
        Vehicle vehicle = mock(Vehicle.class);
        VehicleResponse response = mock(VehicleResponse.class);

        when(service.findAll()).thenReturn(List.of(vehicle));
        when(presenter.toResponse(vehicle)).thenReturn(response);

        List<VehicleResponse> result = adapter.findAll();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("findById deve apresentar o item retornado pelo use case")
    void findById_ShouldPresent() {
        Vehicle vehicle = mock(Vehicle.class);
        VehicleResponse response = mock(VehicleResponse.class);

        when(service.findById("123")).thenReturn(vehicle);
        when(presenter.toResponse(vehicle)).thenReturn(response);

        assertSame(response, adapter.findById("123"));
    }

    @Test
    @DisplayName("findByCustomer deve apresentar cada item retornado pelo use case")
    void findByCustomer_ShouldPresentEach() {
        Vehicle vehicle = mock(Vehicle.class);
        VehicleResponse response = mock(VehicleResponse.class);

        when(service.findByCustomer("customer-1")).thenReturn(List.of(vehicle));
        when(presenter.toResponse(vehicle)).thenReturn(response);

        List<VehicleResponse> result = adapter.findByCustomer("customer-1");

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("delete deve delegar ao use case")
    void delete_ShouldDelegate() {
        adapter.delete("123");
        verify(service).delete("123");
    }
}