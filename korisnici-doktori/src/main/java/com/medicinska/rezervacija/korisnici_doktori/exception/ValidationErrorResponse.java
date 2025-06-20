package com.medicinska.rezervacija.korisnici_doktori.exception;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<FieldError> fieldErrors;
    private String path;

    @Data
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
