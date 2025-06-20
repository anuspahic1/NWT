package com.medicinska.rezervacija.termini_pregledi.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
