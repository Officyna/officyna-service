package br.com.officyna.administrative.user.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserExceptionsTest {

    @Test
    void businessException_ShouldExposeMessage() {
        UserBusinessException ex = new UserBusinessException("regra de negócio violada");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("regra de negócio violada");
    }

    @Test
    void notFoundException_ShouldBuildMessageFromId() {
        UserNotFoundException ex = UserNotFoundException.of("123");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("User not found with id: 123");
    }
}
