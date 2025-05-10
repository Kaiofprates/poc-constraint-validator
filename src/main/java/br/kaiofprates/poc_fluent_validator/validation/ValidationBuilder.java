package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class ValidationBuilder {
    private final List<ValidationRule<ContaRequest>> rules;
    private final ConstraintValidatorContext context;

    private ValidationBuilder(ConstraintValidatorContext context) {
        this.rules = new ArrayList<>();
        this.context = context;
    }

    public static ValidationBuilder of(ConstraintValidatorContext context) {
        return new ValidationBuilder(context);
    }

    public ValidationBuilder addRule(ValidationRule<ContaRequest> rule) {
        rules.add(rule);
        return this;
    }

    public boolean validate(ContaRequest request) {
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