package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContaValidationValidator implements ConstraintValidator<ContaValidation, ContaRequest> {

    @Override
    public boolean isValid(ContaRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        // Validação do tamanho do nome
        if (request.getNome() != null && request.getNome().length() > 50) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("O nome deve ter no máximo 50 caracteres")
                    .addPropertyNode("nome")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
} 