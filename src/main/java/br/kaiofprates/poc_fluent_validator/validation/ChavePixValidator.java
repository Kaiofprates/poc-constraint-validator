package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ChavePixValidator implements Validator<ChavePixRequest> {


    @Override
    public boolean isValid(ChavePixRequest chavePix, ConstraintValidatorContext context) {
        if (Objects.isNull(chavePix)) {
            return true;
        }

        return ValidationBuilder.<ChavePixRequest>of(context)
                .addRule(ValidationRule.of(
                        c -> ChavePixPatterns.exists(c.getTipo()),
                        ValidationMessage.TIPO_CHAVE_PIX_INVALIDO
                ))
                .addRule(ValidationRule.of(c -> ChavePixPatterns.isValidValor(c.getTipo(), c.getValor()),
                        ValidationMessage.VALOR_CHAVE_PIX_INVALIDO
                ))
                .validate(chavePix);
    }

} 