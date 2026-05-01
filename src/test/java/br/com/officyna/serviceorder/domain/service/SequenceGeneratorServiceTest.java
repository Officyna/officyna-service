package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.serviceorder.domain.entity.ServiceOrderSequenceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SequenceGeneratorService - Testes Unitários")
class SequenceGeneratorServiceTest {

    @Mock
    private MongoOperations mongoOperations;

    @InjectMocks
    private SequenceGeneratorService service;

    private ServiceOrderSequenceEntity sequenceEntity;

    @BeforeEach
    void setUp() {
        sequenceEntity = new ServiceOrderSequenceEntity();
    }

    @Test
    @DisplayName("Deve retornar o próximo valor da sequência quando encontrado no MongoDB")
    void generateSequence_ShouldReturnNextValue_WhenCounterExists() {
        // Arrange
        sequenceEntity.setId("service_order_seq");
        sequenceEntity.setSeq(10L);

        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(sequenceEntity);

        // Act
        long sequence = service.generateSequence("service_order_seq");

        // Assert
        assertThat(sequence).isEqualTo(10L);
        verify(mongoOperations, times(1)).findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class));
    }

    @Test
    @DisplayName("Deve retornar 1 quando o contador do MongoDB é nulo")
    void generateSequence_ShouldReturnOne_WhenCounterIsNull() {
        // Arrange
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(null);

        // Act
        long sequence = service.generateSequence("non_existent_seq");

        // Assert
        assertThat(sequence).isEqualTo(1L);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 5L, 100L, 999L, 10000L})
    @DisplayName("Deve retornar valores de sequência variados")
    void generateSequence_ShouldReturnDifferentSequenceValues(long expectedSeq) {
        // Arrange
        sequenceEntity.setSeq(expectedSeq);

        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(sequenceEntity);

        // Act
        long sequence = service.generateSequence("test_seq");

        // Assert
        assertThat(sequence).isEqualTo(expectedSeq);
    }

    @Test
    @DisplayName("Deve chamar findAndModify com os parâmetros corretos")
    void generateSequence_ShouldCallFindAndModifyWithCorrectParameters() {
        // Arrange
        String sequenceName = "specific_seq";
        sequenceEntity.setSeq(42L);

        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(sequenceEntity);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        ArgumentCaptor<FindAndModifyOptions> optionsCaptor = ArgumentCaptor.forClass(FindAndModifyOptions.class);

        // Act
        long sequence = service.generateSequence(sequenceName);

        // Assert
        verify(mongoOperations).findAndModify(queryCaptor.capture(), updateCaptor.capture(),
                optionsCaptor.capture(), eq(ServiceOrderSequenceEntity.class));

        assertThat(sequence).isEqualTo(42L);
        assertThat(queryCaptor.getValue()).isNotNull();
        assertThat(updateCaptor.getValue()).isNotNull();
        assertThat(optionsCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("Deve gerar sequências múltiplas de forma independente")
    void generateSequence_ShouldGenerateMultipleSequencesIndependently() {
        // Arrange
        ServiceOrderSequenceEntity counter1 = new ServiceOrderSequenceEntity();
        counter1.setSeq(1L);

        ServiceOrderSequenceEntity counter2 = new ServiceOrderSequenceEntity();
        counter2.setSeq(100L);

        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(counter1)
                .thenReturn(counter2);

        // Act
        long sequence1 = service.generateSequence("seq_1");
        long sequence2 = service.generateSequence("seq_2");

        // Assert
        assertThat(sequence1).isEqualTo(1L);
        assertThat(sequence2).isEqualTo(100L);
        verify(mongoOperations, times(2)).findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class));
    }

    @Test
    @DisplayName("Deve retornar 1 para nova sequência (counter nulo na primeira chamada)")
    void generateSequence_ShouldReturnOneForNewSequence_OnFirstCallWithNullCounter() {
        // Arrange
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(null);

        // Act
        long firstSequence = service.generateSequence("new_seq");

        // Assert
        assertThat(firstSequence).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lidar com sequências com valor zero")
    void generateSequence_ShouldReturnZeroProperly() {
        // Arrange
        sequenceEntity.setSeq(0L);

        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(sequenceEntity);

        // Act
        long sequence = service.generateSequence("zero_seq");

        // Assert
        assertThat(sequence).isEqualTo(0L);
    }

    @Test
    @DisplayName("Deve manter a sequência após múltiplas gerações")
    void generateSequence_ShouldMaintainSequenceAfterMultipleGenerations() {
        // Arrange
        ServiceOrderSequenceEntity counter = new ServiceOrderSequenceEntity();
        counter.setSeq(50L);

        when(mongoOperations.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ServiceOrderSequenceEntity.class)))
                .thenReturn(counter);

        // Act
        long sequence1 = service.generateSequence("persistent_seq");
        long sequence2 = service.generateSequence("persistent_seq");

        // Assert
        assertThat(sequence1).isEqualTo(50L);
        assertThat(sequence2).isEqualTo(50L);
    }
}