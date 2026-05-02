package br.com.officyna.administrative.user.domain;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN(0, "Administrador"),
    ATTENDANT(1, "Atendente"),
    MECHANIC(2, "Mecanico"),
    MANAGER(3, "Gerente");

    private final int cod;
    private final String role;

    UserRole(int cod, String role) {
        this.cod = cod;
        this.role = role;
    }
}
