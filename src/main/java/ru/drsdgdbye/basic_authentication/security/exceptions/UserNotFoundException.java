package ru.drsdgdbye.basic_authentication.security.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
