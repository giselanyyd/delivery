package com.boltfood.delivery.exception;

public class InvalidTimestampException extends RuntimeException {
    public InvalidTimestampException(String message) {
        super(message);
    }
}
