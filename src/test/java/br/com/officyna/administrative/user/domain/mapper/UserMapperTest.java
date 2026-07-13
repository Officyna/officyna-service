package br.com.officyna.administrative.user.domain.mapper;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    private UserRequest buildRequest() {
        return new UserRequest("João Silva", "joao@email.com", "senha123", UserRole.ATTENDANT);
    }

    @Test
    @DisplayName("toEntity deve mapear todos os campos do request")
    void toEntity_ShouldMapAllFields() {
        User entity = mapper.toEntity(buildRequest());

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
}