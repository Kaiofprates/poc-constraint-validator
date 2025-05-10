package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ChavePixValidator implements Validator<ChavePixRequest> {

    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$");
    private static final Pattern CHAVE_ALEATORIA_PATTERN = Pattern.compile("^[a-zA-Z0-9]{32}$");

    @Override
    public boolean isValid(ChavePixRequest chavePix, ConstraintValidatorContext context) {
        if (chavePix == null) {
            return true;
        }

        return ValidationBuilder.<ChavePixRequest>of(context)
                .addRule(ValidationRule.of(
                        c -> c.getTipo() != null && Arrays.asList("CPF", "CNPJ", "EMAIL", "TELEFONE", "CHAVE_ALEATORIA").contains(c.getTipo()),
                        ValidationMessage.TIPO_CHAVE_PIX_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        c -> isValidValor(c.getTipo(), c.getValor()),
                        ValidationMessage.VALOR_CHAVE_PIX_INVALIDO
                ))
                .validate(chavePix);
    }

    private boolean isValidValor(String tipo, String valor) {
        if (tipo == null || valor == null) return false;

        return switch (tipo) {
            case "CPF" -> CPF_PATTERN.matcher(valor).matches();
            case "CNPJ" -> CNPJ_PATTERN.matcher(valor).matches();
            case "EMAIL" -> EMAIL_PATTERN.matcher(valor).matches();
            case "TELEFONE" -> TELEFONE_PATTERN.matcher(valor).matches();
            case "CHAVE_ALEATORIA" -> CHAVE_ALEATORIA_PATTERN.matcher(valor).matches();
            default -> false;
        };
    }
} 