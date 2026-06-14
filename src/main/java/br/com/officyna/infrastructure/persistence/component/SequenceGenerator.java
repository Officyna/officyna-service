package br.com.officyna.infrastructure.persistence.component;

import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderSequenceDocument;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class SequenceGenerator implements SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    @Override
    public long generateSequence(String seqName) {
        ServiceOrderSequenceDocument counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq", 1), options().returnNew(true).upsert(true),
                ServiceOrderSequenceDocument.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}

