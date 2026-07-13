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
@SuppressWarnings("java:S6437")
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

        log.info("Usuário administrador criado com sucesso.{}", repository.count());
    }

}