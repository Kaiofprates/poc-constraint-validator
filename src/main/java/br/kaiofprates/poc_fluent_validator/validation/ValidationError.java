package br.kaiofprates.poc_fluent_validator.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationError {
    private final String message;
    private final String field;
    private final String code;

    @Override
    public String toString() {
        return String.format(
            "{\"message\": \"%s\", \"field\": \"%s\", \"code\": \"%s\"}",
            message.replace("\"", "\\\""),
            field,
            code
        );
    }
} 