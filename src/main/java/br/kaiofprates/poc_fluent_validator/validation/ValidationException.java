package br.kaiofprates.poc_fluent_validator.validation;

import java.util.List;

/**
 * Exceção lançada quando uma validação falha
 */
public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public ValidationException(String message, List<ValidationError> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(List<ValidationError> errors) {
        this("Validação falhou", errors);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "message='" + getMessage() + '\'' +
                ", errors=" + errors +
                '}';
    }
} 