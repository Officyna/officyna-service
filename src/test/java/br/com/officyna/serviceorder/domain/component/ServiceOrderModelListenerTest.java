package br.com.officyna.serviceorder.domain.component;

import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.service.SequenceGeneratorService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderModelListenerTest {

    @Mock
    private SequenceGeneratorService sequenceGenerator;

    @InjectMocks
    private ServiceOrderModelListener listener;

    @Nested
    class OnBeforeConvert {

        @Test
        void shouldGenerateSequence_WhenOrderNumberIsNull() {
            // Arrange
            ServiceOrderEntity entity = new ServiceOrderEntity();
            BeforeConvertEvent<ServiceOrderEntity> event = new BeforeConvertEvent<>(entity, "service_orders");

            when(sequenceGenerator.generateSequence(ServiceOrderEntity.SEQUENCE_NAME)).thenReturn(1L);

            // Act
            listener.onBeforeConvert(event);

            // Assert
            verify(sequenceGenerator, times(1)).generateSequence(ServiceOrderEntity.SEQUENCE_NAME);
            assertEquals(1L, entity.getServiceOrderNumber());
        }

        @Test
        void shouldGenerateSequence_WhenOrderNumberIsZero() {
            // Arrange
            ServiceOrderEntity entity = new ServiceOrderEntity();
            entity.setServiceOrderNumber(0L);
            BeforeConvertEvent<ServiceOrderEntity> event = new BeforeConvertEvent<>(entity, "service_orders");

            when(sequenceGenerator.generateSequence(ServiceOrderEntity.SEQUENCE_NAME)).thenReturn(2L);

            // Act
            listener.onBeforeConvert(event);

            // Assert
            verify(sequenceGenerator, times(1)).generateSequence(ServiceOrderEntity.SEQUENCE_NAME);
            assertEquals(2L, entity.getServiceOrderNumber());
        }

        @Test
        void shouldGenerateSequence_WhenOrderNumberIsNegative() {
            // Arrange
            ServiceOrderEntity entity = new ServiceOrderEntity();
            entity.setServiceOrderNumber(-5L);
            BeforeConvertEvent<ServiceOrderEntity> event = new BeforeConvertEvent<>(entity, "service_orders");

            when(sequenceGenerator.generateSequence(ServiceOrderEntity.SEQUENCE_NAME)).thenReturn(3L);

            // Act
            listener.onBeforeConvert(event);

            // Assert
            verify(sequenceGenerator, times(1)).generateSequence(ServiceOrderEntity.SEQUENCE_NAME);
            assertEquals(3L, entity.getServiceOrderNumber());
        }

        @Test
        void shouldNotGenerateSequence_WhenOrderNumberIsAlreadySet() {
            // Arrange
            ServiceOrderEntity entity = new ServiceOrderEntity();
            entity.setServiceOrderNumber(100L);
            BeforeConvertEvent<ServiceOrderEntity> event = new BeforeConvertEvent<>(entity, "service_orders");

            // Act
            listener.onBeforeConvert(event);

            // Assert
            verify(sequenceGenerator, never()).generateSequence(ServiceOrderEntity.SEQUENCE_NAME);
            assertEquals(100L, entity.getServiceOrderNumber());
        }

        @Test
        void shouldNotGenerateSequence_WhenOrderNumberIsOne() {
            // Arrange
            ServiceOrderEntity entity = new ServiceOrderEntity();
            entity.setServiceOrderNumber(1L);
            BeforeConvertEvent<ServiceOrderEntity> event = new BeforeConvertEvent<>(entity, "service_orders");

            // Act
            listener.onBeforeConvert(event);

            // Assert
            verify(sequenceGenerator, never()).generateSequence(ServiceOrderEntity.SEQUENCE_NAME);
            assertEquals(1L, entity.getServiceOrderNumber());
        }

        @Test
        void shouldUseCorrectSequenceName() {
            // Arrange
            ServiceOrderEntity entity = new ServiceOrderEntity();
            BeforeConvertEvent<ServiceOrderEntity> event = new BeforeConvertEvent<>(entity, "service_orders");

            when(sequenceGenerator.generateSequence(ServiceOrderEntity.SEQUENCE_NAME)).thenReturn(42L);

            // Act
            listener.onBeforeConvert(event);

            // Assert
            verify(sequenceGenerator).generateSequence("service_orders_sequence");
        }
    }
}