package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.serviceorder.domain.entity.ServiceOrderSequenceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SequenceGeneratorServiceTest {

    @Mock
    private MongoOperations mongoOperations;

    @InjectMocks
    private SequenceGeneratorService service;

    @Test
    @DisplayName("Deve retornar o próximo valor da sequência")
    void generateSequence_ShouldReturnNextValue() {
        ServiceOrderSequenceEntity counter = new ServiceOrderSequenceEntity();
        counter.setSeq(10L);

        when(mongoOperations.findAndModify(any(), any(), any(), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(counter);

        long sequence = service.generateSequence("any_seq");

        assertThat(sequence).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve retornar 1 caso o retorno do MongoDB seja nulo")
    void generateSequence_ShouldReturnOne_WhenCounterIsNull() {
        when(mongoOperations.findAndModify(any(), any(), any(), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(null);

        long sequence = service.generateSequence("any_seq");

        assertThat(sequence).isEqualTo(1L);
    }
}