package br.kaiofprates.poc_fluent_validator.validation;

import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValidationBuilder<T> {
    private final List<ValidationRule<T>> rules;
    private final ConstraintValidatorContext context;

    private ValidationBuilder(ConstraintValidatorContext context) {
        this.rules = new ArrayList<>();
        this.context = context;
    }

    public static <T> ValidationBuilder<T> of(ConstraintValidatorContext context) {
        return new ValidationBuilder<>(context);
    }

    public ValidationBuilder<T> addRule(ValidationRule<T> rule) {
        rules.add(rule);
        return this;
    }

    public <U> ValidationBuilder<T> addValidator(Validator<U> validator, List<U> items) {
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (!validator.isValid(items.get(i), context)) {
                    return this;
                }
            }
        }
        return this;
    }

    public boolean validate(T request) {
        if (request == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        return rules.stream()
                .filter(rule -> !rule.getPredicate().test(request))
                .peek(rule -> buildConstraintViolation(context, rule.getValidationMessage()))
                .findFirst()
                .isEmpty();
    }

    private static void buildConstraintViolation(ConstraintValidatorContext context, ValidationMessage validationMessage) {
        context.buildConstraintViolationWithTemplate(validationMessage.getMessage())
                .addPropertyNode(validationMessage.getField())
                .addConstraintViolation();
    }
} 