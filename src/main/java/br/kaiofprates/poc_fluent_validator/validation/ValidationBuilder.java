package br.kaiofprates.poc_fluent_validator.validation;

import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ValidationBuilder<T> {
    private final Collection<ValidationRule<T>> rules;
    private final Collection<ValidationRule<T>> criticalRules;
    private final ConstraintValidatorContext context;
    private boolean isValid = true;

    private ValidationBuilder(ConstraintValidatorContext context) {
        this.rules = new ArrayList<>();
        this.criticalRules = new ArrayList<>();
        this.context = context;
    }

    public static <T> ValidationBuilder<T> of(ConstraintValidatorContext context) {
        return new ValidationBuilder<>(context);
    }

    public ValidationBuilder<T> addRule(ValidationRule<T> rule) {
        rules.add(rule);
        return this;
    }

    public ValidationBuilder<T> addRuleNonNull(Function<T, Object> fieldSelector, ValidationMessage message) {
        return addRule(ValidationRule.of(
                obj -> fieldSelector.apply(obj) != null,
                message
        ));
    }

    public ValidationBuilder<T> addRulePattern(Function<T, String> fieldSelector, Pattern pattern, ValidationMessage message) {
        return addRule(ValidationRule.of(
                obj -> {
                    String value = fieldSelector.apply(obj);
                    return value == null || pattern.matcher(value).matches();
                },
                message
        ));
    }

    public ValidationBuilder<T> addRuleMaxLength(Function<T, String> fieldSelector, int maxLength, ValidationMessage message) {
        return addRule(ValidationRule.of(
                obj -> {
                    String value = fieldSelector.apply(obj);
                    return value == null || value.length() <= maxLength;
                },
                message
        ));
    }

    public ValidationBuilder<T> addRuleMinValue(Function<T, Number> fieldSelector, Number minValue, ValidationMessage message) {
        return addRule(ValidationRule.of(
                obj -> {
                    Number value = fieldSelector.apply(obj);
                    return value == null || value.doubleValue() >= minValue.doubleValue();
                },
                message
        ));
    }

    public ValidationBuilder<T> addRuleNotEmpty(Function<T, String> fieldSelector, ValidationMessage message) {
        return addRule(ValidationRule.of(
                obj -> {
                    String value = fieldSelector.apply(obj);
                    return value != null && !value.trim().isEmpty();
                },
                message
        ));
    }

    public ValidationBuilder<T> addCriticalRule(ValidationRule<T> rule) {
        criticalRules.add(rule);
        return this;
    }

    public <U> ValidationBuilder<T> addValidator(Validator<U> validator, Collection<U> items) {
        if (Objects.isNull(items) || items.isEmpty()) {
            return this;
        }

        boolean allValid = items.stream().allMatch(item -> validator.isValid(item, context));
        if (!allValid) {
            this.isValid = false;
        }

        return this;
    }

    public boolean validate(T request) {
        if (request == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        // Primeiro valida as regras críticas
        boolean criticalRulesValid = criticalRules.stream()
                .filter(rule -> !rule.getPredicate().test(request))
                .peek(rule -> buildConstraintViolation(context, rule.getValidationMessage()))
                .findFirst()
                .isEmpty();

        // Se alguma regra crítica falhou, retorna false imediatamente
        if (!criticalRulesValid) {
            return false;
        }

        // Se todas as regras críticas passaram, valida as regras normais
        boolean rulesValid = rules.stream()
                .filter(rule -> !rule.getPredicate().test(request))
                .peek(rule -> buildConstraintViolation(context, rule.getValidationMessage()))
                .findFirst()
                .isEmpty();

        return rulesValid && isValid;
    }

    private static void buildConstraintViolation(ConstraintValidatorContext context, ValidationMessage validationMessage) {
        context.buildConstraintViolationWithTemplate(validationMessage.getMessage())
                .addPropertyNode(validationMessage.getField())
                .addConstraintViolation();
    }
} 