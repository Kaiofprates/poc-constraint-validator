package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.util.Strings;

import java.util.regex.Pattern;

public class ContaValidationValidator implements ConstraintValidator<ContaValidation, ContaRequest> {

    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$");

    @Override
    public boolean isValid(ContaRequest request, ConstraintValidatorContext context) {
        return ValidationBuilder.<ContaRequest>of(context)
                // Regra crítica para CPF
                .addCriticalRule(ValidationRule.of(
                        conta -> conta.getCpf() != null && CPF_PATTERN.matcher(conta.getCpf()).matches(),
                        ValidationMessage.CPF_OBRIGATORIO
                ))
                // Validações da conta
                .addRule(ValidationRule.of(
                        conta -> conta.getNome() == null || conta.getNome().length() <= 50,
                        ValidationMessage.NOME_TAMANHO_MAXIMO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCnpj() == null || conta.getCnpj().matches("^[a-zA-Z0-9]*$"),
                        ValidationMessage.CNPJ_ALFANUMERICO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getEmail() == null || EMAIL_PATTERN.matcher(conta.getEmail()).matches(),
                        ValidationMessage.EMAIL_OBRIGATORIO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getTelefone() == null || TELEFONE_PATTERN.matcher(conta.getTelefone()).matches(),
                        ValidationMessage.TELEFONE_OBRIGATORIO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getSalario() == null || conta.getSalario() > 0,
                        ValidationMessage.SALARIO_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        conta -> Strings.isNotEmpty(conta.getEndereco()),
                        ValidationMessage.ENDERECO_OBRIGATORIO
                ))
                // Validações dos cartões usando o validador específico
                .addValidator(new CartaoValidator(), request.getCartoes())
                // Validações das chaves PIX usando o validador específico
                .addValidator(new ChavePixValidator(), request.getChavesPix())
                .validate(request);
    }
} 