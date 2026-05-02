package br.com.officyna.administrative.user.api.resources;

import br.com.officyna.administrative.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private UserRole userRole;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
