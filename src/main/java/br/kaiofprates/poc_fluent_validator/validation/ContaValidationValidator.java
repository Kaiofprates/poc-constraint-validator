package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ContaValidationValidator implements ConstraintValidator<ContaValidation, ContaRequest> {

    @Override
    public boolean isValid(ContaRequest request, ConstraintValidatorContext context) {
        return ValidationBuilder.<ContaRequest>of(context)
                // Validações da conta
                .addRule(ValidationRule.of(
                        conta -> conta.getNome() == null || conta.getNome().length() <= 50,
                        ValidationMessage.NOME_TAMANHO_MAXIMO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCnpj() == null || conta.getCnpj().matches("^[a-zA-Z0-9]*$"),
                        ValidationMessage.CNPJ_ALFANUMERICO
                ))
                // Validações dos cartões usando o validador específico
                .addValidator(new CartaoValidator(), request.getCartoes())
                // Validações das chaves PIX usando o validador específico
                .addValidator(new ChavePixValidator(), request.getChavesPix())
                .validate(request);

    }
} 