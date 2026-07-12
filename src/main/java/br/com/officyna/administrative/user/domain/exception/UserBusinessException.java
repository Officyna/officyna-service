package br.com.officyna.administrative.user.domain.exception;

public class UserBusinessException extends RuntimeException {

    public UserBusinessException(String message) {
        super(message);
    }
}
