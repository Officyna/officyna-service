package br.com.officyna.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserSeeder userSeeder;
    private final CustomerSeeder customerSeeder;
    private final VehicleSeeder vehicleSeeder;
    private final LaborSeeder laborSeeder;
    private final SupplySeeder supplySeeder;

    @Override
    public void run(String... args) {

        userSeeder.seed();

        customerSeeder.seed();

        vehicleSeeder.seed();

        laborSeeder.seed();

        supplySeeder.seed();

        log.info("==========================================");
        log.info("DATABASE SEED FINALIZADO COM SUCESSO");
        log.info("==========================================");
    }
}