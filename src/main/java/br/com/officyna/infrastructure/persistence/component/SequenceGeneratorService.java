package br.com.officyna.infrastructure.persistence.component;

/**
 * Interface exposed by the domain for sequence generation.
 * Implementation must be provided by the infrastructure layer.
 */
public interface SequenceGeneratorService {

    long generateSequence(String seqName);

}
