package br.com.officyna.seed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DatabaseSeederTest {

    private UserSeeder userSeeder;
    private CustomerSeeder customerSeeder;
    private VehicleSeeder vehicleSeeder;
    private LaborSeeder laborSeeder;
    private SupplySeeder supplySeeder;

    private DatabaseSeeder databaseSeeder;

    @BeforeEach
    void setUp() {

        userSeeder = mock(UserSeeder.class);
        customerSeeder = mock(CustomerSeeder.class);
        vehicleSeeder = mock(VehicleSeeder.class);
        laborSeeder = mock(LaborSeeder.class);
        supplySeeder = mock(SupplySeeder.class);

        databaseSeeder = new DatabaseSeeder(
                userSeeder,
                customerSeeder,
                vehicleSeeder,
                laborSeeder,
                supplySeeder
        );
    }

    @Test
    void shouldExecuteAllSeeders() {

        databaseSeeder.run();

        verify(userSeeder).seed();
        verify(customerSeeder).seed();
        verify(vehicleSeeder).seed();
        verify(laborSeeder).seed();
        verify(supplySeeder).seed();

        verifyNoMoreInteractions(
                userSeeder,
                customerSeeder,
                vehicleSeeder,
                laborSeeder,
                supplySeeder
        );
    }

}