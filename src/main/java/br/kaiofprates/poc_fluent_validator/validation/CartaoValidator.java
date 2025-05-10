package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.CartaoRequest;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.regex.Pattern;

public class CartaoValidator implements Validator<CartaoRequest> {

    private static final Pattern DATA_VALIDADE_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");
    private static final Pattern NUMERO_CARTAO_PATTERN = Pattern.compile("^[0-9]{16}$");

    @Override
    public boolean isValid(CartaoRequest cartao, ConstraintValidatorContext context) {
        if (cartao == null) {
            return true;
        }

        return ValidationBuilder.<CartaoRequest>of(context)
                .addRule(ValidationRule.of(
                        c -> c.getNumero() == null || NUMERO_CARTAO_PATTERN.matcher(c.getNumero()).matches(),
                        ValidationMessage.CARTAO_NUMERO_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        c -> c.getBandeira() != null && !c.getBandeira().trim().isEmpty(),
                        ValidationMessage.CARTAO_BANDEIRA_OBRIGATORIA
                ))
                .addRule(ValidationRule.of(
                        c -> c.getTipo() == null || Arrays.asList("CREDITO", "DEBITO").contains(c.getTipo()),
                        ValidationMessage.CARTAO_TIPO_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        c -> c.getLimite() == null || c.getLimite() > 0,
                        ValidationMessage.CARTAO_LIMITE_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        c -> c.getDataValidade() == null || DATA_VALIDADE_PATTERN.matcher(c.getDataValidade()).matches(),
                        ValidationMessage.CARTAO_DATA_VALIDADE_INVALIDA
                ))
                .validate(cartao);
    }
} 