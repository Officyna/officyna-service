package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSeederTest {

    private UserMongoRepository repository;
    private PasswordEncoder passwordEncoder;
    private UserSeeder seeder;

    @BeforeEach
    void setUp() {

        repository = mock(UserMongoRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        seeder = new UserSeeder(repository, passwordEncoder);
    }

    @Test
    void shouldNotSeedWhenAdminAlreadyExists() {

        when(repository.existsByEmail("admin@officyna.com"))
                .thenReturn(true);

        seeder.seed();

        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldCreateAdminUser() {

        when(repository.existsByEmail("admin@officyna.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("senha-criptografada");

        when(repository.count()).thenReturn(1L);

        seeder.seed();

        ArgumentCaptor<UserDocument> captor =
                ArgumentCaptor.forClass(UserDocument.class);

        verify(repository).save(captor.capture());

        UserDocument user = captor.getValue();

        assertEquals("Administrador", user.getName());
        assertEquals("admin@officyna.com", user.getEmail());
        assertEquals("senha-criptografada", user.getPassword());
        assertEquals("ADMIN", user.getUserRole());
        assertTrue(user.getActive());

        verify(passwordEncoder).encode("123456");
        verify(repository).count();
    }

}