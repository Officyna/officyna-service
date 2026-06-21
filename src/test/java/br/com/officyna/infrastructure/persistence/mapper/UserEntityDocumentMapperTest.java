package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.entity.UserRole;
import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityDocumentMapperTest {

    private UserEntityDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserEntityDocumentMapper();
    }

    // --- toDocument tests ---

    @Test
    @DisplayName("Should return null when toDocument receives a null User entity")
    void toDocument_nullEntity_returnsNull() {
        assertNull(mapper.toDocument(null));
    }

    @Test
    @DisplayName("Should correctly map a User entity to UserDocument")
    void toDocument_validEntity_returnsDocument() {
        User entity = User.builder()
                .id("user-123")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .userRole(UserRole.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertEquals(entity.getId(), document.getId());
        assertEquals(entity.getName(), document.getName());
        assertEquals(entity.getEmail(), document.getEmail());
        assertEquals(entity.getPassword(), document.getPassword());
        assertEquals(entity.getUserRole().name(), document.getUserRole());
        assertEquals(entity.getActive(), document.getActive());
        assertEquals(entity.getCreatedAt(), document.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), document.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a User entity with null UserRole to UserDocument")
    void toDocument_entityWithNullUserRole_returnsDocumentWithNullUserRole() {
        User entity = User.builder()
                .id("user-123")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .userRole(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertNull(document.getUserRole());
    }

    // --- toEntity tests ---

    @Test
    @DisplayName("Should return null when toEntity receives a null UserDocument")
    void toEntity_nullDocument_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should correctly map a UserDocument to User entity")
    void toEntity_validDocument_returnsEntity() {
        UserDocument document = UserDocument.builder()
                .id("user-123")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .userRole(UserRole.ADMIN.name())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertEquals(document.getId(), entity.getId());
        assertEquals(document.getName(), entity.getName());
        assertEquals(document.getEmail(), entity.getEmail());
        assertEquals(document.getPassword(), entity.getPassword());
        assertEquals(UserRole.valueOf(document.getUserRole()), entity.getUserRole());
        assertEquals(document.getActive(), entity.getActive());
        assertEquals(document.getCreatedAt(), entity.getCreatedAt());
        assertEquals(document.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a UserDocument with null userRole string to User entity")
    void toEntity_documentWithNullUserRoleString_returnsEntityWithNullUserRole() {
        UserDocument document = UserDocument.builder()
                .id("user-123")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .userRole(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertNull(entity.getUserRole());
    }
}