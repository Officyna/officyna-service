package br.com.officyna.infrastructure.persistence.mongodb.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Documento técnico para persistência no MongoDB.
 * Contém todas as anotações do Spring Data MongoDB.
 * Espelho da entidade de domínio CustomerEntity.
 */
@Document(collection = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDocument {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String document;

    private String type;

    private String email;

    private String phone;

    private String areaCode;

    private String countryCode;

    private AddressDocument address;

    private Boolean active;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

