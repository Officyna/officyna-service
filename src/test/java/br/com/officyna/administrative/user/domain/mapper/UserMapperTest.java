package br.com.officyna.administrative.user.domain.mapper;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.administrative.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    private UserRequest buildRequest() {
        return new UserRequest("João Silva", "joao@email.com", "senha123", UserRole.ATTENDANT);
    }

    private UserEntity buildEntity() {
        return UserEntity.builder()
                .id("1").name("João Silva").email("joao@email.com")
                .password("encoded").userRole(UserRole.ATTENDANT).active(true)
                .build();
    }

    @Test
    @DisplayName("toEntity deve mapear todos os campos do request")
    void toEntity_ShouldMapAllFields() {
        UserEntity entity = mapper.toEntity(buildRequest());

        assertEquals("João Silva", entity.getName());
        assertEquals("joao@email.com", entity.getEmail());
        assertEquals("senha123", entity.getPassword());
        assertEquals(UserRole.ATTENDANT, entity.getUserRole());
        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("toEntity deve sempre setar active como true")
    void toEntity_ShouldSetActiveTrue() {
        assertTrue(mapper.toEntity(buildRequest()).getActive());
    }

    @Test
    @DisplayName("toResponse não deve expor a senha")
    void toResponse_ShouldNotExposePassword() {
        UserResponse response = mapper.toResponse(buildEntity());

        assertEquals("1", response.getId());
        assertEquals("João Silva", response.getName());
        assertEquals("joao@email.com", response.getEmail());
        assertEquals(UserRole.ATTENDANT, response.getUserRole());
        assertTrue(response.getActive());
    }

    @Test
    @DisplayName("updateEntity deve atualizar name, email e role sem alterar password")
    void updateEntity_ShouldUpdateNameEmailAndRole() {
        UserEntity entity = buildEntity();
        UserRequest request = new UserRequest("Novo Nome", "novo@email.com", "outrasenha", UserRole.MANAGER);

        mapper.updateEntity(entity, request);

        assertEquals("Novo Nome", entity.getName());
        assertEquals("novo@email.com", entity.getEmail());
        assertEquals(UserRole.MANAGER, entity.getUserRole());
        assertEquals("encoded", entity.getPassword()); // senha não alterada pelo mapper
    }
}