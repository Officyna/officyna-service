package br.com.officyna.infrastructure.persistence.component;

import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ServiceOrderModelListenerTest {

    private SequenceGeneratorService generator;
    private ServiceOrderModelListener listener;

    @BeforeEach
    void setUp() {
        generator = mock(SequenceGeneratorService.class);
        listener = new ServiceOrderModelListener(generator);
    }

    @Test
    void shouldGenerateNumberWhenServiceOrderNumberIsNull() {

        ServiceOrderDocument document = new ServiceOrderDocument();

        when(generator.generateSequence(ServiceOrderDocument.SEQUENCE_NAME))
                .thenReturn(100L);

        BeforeConvertEvent<ServiceOrderDocument> event =
                new BeforeConvertEvent<>(document, "serviceOrder");

        listener.onBeforeConvert(event);

        assertEquals(100L, document.getServiceOrderNumber());

        verify(generator).generateSequence(ServiceOrderDocument.SEQUENCE_NAME);
    }

    @Test
    void shouldNotGenerateNumberWhenAlreadyExists() {

        ServiceOrderDocument document = new ServiceOrderDocument();
        document.setServiceOrderNumber(10L);

        BeforeConvertEvent<ServiceOrderDocument> event =
                new BeforeConvertEvent<>(document, "serviceOrder");

        listener.onBeforeConvert(event);

        verify(generator, never()).generateSequence(anyString());

        assertEquals(10L, document.getServiceOrderNumber());
    }

    @Test
    void shouldGenerateNumberWhenServiceOrderNumberIsZero() {

        ServiceOrderDocument document = new ServiceOrderDocument();
        document.setServiceOrderNumber(0L);

        when(generator.generateSequence(ServiceOrderDocument.SEQUENCE_NAME))
                .thenReturn(50L);

        BeforeConvertEvent<ServiceOrderDocument> event =
                new BeforeConvertEvent<>(document, "serviceOrder");

        listener.onBeforeConvert(event);

        assertEquals(50L, document.getServiceOrderNumber());
    }

}