package br.com.officyna.seed;

import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import br.com.officyna.infrastructure.persistence.mongodb.model.AddressDocument;
import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerSeeder {

    private final CustomerMongoRepository repository;

    public void seed() {
        log.info("Iniciando seed de clientes...");

        if (repository.existsByDocument("12345678901")) {
            return;
        }

        CustomerDocument customer = CustomerDocument.builder()
                .name("João da Silva")
                .document("12345678901")
                .type(CustomerType.INDIVIDUAL.name())
                .email("joao@email.com")
                .phone("999999999")
                .areaCode("11")
                .countryCode("+55")
                .active(true)
                .address(
                        AddressDocument.builder()
                                .street("Rua das Flores")
                                .number("100")
                                .complement("Casa")
                                .neighborhood("Centro")
                                .city("São Paulo")
                                .state("SP")
                                .zipCode("01000-000")
                                .country("Brasil")
                                .build()
                )
                .build();

        repository.save(customer);

        log.info("Cliente João da Silva criado. {}", repository.count());

        CustomerDocument customer2 = CustomerDocument.builder()
                .name("Empresa XPTO")
                .document("12345678000199")
                .type(CustomerType.COMPANY.name())
                .email("contato@xpto.com")
                .phone("988888888")
                .areaCode("11")
                .countryCode("+55")
                .active(true)
                .address(
                        AddressDocument.builder()
                                .street("Av. Paulista")
                                .number("1500")
                                .complement("10º andar")
                                .neighborhood("Bela Vista")
                                .city("São Paulo")
                                .state("SP")
                                .zipCode("01310-200")
                                .country("Brasil")
                                .build()
                )
                .build();

        repository.save(customer2);
    }

}