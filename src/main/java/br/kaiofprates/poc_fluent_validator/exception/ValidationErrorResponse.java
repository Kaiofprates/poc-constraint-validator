package br.kaiofprates.poc_fluent_validator.exception;

import br.kaiofprates.poc_fluent_validator.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {
    private final List<ValidationError> errors;
    private final int status;
    private final String message;

    public ValidationErrorResponse(List<ValidationError> errors) {
        this.errors = errors;
        this.status = 400;
        this.message = "Validation failed";
    }
} 