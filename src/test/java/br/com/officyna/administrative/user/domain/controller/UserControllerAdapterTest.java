package br.com.officyna.administrative.user.domain.controller;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.entity.UserRole;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.domain.presenter.UserPresenter;
import br.com.officyna.administrative.user.domain.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerAdapterTest {

    @Mock
    private UserService service;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserPresenter presenter;

    @InjectMocks
    private UserControllerAdapter adapter;

    private UserRequest buildRequest() {
        return new UserRequest("João Silva", "joao@email.com", "senha123", UserRole.ATTENDANT);
    }

    @Test
    @DisplayName("create deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void create_ShouldMapInvokeUseCaseAndPresent() {
        UserRequest request = buildRequest();
        User mapped = mock(User.class);
        User created = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.create(mapped)).thenReturn(created);
        when(presenter.toResponse(created)).thenReturn(response);

        assertSame(response, adapter.create(request));
        verify(service).create(mapped);
    }

    @Test
    @DisplayName("update deve mapear request -> domínio, chamar o use case e apresentar o resultado")
    void update_ShouldMapInvokeUseCaseAndPresent() {
        String id = "1";
        UserRequest request = buildRequest();
        User mapped = mock(User.class);
        User updated = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(mapper.toEntity(request)).thenReturn(mapped);
        when(service.update(id, mapped)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.update(id, request));
        verify(service).update(id, mapped);
    }

    @Test
    @DisplayName("findAll deve apresentar cada item retornado pelo use case")
    void findAll_ShouldPresentEach() {
        User user = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(service.findAll()).thenReturn(List.of(user));
        when(presenter.toResponse(user)).thenReturn(response);

        List<UserResponse> result = adapter.findAll();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("findById deve apresentar o item retornado pelo use case")
    void findById_ShouldPresent() {
        User user = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(service.findById("1")).thenReturn(user);
        when(presenter.toResponse(user)).thenReturn(response);

        assertSame(response, adapter.findById("1"));
    }

    @Test
    @DisplayName("findByEmail deve apresentar o item retornado pelo use case")
    void findByEmail_ShouldPresent() {
        User user = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(service.findByEmail("joao@email.com")).thenReturn(user);
        when(presenter.toResponse(user)).thenReturn(response);

        assertSame(response, adapter.findByEmail("joao@email.com"));
    }

    @Test
    @DisplayName("delete deve delegar ao use case")
    void delete_ShouldDelegate() {
        adapter.delete("1");
        verify(service).delete("1");
    }
}