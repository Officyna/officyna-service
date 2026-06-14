package br.com.officyna.administrative.user.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id;

    private String name;

    private String email;

    private String password;

    private UserRole userRole;

    private Boolean active;

    @Setter(NONE)
    private LocalDateTime createdAt;

    @Setter(NONE)
    private LocalDateTime updatedAt;
}
