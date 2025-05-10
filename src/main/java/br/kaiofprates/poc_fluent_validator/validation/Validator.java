package br.kaiofprates.poc_fluent_validator.validation;

import jakarta.validation.ConstraintValidatorContext;

public interface Validator<T> {
    boolean isValid(T value, ConstraintValidatorContext context);
} 