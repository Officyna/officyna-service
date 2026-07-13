package br.com.officyna.administrative.supply.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupplyExceptionsTest {

    @Test
    void businessException_ShouldExposeMessage() {
        SupplyBusinessException ex = new SupplyBusinessException("regra de negócio violada");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("regra de negócio violada");
    }

    @Test
    void notFoundException_ShouldBuildMessageFromId() {
        SupplyNotFoundException ex = SupplyNotFoundException.of("123");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Supply not found with id: 123");
    }
}
