package com.gaebokchi.userservice.exception;

public class NotFoundTokenException extends RuntimeException {

    public NotFoundTokenException(String message) {
        super(message);
    }
}
