package br.com.officyna.administrative.labor.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LaborExceptionsTest {

    @Test
    void businessException_ShouldExposeMessage() {
        LaborBusinessException ex = new LaborBusinessException("regra de negócio violada");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("regra de negócio violada");
    }

    @Test
    void notFoundException_ShouldBuildMessageFromId() {
        LaborNotFoundException ex = LaborNotFoundException.of("123");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Labor not found with id: 123");
    }
}
