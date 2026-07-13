package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.entity.UserRole;
import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre UserEntity (domínio) e UserDocument (infraestrutura).
 */
@Component
public class UserEntityDocumentMapper {

    public UserDocument toDocument(User entity) {
        if (entity == null) {
            return null;
        }
        return UserDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .userRole(entity.getUserRole() != null ? entity.getUserRole().name() : null)
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public User toEntity(UserDocument document) {
        if (document == null) {
            return null;
        }
        return User.builder()
                .id(document.getId())
                .name(document.getName())
                .email(document.getEmail())
                .password(document.getPassword())
                .userRole(document.getUserRole() != null ? UserRole.valueOf(document.getUserRole()) : null)
                .active(document.getActive())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

