package br.com.officyna.infrastructure.persistence.component;

import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceOrderModelListener extends AbstractMongoEventListener<ServiceOrderDocument> {

    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<ServiceOrderDocument> event) {
        if (event.getSource().getServiceOrderNumber() == null || event.getSource().getServiceOrderNumber() < 1) {
            event.getSource().setServiceOrderNumber(sequenceGenerator.generateSequence(ServiceOrderDocument.SEQUENCE_NAME));
        }
    }
}
