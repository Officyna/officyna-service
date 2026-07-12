package br.com.officyna.administrative.user.domain.presenter;

import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPresenterTest {

    private final UserPresenter presenter = new UserPresenter();

    private User buildEntity() {
        return User.builder()
                .id("1").name("João Silva").email("joao@email.com")
                .password("encoded").userRole(UserRole.ATTENDANT).active(true)
                .build();
    }

    @Test
    @DisplayName("toResponse não deve expor a senha")
    void toResponse_ShouldNotExposePassword() {
        UserResponse response = presenter.toResponse(buildEntity());

        assertEquals("1", response.getId());
        assertEquals("João Silva", response.getName());
        assertEquals("joao@email.com", response.getEmail());
        assertEquals(UserRole.ATTENDANT, response.getUserRole());
        assertTrue(response.getActive());
    }
}