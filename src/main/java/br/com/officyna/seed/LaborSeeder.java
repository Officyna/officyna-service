package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.LaborDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class LaborSeeder {

    private final LaborMongoRepository repository;

    public void seed() {

        log.info("Iniciando seed de mão de obra...");

        if (repository.count() > 0) {
            return;
        }

        repository.save(
                LaborDocument.builder()
                        .name("Troca de óleo")
                        .description("Troca completa do óleo do motor")
                        .price(new BigDecimal("120.00"))
                        .executionTimeInDays(1)
                        .active(true)
                        .build());

        repository.save(
                LaborDocument.builder()
                        .name("Troca de filtro de óleo")
                        .description("Substituição do filtro de óleo")
                        .price(new BigDecimal("50.00"))
                        .executionTimeInDays(1)
                        .active(true)
                        .build());

        repository.save(
                LaborDocument.builder()
                        .name("Alinhamento")
                        .description("Alinhamento computadorizado")
                        .price(new BigDecimal("90.00"))
                        .executionTimeInDays(1)
                        .active(true)
                        .build());

        repository.save(
                LaborDocument.builder()
                        .name("Balanceamento")
                        .description("Balanceamento das rodas")
                        .price(new BigDecimal("70.00"))
                        .executionTimeInDays(1)
                        .active(true)
                        .build());

        repository.save(
                LaborDocument.builder()
                        .name("Troca de pastilhas de freio")
                        .description("Substituição das pastilhas dianteiras")
                        .price(new BigDecimal("180.00"))
                        .executionTimeInDays(1)
                        .active(true)
                        .build());

        repository.save(
                LaborDocument.builder()
                        .name("Revisão Completa")
                        .description("Revisão preventiva completa do veículo")
                        .price(new BigDecimal("650.00"))
                        .executionTimeInDays(2)
                        .active(true)
                        .build());

        log.info("{} serviços cadastrados.", repository.count());
    }

}