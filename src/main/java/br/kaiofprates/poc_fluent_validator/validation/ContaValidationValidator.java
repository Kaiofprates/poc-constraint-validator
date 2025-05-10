package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContaValidationValidator implements ConstraintValidator<ContaValidation, ContaRequest> {

    @Override
    public void initialize(ContaValidation constraintAnnotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(ContaRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        // Validação do tamanho do nome
        if (request.getNome() != null && request.getNome().length() > 50) {
            buildConstraintViolation(context, ValidationMessage.NOME_TAMANHO_MAXIMO);
            return false;
        }

        // Validação do CNPJ alfanumérico
        if (request.getCnpj() != null && !request.getCnpj().matches("^[a-zA-Z0-9]*$")) {
            buildConstraintViolation(context, ValidationMessage.CNPJ_ALFANUMERICO);
            return false;
        }

        return true;
    }

    private static void buildConstraintViolation(ConstraintValidatorContext context, ValidationMessage validationMessage) {
        context.buildConstraintViolationWithTemplate(validationMessage.getMessage())
                .addPropertyNode(validationMessage.getField())
                .addConstraintViolation();
    }
} 