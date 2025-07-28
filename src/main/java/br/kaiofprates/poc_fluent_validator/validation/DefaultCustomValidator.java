package br.kaiofprates.poc_fluent_validator.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Implementação padrão do CustomValidator
 */
public class DefaultCustomValidator<T> extends CustomValidator<T> {

    public DefaultCustomValidator() {
        super();
    }

    @Override
    protected ValidationResult validateCustom(T object) {
        // Implementação padrão não adiciona validações customizadas
        return ValidationResult.success();
    }

} 