package br.com.officyna.administrative.user.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException of(Object id) {
        return new UserNotFoundException("User not found with id: " + id);
    }
}
