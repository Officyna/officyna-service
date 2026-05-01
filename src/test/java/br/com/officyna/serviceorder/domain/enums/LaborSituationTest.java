package br.com.officyna.serviceorder.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LaborSituation enum tests")
class LaborSituationTest {

    @ParameterizedTest
    @EnumSource(LaborSituation.class)
    @DisplayName("valueOf should return the enum constant for each name")
    void valueOf_eachConstant(LaborSituation situation) {
        String name = situation.name();
        LaborSituation byName = LaborSituation.valueOf(name);
        assertThat(byName).isEqualTo(situation);
        // toString should match name() for a simple enum
        assertThat(byName.toString()).isEqualTo(name);
    }

    @Test
    @DisplayName("valueOf should throw for unknown name")
    void valueOf_unknownName_shouldThrow() {
        assertThatThrownBy(() -> LaborSituation.valueOf("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("enum should contain exactly the expected constants and ids should be unique")
    void enumConstants_uniqueAndCount() {
        LaborSituation[] values = LaborSituation.values();
        assertThat(values).hasSize(3);

        Set<String> names = new HashSet<>();
        Arrays.stream(values).forEach(s -> names.add(s.name()));
        assertThat(names).hasSize(values.length);
        assertThat(names).containsExactlyInAnyOrder("PENDENTE", "APROVADO", "REJEITADO");
    }
}

