package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import br.com.officyna.infrastructure.persistence.mongodb.model.VehicleDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import br.com.officyna.infrastructure.persistence.mongodb.repository.VehicleMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleSeeder {

    private final VehicleMongoRepository repository;
    private final CustomerMongoRepository customerRepository;

    public void seed() {

        log.info("Iniciando seed de veículos...");

        if (repository.existsByPlate("ABC1D23")) {
            return;
        }

        CustomerDocument customer = customerRepository
                .findByDocument("12345678901")
                .orElseThrow(() ->
                        new IllegalStateException("Cliente seed não encontrado."));

        VehicleDocument vehicle = VehicleDocument.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .plate("ABC1D23")
                .brand("Toyota")
                .model("Corolla")
                .year(2022)
                .color("Prata")
                .active(true)
                .build();

        repository.save(vehicle);

        log.info("Veículo ABC1D23 criado. {}", repository.count());

        VehicleDocument vehicle2 = VehicleDocument.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .plate("DEF4G56")
                .brand("Honda")
                .model("Civic")
                .year(2020)
                .color("Preto")
                .active(true)
                .build();

        repository.save(vehicle2);

    }

}