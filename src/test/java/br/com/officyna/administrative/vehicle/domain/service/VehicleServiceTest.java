package br.com.officyna.administrative.vehicle.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.administrative.vehicle.domain.exception.VehicleBusinessException;
import br.com.officyna.administrative.vehicle.domain.exception.VehicleNotFoundException;
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
    private CustomerService customerService;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle createVehicleEntity(String id, String plate, boolean active) {
        return Vehicle.builder()
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

    private Customer createCustomerEntity(String id) {
        return Customer.builder()
                .id(id)
                .name("João Silva")
                .document("123.456.789-09")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Deve retornar todos os veículos")
    void findAll_ShouldReturnAllVehicles() {
        Vehicle entity1 = createVehicleEntity("1", "ABC-1234", true);
        Vehicle entity2 = createVehicleEntity("2", "XYZ-5678", true);

        when(vehicleRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<Vehicle> result = vehicleService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um veículo pelo ID")
    void findById_ShouldReturnVehicle() {
        String id = "123";
        Vehicle entity = createVehicleEntity(id, "ABC-1234", true);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(entity));

        Vehicle result = vehicleService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(vehicleRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o veículo não for encontrado pelo ID")
    void findById_ShouldThrowNotFoundException() {
        String id = "nonExistentId";
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.findById(id));
        verify(vehicleRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar veículos pelo ID do cliente")
    void findByCustomer_ShouldReturnVehiclesForCustomer() {
        String customerId = "customer-1";
        Vehicle entity = createVehicleEntity("1", "ABC-1234", true);

        when(vehicleRepository.findByCustomerId(customerId)).thenReturn(List.of(entity));

        List<Vehicle> result = vehicleService.findByCustomer(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vehicleRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Deve criar um novo veículo resolvendo o cliente")
    void create_ShouldReturnCreatedVehicle() {
        Customer customer = createCustomerEntity("customer-1");
        Vehicle incoming = createVehicleEntity(null, "ABC-1234", true);
        Vehicle savedEntity = createVehicleEntity("newId", "ABC-1234", true);

        when(vehicleRepository.existsByPlate("ABC-1234")).thenReturn(false);
        when(customerService.findEntityById("customer-1")).thenReturn(customer);
        when(vehicleRepository.save(incoming)).thenReturn(savedEntity);

        Vehicle result = vehicleService.create(incoming);

        assertNotNull(result);
        assertEquals("newId", result.getId());
        assertEquals("João Silva", incoming.getCustomerName());
        verify(vehicleRepository, times(1)).existsByPlate("ABC-1234");
        verify(customerService, times(1)).findEntityById("customer-1");
        verify(vehicleRepository, times(1)).save(incoming);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar criar veículo com placa já existente")
    void create_ShouldThrowDomainException_WhenPlateExists() {
        Vehicle incoming = createVehicleEntity(null, "ABC-1234", true);
        when(vehicleRepository.existsByPlate("ABC-1234")).thenReturn(true);

        assertThrows(VehicleBusinessException.class, () -> vehicleService.create(incoming));
        verify(vehicleRepository, times(1)).existsByPlate("ABC-1234");
        verify(customerService, never()).findEntityById(any());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve atualizar um veículo existente com sucesso")
    void update_ShouldReturnUpdatedVehicle() {
        String id = "123";
        Customer customer = createCustomerEntity("customer-1");
        Vehicle existingEntity = createVehicleEntity(id, "ABC-1234", true);
        Vehicle changes = createVehicleEntity(null, "NEW-9999", true);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(vehicleRepository.existsByPlate("NEW-9999")).thenReturn(false);
        when(customerService.findEntityById("customer-1")).thenReturn(customer);
        when(vehicleRepository.save(existingEntity)).thenReturn(existingEntity);

        Vehicle result = vehicleService.update(id, changes);

        assertNotNull(result);
        assertEquals("NEW-9999", existingEntity.getPlate());
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleRepository, times(1)).existsByPlate("NEW-9999");
        verify(customerService, times(1)).findEntityById("customer-1");
        verify(vehicleRepository, times(1)).save(existingEntity);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao tentar atualizar veículo com placa já existente")
    void update_ShouldThrowDomainException_WhenPlateExists() {
        String id = "123";
        Vehicle existingEntity = createVehicleEntity(id, "ABC-1234", true);
        Vehicle changes = createVehicleEntity(null, "EXISTING-PLATE", true);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(vehicleRepository.existsByPlate("EXISTING-PLATE")).thenReturn(true);

        assertThrows(VehicleBusinessException.class, () -> vehicleService.update(id, changes));
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleRepository, times(1)).existsByPlate("EXISTING-PLATE");
        verify(customerService, never()).findEntityById(any());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve desativar um veículo ao invés de deletar fisicamente")
    void delete_ShouldDeactivateVehicle() {
        String id = "123";
        Vehicle entity = createVehicleEntity(id, "ABC-1234", true);
        Vehicle deactivatedEntity = createVehicleEntity(id, "ABC-1234", false);

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

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.delete(id));
        verify(vehicleRepository, times(1)).findById(id);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}