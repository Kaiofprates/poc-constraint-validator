package br.kaiofprates.poc_fluent_validator.validation;

import lombok.Builder;
import lombok.Getter;

import java.util.function.Predicate;

@Getter
@Builder
public class ValidationRule<T> {
    private final Predicate<T> predicate;
    private final ValidationMessage validationMessage;
    private final String field;

    public static <T> ValidationRule<T> of(Predicate<T> predicate, ValidationMessage validationMessage) {
        return ValidationRule.<T>builder()
                .predicate(predicate)
                .validationMessage(validationMessage)
                .field(validationMessage.getField())
                .build();
    }
} 