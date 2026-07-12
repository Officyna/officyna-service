package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.serviceorder.domain.dto.VehicleDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleSelectionServiceTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleSelectionService service;

    @Test
    @DisplayName("Deve retornar VehicleDTO mapeado com sucesso")
    void getVehicle_ShouldReturnVehicleDTO() {
        String id = "veh-123";
        String customerId = "cust-123";
        String custommerName = "Ricardo Almeida";
        Vehicle vehicle = Vehicle.builder()
                .id(id).customerId(customerId).customerName(custommerName)
                .plate("PLACA-123").brand("Marca").model("Modelo").year(2000).color("Cor").active(true)
                .build();

        when(vehicleService.findById(id)).thenReturn(vehicle);

        VehicleDTO result = service.getVehicle(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getPlate()).isEqualTo("PLACA-123");
        assertThat(result.getBrand()).isEqualTo("Marca");
    }
}