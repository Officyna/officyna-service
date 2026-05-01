package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.domain.dto.VehicleDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        VehicleResponse response = new VehicleResponse(id, customerId, custommerName, "PLACA-123", "Marca", "Modelo", 2000, "Cor", true, LocalDateTime.now());

        when(vehicleService.findById(id)).thenReturn(response);

        VehicleDTO result = service.getVehicle(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getPlate()).isEqualTo("PLACA-123");
        assertThat(result.getBrand()).isEqualTo("Marca");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando veículo não for encontrado")
    void getVehicle_ShouldThrowNotFound_WhenVehicleNotFound() {
        String id = "veh-not-found";

        when(vehicleService.findById(id)).thenThrow(new NotFoundException("Veículo não encontrado"));

        assertThatThrownBy(() -> service.getVehicle(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Veículo não encontrado");
    }

    @Test
    @DisplayName("Deve mapear todos os dados do veículo corretamente")
    void getVehicle_ShouldMapAllVehicleData() {
        String id = "veh-456";
        VehicleResponse response = new VehicleResponse(
                id,
                "cust-789",
                "João Silva",
                "ABC-1234",
                "Ford",
                "Fiesta",
                2022,
                "Branco",
                true,
                LocalDateTime.now()
        );

        when(vehicleService.findById(id)).thenReturn(response);

        VehicleDTO result = service.getVehicle(id);

        assertThat(result)
                .isNotNull()
                .extracting("id", "plate", "brand", "model", "color")
                .contains(id, "ABC-1234", "Ford", "Fiesta", "Branco");
    }

    @Test
    @DisplayName("Deve retornar VehicleDTO com ID diferente quando especificado")
    void getVehicle_ShouldReturnCorrectVehicleForDifferentIds() {
        VehicleResponse response1 = new VehicleResponse("veh-001", "cust-1", "Cliente 1", "PLATE-001", "Honda", "Civic", 2020, "Preto", true, LocalDateTime.now());
        VehicleResponse response2 = new VehicleResponse("veh-002", "cust-2", "Cliente 2", "PLATE-002", "Toyota", "Corolla", 2021, "Prata", true, LocalDateTime.now());

        when(vehicleService.findById("veh-001")).thenReturn(response1);
        when(vehicleService.findById("veh-002")).thenReturn(response2);

        VehicleDTO result1 = service.getVehicle("veh-001");
        VehicleDTO result2 = service.getVehicle("veh-002");

        assertThat(result1.getId()).isEqualTo("veh-001");
        assertThat(result1.getBrand()).isEqualTo("Honda");
        assertThat(result2.getId()).isEqualTo("veh-002");
        assertThat(result2.getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Deve retornar VehicleDTO com modelo e ano corretos")
    void getVehicle_ShouldReturnCorrectModelAndYear() {
        String id = "veh-789";
        VehicleResponse response = new VehicleResponse(id, "cust-1", "Cliente", "XYZ-9876", "Chevrolet", "Onix", 2023, "Cinza", true, LocalDateTime.now());

        when(vehicleService.findById(id)).thenReturn(response);

        VehicleDTO result = service.getVehicle(id);

        assertThat(result.getModel()).isEqualTo("Onix");
        assertThat(result.getColor()).isEqualTo("Cinza");
    }
}