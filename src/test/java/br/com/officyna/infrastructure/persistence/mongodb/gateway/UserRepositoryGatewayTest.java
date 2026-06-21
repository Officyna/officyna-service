package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.entity.UserRole;
import br.com.officyna.infrastructure.persistence.mapper.UserEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryGatewayTest {

    @Mock
    private UserMongoRepository mongoRepository;

    @Mock
    private UserEntityDocumentMapper mapper;

    @InjectMocks
    private UserRepositoryGateway gateway;

    private User user;
    private UserDocument userDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id("user-123")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .userRole(UserRole.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userDocument = UserDocument.builder()
                .id("user-123")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .userRole(UserRole.ADMIN.name())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save a user successfully")
    void save_success() {
        when(mapper.toDocument(any(User.class))).thenReturn(userDocument);
        when(mongoRepository.save(any(UserDocument.class))).thenReturn(userDocument);
        when(mapper.toEntity(any(UserDocument.class))).thenReturn(user);

        User savedUser = gateway.save(user);

        assertNotNull(savedUser);
        assertEquals(user.getId(), savedUser.getId());
        verify(mapper, times(1)).toDocument(user);
        verify(mongoRepository, times(1)).save(userDocument);
        verify(mapper, times(1)).toEntity(userDocument);
    }

    @Test
    @DisplayName("Should find a user by ID when it exists")
    void findById_found() {
        when(mongoRepository.findById("user-123")).thenReturn(Optional.of(userDocument));
        when(mapper.toEntity(any(UserDocument.class))).thenReturn(user);

        Optional<User> foundUser = gateway.findById("user-123");

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        verify(mongoRepository, times(1)).findById("user-123");
        verify(mapper, times(1)).toEntity(userDocument);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void findById_notFound() {
        when(mongoRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<User> foundUser = gateway.findById("non-existent-id");

        assertFalse(foundUser.isPresent());
        verify(mongoRepository, times(1)).findById("non-existent-id");
        verify(mapper, never()).toEntity(any(UserDocument.class));
    }

    @Test
    @DisplayName("Should return all users")
    void findAll_returnsAllUsers() {
        List<UserDocument> documents = Collections.singletonList(userDocument);
        List<User> users = Collections.singletonList(user);

        when(mongoRepository.findAll()).thenReturn(documents);
        when(mapper.toEntity(any(UserDocument.class))).thenReturn(user);

        List<User> result = gateway.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, times(1)).toEntity(userDocument);
    }

    @Test
    @DisplayName("Should return empty list when no users are found")
    void findAll_returnsEmptyList() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = gateway.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, never()).toEntity(any(UserDocument.class));
    }

    @Test
    @DisplayName("Should delete a user by ID")
    void deleteById_success() {
        doNothing().when(mongoRepository).deleteById("user-123");

        gateway.deleteById("user-123");

        verify(mongoRepository, times(1)).deleteById("user-123");
    }

    @Test
    @DisplayName("Should return true if user exists by ID")
    void existsById_true() {
        when(mongoRepository.existsById("user-123")).thenReturn(true);

        assertTrue(gateway.existsById("user-123"));
        verify(mongoRepository, times(1)).existsById("user-123");
    }

    @Test
    @DisplayName("Should return false if user does not exist by ID")
    void existsById_false() {
        when(mongoRepository.existsById("non-existent-id")).thenReturn(false);

        assertFalse(gateway.existsById("non-existent-id"));
        verify(mongoRepository, times(1)).existsById("non-existent-id");
    }

    @Test
    @DisplayName("Should find a user by email when it exists")
    void findByEmail_found() {
        when(mongoRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(userDocument));
        when(mapper.toEntity(any(UserDocument.class))).thenReturn(user);

        Optional<User> foundUser = gateway.findByEmail("john.doe@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
        verify(mongoRepository, times(1)).findByEmail("john.doe@example.com");
        verify(mapper, times(1)).toEntity(userDocument);
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void findByEmail_notFound() {
        when(mongoRepository.findByEmail("non.existent@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = gateway.findByEmail("non.existent@example.com");

        assertFalse(foundUser.isPresent());
        verify(mongoRepository, times(1)).findByEmail("non.existent@example.com");
        verify(mapper, never()).toEntity(any(UserDocument.class));
    }

    @Test
    @DisplayName("Should return true if user exists by email")
    void existsByEmail_true() {
        when(mongoRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertTrue(gateway.existsByEmail("john.doe@example.com"));
        verify(mongoRepository, times(1)).existsByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return false if user does not exist by email")
    void existsByEmail_false() {
        when(mongoRepository.existsByEmail("non.existent@example.com")).thenReturn(false);

        assertFalse(gateway.existsByEmail("non.existent@example.com"));
        verify(mongoRepository, times(1)).existsByEmail("non.existent@example.com");
    }

    @Test
    @DisplayName("Should return active users")
    void findByActiveTrue_returnsActiveUsers() {
        List<UserDocument> documents = Collections.singletonList(userDocument);
        List<User> users = Collections.singletonList(user);

        when(mongoRepository.findByActiveTrue()).thenReturn(documents);
        when(mapper.toEntity(any(UserDocument.class))).thenReturn(user);

        List<User> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, times(1)).toEntity(userDocument);
    }

    @Test
    @DisplayName("Should return empty list when no active users are found")
    void findByActiveTrue_returnsEmptyList() {
        when(mongoRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        List<User> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, never()).toEntity(any(UserDocument.class));
    }
}