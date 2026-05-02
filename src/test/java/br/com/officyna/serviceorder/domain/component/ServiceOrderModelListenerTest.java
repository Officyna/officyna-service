package br.com.officyna.serviceorder.domain.component;

import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.service.SequenceGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderModelListenerTest {

    @Mock
    private SequenceGeneratorService sequenceGenerator;

    @InjectMocks
    private ServiceOrderModelListener listener;

    @Test
    void onBeforeConvert_ShouldGenerateSequence_WhenOrderNumberIsNull() {
        // Arrange
        ServiceOrderEntity entity = new ServiceOrderEntity();
        BeforeConvertEvent<ServiceOrderEntity> event = new BeforeConvertEvent<>(entity, "service_orders");
        
        when(sequenceGenerator.generateSequence(ServiceOrderEntity.SEQUENCE_NAME)).thenReturn(1L);

        // Act
        listener.onBeforeConvert(event);

        // Assert
        verify(sequenceGenerator).generateSequence(ServiceOrderEntity.SEQUENCE_NAME);
        assert entity.getServiceOrderNumber() == 1L;
    }
}