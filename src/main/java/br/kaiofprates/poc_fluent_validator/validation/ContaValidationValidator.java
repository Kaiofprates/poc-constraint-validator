package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ContaValidationValidator implements ConstraintValidator<ContaValidation, ContaRequest> {

    private static final Pattern DATA_VALIDADE_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");
    private static final Pattern NUMERO_CARTAO_PATTERN = Pattern.compile("^[0-9]{16}$");

    @Override
    public boolean isValid(ContaRequest request, ConstraintValidatorContext context) {
        return ValidationBuilder.of(context)
                // Validações da conta
                .addRule(ValidationRule.of(
                        conta -> conta.getNome() == null || conta.getNome().length() <= 50,
                        ValidationMessage.NOME_TAMANHO_MAXIMO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCnpj() == null || conta.getCnpj().matches("^[a-zA-Z0-9]*$"),
                        ValidationMessage.CNPJ_ALFANUMERICO
                ))
                // Validações dos cartões
                .addRule(ValidationRule.of(
                        conta -> conta.getCartoes() == null || conta.getCartoes().stream()
                                .allMatch(cartao -> cartao.getNumero() == null || NUMERO_CARTAO_PATTERN.matcher(cartao.getNumero()).matches()),
                        ValidationMessage.CARTAO_NUMERO_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCartoes() == null || conta.getCartoes().stream()
                                .allMatch(cartao -> cartao.getBandeira() != null && !cartao.getBandeira().trim().isEmpty()),
                        ValidationMessage.CARTAO_BANDEIRA_OBRIGATORIA
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCartoes() == null || conta.getCartoes().stream()
                                .allMatch(cartao -> cartao.getTipo() == null || Arrays.asList("CREDITO", "DEBITO").contains(cartao.getTipo())),
                        ValidationMessage.CARTAO_TIPO_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCartoes() == null || conta.getCartoes().stream()
                                .allMatch(cartao -> cartao.getLimite() == null || cartao.getLimite() > 0),
                        ValidationMessage.CARTAO_LIMITE_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCartoes() == null || conta.getCartoes().stream()
                                .allMatch(cartao -> cartao.getDataValidade() == null || DATA_VALIDADE_PATTERN.matcher(cartao.getDataValidade()).matches()),
                        ValidationMessage.CARTAO_DATA_VALIDADE_INVALIDA
                ))
                .validate(request);
    }
} 