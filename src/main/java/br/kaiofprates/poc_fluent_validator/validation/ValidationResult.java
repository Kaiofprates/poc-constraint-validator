package br.kaiofprates.poc_fluent_validator.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Resultado de uma validação contendo status e lista de erros
 */
public class ValidationResult {
    private final boolean valid;
    private final List<ValidationError> errors;

    private ValidationResult(boolean valid, List<ValidationError> errors) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors);
    }

    /**
     * Cria um resultado de validação bem-sucedida
     */
    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * Cria um resultado de validação com erros
     */
    public static ValidationResult failure(List<ValidationError> errors) {
        return new ValidationResult(false, errors);
    }

    /**
     * Cria um resultado de validação com um único erro
     */
    public static ValidationResult failure(ValidationError error) {
        return new ValidationResult(false, List.of(error));
    }

    /**
     * Verifica se a validação foi bem-sucedida
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Verifica se a validação falhou
     */
    public boolean hasErrors() {
        return !valid;
    }

    /**
     * Retorna a lista de erros de validação
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Retorna o primeiro erro encontrado
     */
    public ValidationError getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    /**
     * Lança uma exceção se a validação falhou
     */
    public void orThrows(Supplier<? extends RuntimeException> exceptionSupplier) {
        if (hasErrors()) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Lança uma exceção padrão se a validação falhou
     */
    public void orThrows() {
        orThrows(() -> new ValidationException("Validação falhou", errors));
    }

    /**
     * Combina este resultado com outro resultado de validação
     */
    public ValidationResult and(ValidationResult other) {
        if (this.valid && other.valid) {
            return ValidationResult.success();
        }

        List<ValidationError> combinedErrors = new ArrayList<>();
        if (!this.valid) {
            combinedErrors.addAll(this.errors);
        }
        if (!other.valid) {
            combinedErrors.addAll(other.errors);
        }

        return ValidationResult.failure(combinedErrors);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors +
                '}';
    }
} 