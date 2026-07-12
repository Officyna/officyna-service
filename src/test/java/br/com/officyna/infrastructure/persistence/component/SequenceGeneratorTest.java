package br.com.officyna.infrastructure.persistence.component;

import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderSequenceDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SequenceGeneratorTest {

    private MongoOperations mongoOperations;
    private SequenceGenerator sequenceGenerator;

    @BeforeEach
    void setUp() {
        mongoOperations = mock(MongoOperations.class);
        sequenceGenerator = new SequenceGenerator(mongoOperations);
    }

    @Test
    void shouldReturnGeneratedSequence() {

        ServiceOrderSequenceDocument document = new ServiceOrderSequenceDocument();
        document.setSeq(15L);

        when(mongoOperations.findAndModify(
                any(),
                any(),
                any(),
                eq(ServiceOrderSequenceDocument.class)))
                .thenReturn(document);

        long sequence = sequenceGenerator.generateSequence("service_order_sequence");

        assertEquals(15L, sequence);
    }

    @Test
    void shouldReturnOneWhenCounterIsNull() {

        when(mongoOperations.findAndModify(
                any(),
                any(),
                any(),
                eq(ServiceOrderSequenceDocument.class)))
                .thenReturn(null);

        long sequence = sequenceGenerator.generateSequence("service_order_sequence");

        assertEquals(1L, sequence);
    }
}