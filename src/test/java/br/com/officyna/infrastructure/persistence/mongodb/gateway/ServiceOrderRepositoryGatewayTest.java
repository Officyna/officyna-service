package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.ServiceOrderEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.ServiceOrderMongoRepository;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceOrderRepositoryGatewayTest {

    @Mock
    private ServiceOrderMongoRepository mongoRepository;

    @Mock
    private ServiceOrderEntityDocumentMapper mapper;

    @InjectMocks
    private ServiceOrderRepositoryGateway gateway;

    private ServiceOrder serviceOrder;
    private ServiceOrderDocument serviceOrderDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        VehicleDTO vehicle = new VehicleDTO();
        CustomerDTO customer = new CustomerDTO();
        MechanicDTO mechanic = new MechanicDTO("mechanic-id", "Mechanic Name");
        LaborsDTO labors = new LaborsDTO(Collections.singletonList(new LaborDetailDTO()), new BigDecimal("100.00"));
        SupplyDTO supplys = new SupplyDTO(Collections.singletonList(new SupplyDetailDTO()), new BigDecimal("50.00"));

        serviceOrder = ServiceOrder.builder()
                .id("so-123")
                .serviceOrderNumber(1001L)
                .vehicle(vehicle)
                .customer(customer)
                .mechanic(mechanic)
                .labors(labors)
                .supplys(supplys)
                .registrationDate(LocalDateTime.now())
                .status(ServiceOrderStatus.RECEBIDA)
                .totalBudgetAmount(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        serviceOrderDocument = ServiceOrderDocument.builder()
                .id("so-123")
                .serviceOrderNumber(1001L)
                .vehicle(vehicle)
                .customer(customer)
                .mechanic(mechanic)
                .labors(labors)
                .supplys(supplys)
                .registrationDate(LocalDateTime.now())
                .status(ServiceOrderStatus.RECEBIDA.name())
                .totalBudgetAmount(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save a ServiceOrder successfully")
    void save_success() {
        when(mapper.toDocument(any(ServiceOrder.class))).thenReturn(serviceOrderDocument);
        when(mongoRepository.save(any(ServiceOrderDocument.class))).thenReturn(serviceOrderDocument);
        when(mapper.toEntity(any(ServiceOrderDocument.class))).thenReturn(serviceOrder);

        ServiceOrder savedServiceOrder = gateway.save(serviceOrder);

        assertNotNull(savedServiceOrder);
        assertEquals(serviceOrder.getId(), savedServiceOrder.getId());
        verify(mapper, times(1)).toDocument(serviceOrder);
        verify(mongoRepository, times(1)).save(serviceOrderDocument);
        verify(mapper, times(1)).toEntity(serviceOrderDocument);
    }

    @Test
    @DisplayName("Should find a ServiceOrder by ID when it exists")
    void findById_found() {
        when(mongoRepository.findById("so-123")).thenReturn(Optional.of(serviceOrderDocument));
        when(mapper.toEntity(any(ServiceOrderDocument.class))).thenReturn(serviceOrder);

        Optional<ServiceOrder> foundServiceOrder = gateway.findById("so-123");

        assertTrue(foundServiceOrder.isPresent());
        assertEquals(serviceOrder.getId(), foundServiceOrder.get().getId());
        verify(mongoRepository, times(1)).findById("so-123");
        verify(mapper, times(1)).toEntity(serviceOrderDocument);
    }

    @Test
    @DisplayName("Should return empty when ServiceOrder not found by ID")
    void findById_notFound() {
        when(mongoRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<ServiceOrder> foundServiceOrder = gateway.findById("non-existent-id");

        assertFalse(foundServiceOrder.isPresent());
        verify(mongoRepository, times(1)).findById("non-existent-id");
        verify(mapper, never()).toEntity(any(ServiceOrderDocument.class));
    }

    @Test
    @DisplayName("Should return all ServiceOrders")
    void findAll_returnsAllServiceOrders() {
        List<ServiceOrderDocument> documents = Collections.singletonList(serviceOrderDocument);
        List<ServiceOrder> serviceOrders = Collections.singletonList(serviceOrder);

        when(mongoRepository.findAll()).thenReturn(documents);
        when(mapper.toEntity(any(ServiceOrderDocument.class))).thenReturn(serviceOrder);

        List<ServiceOrder> result = gateway.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(serviceOrder.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, times(1)).toEntity(serviceOrderDocument);
    }

    @Test
    @DisplayName("Should return empty list when no ServiceOrders are found")
    void findAll_returnsEmptyList() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        List<ServiceOrder> result = gateway.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, never()).toEntity(any(ServiceOrderDocument.class));
    }

    @Test
    @DisplayName("Should delete a ServiceOrder by ID")
    void deleteById_success() {
        doNothing().when(mongoRepository).deleteById("so-123");

        gateway.deleteById("so-123");

        verify(mongoRepository, times(1)).deleteById("so-123");
    }

    @Test
    @DisplayName("Should return true if ServiceOrder exists by ID")
    void existsById_true() {
        when(mongoRepository.existsById("so-123")).thenReturn(true);

        assertTrue(gateway.existsById("so-123"));
        verify(mongoRepository, times(1)).existsById("so-123");
    }

    @Test
    @DisplayName("Should return false if ServiceOrder does not exist by ID")
    void existsById_false() {
        when(mongoRepository.existsById("non-existent-id")).thenReturn(false);

        assertFalse(gateway.existsById("non-existent-id"));
        verify(mongoRepository, times(1)).existsById("non-existent-id");
    }

    @Test
    @DisplayName("Should find ServiceOrders by laborId with completed executions")
    void findByLaborIdWithCompletedExecutions_returnsServiceOrders() {
        List<ServiceOrderDocument> documents = Collections.singletonList(serviceOrderDocument);
        List<ServiceOrder> serviceOrders = Collections.singletonList(serviceOrder);

        when(mongoRepository.findByLaborIdWithCompletedExecutions("labor-id-1")).thenReturn(documents);
        when(mapper.toEntity(any(ServiceOrderDocument.class))).thenReturn(serviceOrder);

        List<ServiceOrder> result = gateway.findByLaborIdWithCompletedExecutions("labor-id-1");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(serviceOrder.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findByLaborIdWithCompletedExecutions("labor-id-1");
        verify(mapper, times(1)).toEntity(serviceOrderDocument);
    }

    @Test
    @DisplayName("Should return empty list when no ServiceOrders found by laborId with completed executions")
    void findByLaborIdWithCompletedExecutions_returnsEmptyList() {
        when(mongoRepository.findByLaborIdWithCompletedExecutions("non-existent-labor-id")).thenReturn(Collections.emptyList());

        List<ServiceOrder> result = gateway.findByLaborIdWithCompletedExecutions("non-existent-labor-id");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByLaborIdWithCompletedExecutions("non-existent-labor-id");
        verify(mapper, never()).toEntity(any(ServiceOrderDocument.class));
    }

    @Test
    @DisplayName("Should find a ServiceOrder by service order number when it exists")
    void findByServiceOrderNumber_found() {
        when(mongoRepository.findByServiceOrderNumber(1001L)).thenReturn(Optional.of(serviceOrderDocument));
        when(mapper.toEntity(any(ServiceOrderDocument.class))).thenReturn(serviceOrder);

        Optional<ServiceOrder> foundServiceOrder = gateway.findByServiceOrderNumber(1001L);

        assertTrue(foundServiceOrder.isPresent());
        assertEquals(serviceOrder.getServiceOrderNumber(), foundServiceOrder.get().getServiceOrderNumber());
        verify(mongoRepository, times(1)).findByServiceOrderNumber(1001L);
        verify(mapper, times(1)).toEntity(serviceOrderDocument);
    }

    @Test
    @DisplayName("Should return empty when ServiceOrder not found by service order number")
    void findByServiceOrderNumber_notFound() {
        when(mongoRepository.findByServiceOrderNumber(9999L)).thenReturn(Optional.empty());

        Optional<ServiceOrder> foundServiceOrder = gateway.findByServiceOrderNumber(9999L);

        assertFalse(foundServiceOrder.isPresent());
        verify(mongoRepository, times(1)).findByServiceOrderNumber(9999L);
        verify(mapper, never()).toEntity(any(ServiceOrderDocument.class));
    }

    @Test
    @DisplayName("Should find ServiceOrders by customer ID")
    void findByCustomerId_returnsServiceOrders() {
        List<ServiceOrderDocument> documents = Collections.singletonList(serviceOrderDocument);
        List<ServiceOrder> serviceOrders = Collections.singletonList(serviceOrder);

        when(mongoRepository.findByCustomerId("customer-id")).thenReturn(documents);
        when(mapper.toEntity(any(ServiceOrderDocument.class))).thenReturn(serviceOrder);

        List<ServiceOrder> result = gateway.findByCustomerId("customer-id");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(serviceOrder.getCustomer().getId(), result.get(0).getCustomer().getId());
        verify(mongoRepository, times(1)).findByCustomerId("customer-id");
        verify(mapper, times(1)).toEntity(serviceOrderDocument);
    }

    @Test
    @DisplayName("Should return empty list when no ServiceOrders found by customer ID")
    void findByCustomerId_returnsEmptyList() {
        when(mongoRepository.findByCustomerId("non-existent-customer-id")).thenReturn(Collections.emptyList());

        List<ServiceOrder> result = gateway.findByCustomerId("non-existent-customer-id");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByCustomerId("non-existent-customer-id");
        verify(mapper, never()).toEntity(any(ServiceOrderDocument.class));
    }
}