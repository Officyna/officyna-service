package br.com.officyna.administrative.vehicle.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleExceptionsTest {

    @Test
    void businessException_ShouldExposeMessage() {
        VehicleBusinessException ex = new VehicleBusinessException("regra de negócio violada");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("regra de negócio violada");
    }

    @Test
    void notFoundException_ShouldBuildMessageFromId() {
        VehicleNotFoundException ex = VehicleNotFoundException.of("123");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Vehicle not found with id: 123");
    }
}
