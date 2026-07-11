package br.com.officyna.seed;

import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.infrastructure.persistence.mongodb.model.SupplyDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.SupplyMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class SupplySeeder {

    private final SupplyMongoRepository repository;

    public void seed() {

        log.info("Iniciando seed de insumos...");

        if (repository.count() > 0) {
            return;
        }

        repository.save(
                SupplyDocument.builder()
                        .name("Óleo 5W30")
                        .description("Óleo sintético 5W30")
                        .type(SupplyType.SUPPLY.name())
                        .purchasePrice(new BigDecimal("35.00"))
                        .salePrice(new BigDecimal("55.00"))
                        .stockQuantity(100)
                        .minimumQuantity(20)
                        .reservedQuantity(0)
                        .active(true)
                        .build());

        repository.save(
                SupplyDocument.builder()
                        .name("Filtro de óleo Bosch")
                        .description("Filtro de óleo Bosch")
                        .type(SupplyType.PART.name())
                        .purchasePrice(new BigDecimal("18.00"))
                        .salePrice(new BigDecimal("35.00"))
                        .stockQuantity(80)
                        .minimumQuantity(10)
                        .reservedQuantity(0)
                        .active(true)
                        .build());

        repository.save(
                SupplyDocument.builder()
                        .name("Pastilha de freio")
                        .description("Pastilha dianteira")
                        .type(SupplyType.PART.name())
                        .purchasePrice(new BigDecimal("90.00"))
                        .salePrice(new BigDecimal("150.00"))
                        .stockQuantity(50)
                        .minimumQuantity(10)
                        .reservedQuantity(0)
                        .active(true)
                        .build());

        repository.save(
                SupplyDocument.builder()
                        .name("Filtro de ar")
                        .description("Filtro de ar do motor")
                        .type(SupplyType.PART.name())
                        .purchasePrice(new BigDecimal("25.00"))
                        .salePrice(new BigDecimal("45.00"))
                        .stockQuantity(40)
                        .minimumQuantity(10)
                        .reservedQuantity(0)
                        .active(true)
                        .build());

        repository.save(
                SupplyDocument.builder()
                        .name("Aditivo para Radiador")
                        .description("Aditivo concentrado")
                        .type(SupplyType.SUPPLY.name())
                        .purchasePrice(new BigDecimal("22.00"))
                        .salePrice(new BigDecimal("40.00"))
                        .stockQuantity(30)
                        .minimumQuantity(5)
                        .reservedQuantity(0)
                        .active(true)
                        .build());

        log.info("{} insumos cadastrados.", repository.count());
    }

}