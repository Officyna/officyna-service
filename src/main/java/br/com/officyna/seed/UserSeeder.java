package br.com.officyna.seed;

import br.com.officyna.administrative.user.domain.entity.UserRole;
import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {

    private final UserMongoRepository repository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {

        log.info("Iniciando seed de usuários...");

        if (repository.existsByEmail("admin@officyna.com")) {
            return;
        }

        UserDocument admin = UserDocument.builder()
                .name("Administrador")
                .email("admin@officyna.com")
                .password(passwordEncoder.encode("123456"))
                .userRole(UserRole.ADMIN.name())
                .active(true)
                .build();

        repository.save(admin);
//
//        UserDocument mechanic = UserDocument.builder()
//                .name("Carlos Mecânico")
//                .email("mecanico@officyna.com")
//                .password(passwordEncoder.encode("123456"))
//                .userRole(UserRole.MECHANIC.name())
//                .active(true)
//                .build();
//
//        repository.save(mechanic);
//
//        UserDocument attendant = UserDocument.builder()
//                .name("Maria Atendente")
//                .email("atendente@officyna.com")
//                .password(passwordEncoder.encode("123456"))
//                .userRole(UserRole.ATTENDANT.name())
//                .active(true)
//                .build();
//
//        repository.save(attendant);

        log.info("Usuário administrador criado com sucesso.{}", repository.count());
    }

}