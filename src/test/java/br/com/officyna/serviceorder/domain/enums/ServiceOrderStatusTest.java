package br.com.officyna.serviceorder.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ServiceOrderStatus enum tests")
class ServiceOrderStatusTest {

    @ParameterizedTest
    @CsvSource({
            "1, RECEBIDA",
            "2, EM_DIAGNOSTICO",
            "3, AGUARDANDO_APROVACAO",
            "4, EM_EXECUCAO",
            "5, FINALIZADA",
            "6, APROVADA",
            "7, ENTREGUE",
            "8, RECUSADA"
    })
    @DisplayName("fromId should return expected enum for known ids")
    void fromId_knownIds(Integer id, ServiceOrderStatus expected) {
        ServiceOrderStatus actual = ServiceOrderStatus.fromId(id);
        assertThat(actual).isEqualTo(expected);
        // also assert getters
        assertThat(actual.getIdStatus()).isEqualTo(id);
        assertThat(actual.getStatusName()).isNotBlank();
    }

    @Test
    @DisplayName("fromId should throw IllegalArgumentException for unknown id")
    void fromId_unknownId_shouldThrow() {
        assertThatThrownBy(() -> ServiceOrderStatus.fromId(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ServiceOrderStatus ID");
    }

    @Test
    @DisplayName("fromId should throw IllegalArgumentException for null id")
    void fromId_null_shouldThrow() {
        assertThatThrownBy(() -> ServiceOrderStatus.fromId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ServiceOrderStatus ID");
    }

    @Test
    @DisplayName("All enum idStatus values must be unique and non-null")
    void idsShouldBeUniqueAndNonNull() {
        Set<Integer> ids = new HashSet<>();
        Arrays.stream(ServiceOrderStatus.values()).forEach(s -> {
            assertThat(s.getIdStatus()).as("id for %s", s).isNotNull();
            assertThat(s.getStatusName()).as("name for %s", s).isNotBlank();
            ids.add(s.getIdStatus());
        });
        assertThat(ids).hasSize(ServiceOrderStatus.values().length);
    }
}

