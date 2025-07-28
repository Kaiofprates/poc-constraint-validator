package br.kaiofprates.poc_fluent_validator.validation;

/**
 * Representa um erro de validação com campo e mensagem
 */
public class ValidationError {
    private final String field;
    private final String message;

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationError that = (ValidationError) o;

        if (!field.equals(that.field)) return false;
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }
} 