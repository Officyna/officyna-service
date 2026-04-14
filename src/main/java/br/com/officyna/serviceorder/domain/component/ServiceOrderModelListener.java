package br.com.officyna.serviceorder.domain.component;

import br.com.officyna.serviceorder.domain.enitity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.service.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceOrderModelListener extends AbstractMongoEventListener<ServiceOrderEntity> {
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<ServiceOrderEntity> event) {
        if (event.getSource().getServiceOrderNumber() == null || event.getSource().getServiceOrderNumber() < 1) {
            event.getSource().setServiceOrderNumber(sequenceGenerator.generateSequence(ServiceOrderEntity.SEQUENCE_NAME));
        }
    }
}
