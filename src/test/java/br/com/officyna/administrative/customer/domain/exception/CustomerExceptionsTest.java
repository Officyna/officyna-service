package br.com.officyna.administrative.customer.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerExceptionsTest {

    @Test
    void businessException_ShouldExposeMessage() {
        CustomerBusinessException ex = new CustomerBusinessException("regra de negócio violada");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("regra de negócio violada");
    }

    @Test
    void notFoundException_ShouldBuildMessageFromId() {
        CustomerNotFoundException ex = CustomerNotFoundException.of("123");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Customer not found with id: 123");
    }
}
