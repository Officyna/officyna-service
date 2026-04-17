package br.com.officyna.administrative.vehicle.domain.service;

import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.repository.VehicleRepository;
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
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleEntity createVehicleEntity(String id, String plate, boolean active) {
        return VehicleEntity.builder()
                .id(id)
                .customerId("customer-1")
                .customerName("João Silva")
                .plate(plate)
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .color("Prata")
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CustomerEntity createCustomerEntity(String id) {
        return CustomerEntity.builder()
                .id(id)
                .name("João Silva")
                .document("123.456.789-09")
                .active(true)
                .build();
    }

    private VehicleRequest createVehicleRequest(String plate) {
        return new VehicleRequest("customer-1", plate, "Toyota", "Corolla", 2020, "Prata");
    }

    private VehicleResponse createVehicleResponse(String id, String plate) {
        return new VehicleResponse(id, "customer-1", "João Silva", plate, "Toyota", "Corolla", 2020, "Prata", true, LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve retornar todos os veículos")
    void findAll_ShouldReturnAllVehicles() {
        VehicleEntity entity1 = createVehicleEntity("1", "ABC-1234", true);
        VehicleEntity entity2 = createVehicleEntity("2", "XYZ-5678", true);
        VehicleResponse response1 = createVehicleResponse("1", "ABC-1234");
        VehicleResponse response2 = createVehicleResponse("2", "XYZ-5678");

        when(vehicleRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(vehicleMapper.toResponse(entity1)).thenReturn(response1);
        when(vehicleMapper.toResponse(entity2)).thenReturn(response2);

        List<VehicleResponse> result = vehicleService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ABC-1234", result.get(0).plate());
        assertEquals("XYZ-5678", result.get(1).plate());
        verify(vehicleRepository, times(1)).findAll();
        verify(vehicleMapper, times(2)).toResponse(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Deve retornar um veículo pelo ID")
    void findById_ShouldReturnVehicleResponse() {
        String id = "123";
        VehicleEntity entity = createVehicleEntity(id, "ABC-1234", true);
        VehicleResponse response = createVehicleResponse(id, "ABC-1234");

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(entity));
        when(vehicleMapper.toResponse(entity)).thenReturn(response);

        VehicleResponse result = vehicleService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("ABC-1234", result.plate());
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleMapper, times(1)).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o veículo não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehicleService.findById(id));
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleMapper, never()).toResponse(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Deve retornar veículos pelo ID do cliente")
    void findByCustomer_ShouldReturnVehiclesForCustomer() {
        String customerId = "customer-1";
        VehicleEntity entity = createVehicleEntity("1", "ABC-1234", true);
        VehicleResponse response = createVehicleResponse("1", "ABC-1234");

        when(vehicleRepository.findByCustomerId(customerId)).thenReturn(List.of(entity));
        when(vehicleMapper.toResponse(entity)).thenReturn(response);

        List<VehicleResponse> result = vehicleService.findByCustomer(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC-1234", result.get(0).plate());
        verify(vehicleRepository, times(1)).findByCustomerId(customerId);
        verify(vehicleMapper, times(1)).toResponse(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Deve criar um novo veículo com sucesso")
    void create_ShouldReturnCreatedVehicleResponse() {
        VehicleRequest request = createVehicleRequest("ABC-1234");
        CustomerEntity customer = createCustomerEntity("customer-1");
        VehicleEntity entity = createVehicleEntity(null, "ABC-1234", true);
        VehicleEntity savedEntity = createVehicleEntity("newId", "ABC-1234", true);
        VehicleResponse response = createVehicleResponse("newId", "ABC-1234");

        when(vehicleRepository.existsByPlate(request.plate().toUpperCase())).thenReturn(false);
        when(customerService.findEntityById(request.customerId())).thenReturn(customer);
        when(vehicleMapper.toEntity(request, customer)).thenReturn(entity);
        when(vehicleRepository.save(entity)).thenReturn(savedEntity);
        when(vehicleMapper.toResponse(savedEntity)).thenReturn(response);

        VehicleResponse result = vehicleService.create(request);

        assertNotNull(result);
        assertEquals("newId", result.id());
        assertEquals("ABC-1234", result.plate());
        verify(vehicleRepository, times(1)).existsByPlate(request.plate().toUpperCase());
        verify(customerService, times(1)).findEntityById(request.customerId());
        verify(vehicleMapper, times(1)).toEntity(request, customer);
        verify(vehicleRepository, times(1)).save(entity);
        verify(vehicleMapper, times(1)).toResponse(savedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar veículo com placa já existente")
    void create_ShouldThrowDomainException_WhenPlateExists() {
        VehicleRequest request = createVehicleRequest("ABC-1234");
        when(vehicleRepository.existsByPlate(request.plate().toUpperCase())).thenReturn(true);

        assertThrows(DomainException.class, () -> vehicleService.create(request));
        verify(vehicleRepository, times(1)).existsByPlate(request.plate().toUpperCase());
        verify(vehicleMapper, never()).toEntity(any(VehicleRequest.class), any(CustomerEntity.class));
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar um veículo existente com sucesso")
    void update_ShouldReturnUpdatedVehicleResponse() {
        String id = "123";
        VehicleRequest request = createVehicleRequest("NEW-9999");
        CustomerEntity customer = createCustomerEntity("customer-1");
        VehicleEntity existingEntity = createVehicleEntity(id, "ABC-1234", true);
        VehicleEntity updatedEntity = createVehicleEntity(id, "NEW-9999", true);
        VehicleResponse response = createVehicleResponse(id, "NEW-9999");

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(vehicleRepository.existsByPlate(request.plate().toUpperCase())).thenReturn(false);
        when(customerService.findEntityById(request.customerId())).thenReturn(customer);
        doNothing().when(vehicleMapper).updateEntity(existingEntity, request, customer);
        when(vehicleRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(vehicleMapper.toResponse(updatedEntity)).thenReturn(response);

        VehicleResponse result = vehicleService.update(id, request);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("NEW-9999", result.plate());
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleRepository, times(1)).existsByPlate(request.plate().toUpperCase());
        verify(vehicleMapper, times(1)).updateEntity(existingEntity, request, customer);
        verify(vehicleRepository, times(1)).save(existingEntity);
        verify(vehicleMapper, times(1)).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar veículo com placa já existente")
    void update_ShouldThrowDomainException_WhenPlateExists() {
        String id = "123";
        VehicleRequest request = createVehicleRequest("EXISTING-PLATE");
        VehicleEntity existingEntity = createVehicleEntity(id, "ABC-1234", true);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(vehicleRepository.existsByPlate(request.plate().toUpperCase())).thenReturn(true);

        assertThrows(DomainException.class, () -> vehicleService.update(id, request));
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleRepository, times(1)).existsByPlate(request.plate().toUpperCase());
        verify(vehicleMapper, never()).updateEntity(any(VehicleEntity.class), any(VehicleRequest.class), any(CustomerEntity.class));
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Deve desativar um veículo ao invés de deletar fisicamente")
    void delete_ShouldDeactivateVehicle() {
        String id = "123";
        VehicleEntity entity = createVehicleEntity(id, "ABC-1234", true);
        VehicleEntity deactivatedEntity = createVehicleEntity(id, "ABC-1234", false);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(entity));
        when(vehicleRepository.save(entity)).thenReturn(deactivatedEntity);

        vehicleService.delete(id);

        assertFalse(entity.isActive());
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar desativar veículo inexistente")
    void delete_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehicleService.delete(id));
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }
}