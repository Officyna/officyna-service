package br.com.officyna.serviceorder.domain.enitity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@Getter
@Setter
public class ServiceOrderSequenceEntity {

    @Id
    private String id;

    private long seq;
}
