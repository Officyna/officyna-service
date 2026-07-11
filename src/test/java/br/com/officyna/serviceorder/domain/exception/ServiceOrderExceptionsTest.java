package br.com.officyna.serviceorder.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderExceptionsTest {

    @Test
    void businessException_ShouldExposeMessage() {
        ServiceOrderBusinessException ex = new ServiceOrderBusinessException("regra de negócio violada");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("regra de negócio violada");
    }

    @Test
    void notFoundException_ShouldBuildMessageFromId() {
        ServiceOrderNotFoundException ex = ServiceOrderNotFoundException.of("123");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Service Order not found with id: 123");
    }
}
