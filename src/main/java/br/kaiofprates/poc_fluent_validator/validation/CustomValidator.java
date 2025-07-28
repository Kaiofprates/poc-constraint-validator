package br.kaiofprates.poc_fluent_validator.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Classe abstrata para validador customizado independente do ConstraintValidator
 * Contém implementações padrão das validações comuns
 */
public abstract class CustomValidator<T> {
    
    protected final List<ValidationRule<T>> rules;
    protected final List<ValidationRule<T>> criticalRules;

    protected CustomValidator() {
        this.rules = new ArrayList<>();
        this.criticalRules = new ArrayList<>();
    }

    /**
     * Adiciona uma regra de validação
     */
    public CustomValidator<T> addRule(Predicate<T> predicate, String field, String message) {
        rules.add(new ValidationRule<>(predicate, new ValidationError(field, message)));
        return this;
    }

    /**
     * Adiciona uma ValidationRule existente
     */
    public CustomValidator<T> addRule(br.kaiofprates.poc_fluent_validator.validation.ValidationRule<T> rule) {
        rules.add(new ValidationRule<>(
            rule.getPredicate(),
            new ValidationError(rule.getValidationMessage().getField(), rule.getValidationMessage().getMessage())
        ));
        return this;
    }

    /**
     * Adiciona uma regra crítica (falha imediatamente se não passar)
     */
    public CustomValidator<T> addCriticalRule(Predicate<T> predicate, String field, String message) {
        criticalRules.add(new ValidationRule<>(predicate, new ValidationError(field, message)));
        return this;
    }

    /**
     * Adiciona uma ValidationRule crítica existente
     */
    public CustomValidator<T> addCriticalRule(br.kaiofprates.poc_fluent_validator.validation.ValidationRule<T> rule) {
        criticalRules.add(new ValidationRule<>(
            rule.getPredicate(),
            new ValidationError(rule.getValidationMessage().getField(), rule.getValidationMessage().getMessage())
        ));
        return this;
    }

    /**
     * Adiciona regra para campo não nulo
     */
    public CustomValidator<T> notNull(Function<T, Object> fieldSelector, String field, String message) {
        return addRule(
                obj -> fieldSelector.apply(obj) != null,
                field,
                message
        );
    }

    /**
     * Adiciona regra para campo não vazio
     */
    public CustomValidator<T> notEmpty(Function<T, String> fieldSelector, String field, String message) {
        return addRule(
                obj -> {
                    String value = fieldSelector.apply(obj);
                    return value != null && !value.trim().isEmpty();
                },
                field,
                message
        );
    }

    /**
     * Adiciona regra para padrão regex
     */
    public CustomValidator<T> matches(Function<T, String> fieldSelector, Pattern pattern, String field, String message) {
        return addRule(
                obj -> {
                    String value = fieldSelector.apply(obj);
                    return value == null || pattern.matcher(value).matches();
                },
                field,
                message
        );
    }

    /**
     * Adiciona regra para tamanho máximo
     */
    public CustomValidator<T> maxLength(Function<T, String> fieldSelector, int maxLength, String field, String message) {
        return addRule(
                obj -> {
                    String value = fieldSelector.apply(obj);
                    return value == null || value.length() <= maxLength;
                },
                field,
                message
        );
    }

    /**
     * Adiciona regra para valor mínimo
     */
    public CustomValidator<T> minValue(Function<T, Number> fieldSelector, Number minValue, String field, String message) {
        return addRule(
                obj -> {
                    Number value = fieldSelector.apply(obj);
                    return value == null || value.doubleValue() >= minValue.doubleValue();
                },
                field,
                message
        );
    }

    /**
     * Adiciona regra para valor máximo
     */
    public CustomValidator<T> maxValue(Function<T, Number> fieldSelector, Number maxValue, String field, String message) {
        return addRule(
                obj -> {
                    Number value = fieldSelector.apply(obj);
                    return value == null || value.doubleValue() <= maxValue.doubleValue();
                },
                field,
                message
        );
    }

    /**
     * Adiciona validação customizada
     */
    public CustomValidator<T> custom(Predicate<T> predicate, String field, String message) {
        return addRule(predicate, field, message);
    }

    /**
     * Adiciona validação para coleção de itens
     */
    public <U> CustomValidator<T> validateCollection(Function<T, Collection<U>> collectionSelector, 
                                                   CustomValidator<U> itemValidator, 
                                                   String field, 
                                                   String message) {
        return addRule(
                obj -> {
                    Collection<U> items = collectionSelector.apply(obj);
                    if (items == null || items.isEmpty()) {
                        return true;
                    }
                    return items.stream().allMatch(item -> itemValidator.validate(item).isValid());
                },
                field,
                message
        );
    }

    /**
     * Valida o objeto e retorna o resultado
     * Implementação padrão que pode ser sobrescrita pelas subclasses
     */
    public ValidationResult validate(T object) {
        if (object == null) {
            return ValidationResult.success();
        }

        List<ValidationError> errors = new ArrayList<>();

        // Valida regras críticas primeiro
        for (ValidationRule<T> rule : criticalRules) {
            if (!rule.getPredicate().test(object)) {
                errors.add(rule.getValidationError());
                // Para regras críticas, retorna imediatamente
                return ValidationResult.failure(errors);
            }
        }

        // Valida regras normais
        for (ValidationRule<T> rule : rules) {
            if (!rule.getPredicate().test(object)) {
                errors.add(rule.getValidationError());
            }
        }

        // Chama método abstrato para validações customizadas específicas
        ValidationResult customResult = validateCustom(object);
        if (customResult.hasErrors()) {
            errors.addAll(customResult.getErrors());
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }

    /**
     * Método abstrato para validações customizadas específicas
     * Deve ser implementado pelas subclasses
     */
    protected abstract ValidationResult validateCustom(T object);

    /**
     * Valida o objeto e lança exceção se falhar
     */
    public void validateAndThrow(T object) {
        validate(object).orThrows();
    }

    /**
     * Valida o objeto e lança exceção customizada se falhar
     */
    public void validateAndThrow(T object, String message) {
        ValidationResult result = validate(object);
        if (result.hasErrors()) {
            throw new ValidationException(message, result.getErrors());
        }
    }

    /**
     * Cria um novo validador padrão
     */
    public static <T> CustomValidator<T> create() {
        return new DefaultCustomValidator<>();
    }

    /**
     * Classe interna para representar uma regra de validação
     */
    protected static class ValidationRule<T> {
        private final Predicate<T> predicate;
        private final ValidationError validationError;

        public ValidationRule(Predicate<T> predicate, ValidationError validationError) {
            this.predicate = predicate;
            this.validationError = validationError;
        }

        public Predicate<T> getPredicate() {
            return predicate;
        }

        public ValidationError getValidationError() {
            return validationError;
        }
    }
} 