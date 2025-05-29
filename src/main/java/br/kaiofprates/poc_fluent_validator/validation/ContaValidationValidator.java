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

    private ContaScope scope;

    @Override
    public void initialize(ContaValidation constraintAnnotation) {
        this.scope = constraintAnnotation.scope();
    }

    @Override
    public boolean isValid(ContaRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        ValidationBuilder<ContaRequest> builder = ValidationBuilder.<ContaRequest>of(context)
                // Regra crítica para CPF
                .addCriticalRule(ValidationRule.of(
                        conta -> conta.getCpf() != null && CPF_PATTERN.matcher(conta.getCpf()).matches(),
                        ValidationMessage.CPF_OBRIGATORIO
                ))
                // Validações da conta
                .addRuleMaxLength(ContaRequest::getNome, 50, ValidationMessage.NOME_TAMANHO_MAXIMO)
                .addRulePattern(ContaRequest::getCnpj, Pattern.compile("^[a-zA-Z0-9]*$"), ValidationMessage.CNPJ_ALFANUMERICO)
                .addRulePattern(ContaRequest::getEmail, EMAIL_PATTERN, ValidationMessage.EMAIL_OBRIGATORIO)
                .addRulePattern(ContaRequest::getTelefone, TELEFONE_PATTERN, ValidationMessage.TELEFONE_OBRIGATORIO)
                .addRuleMinValue(ContaRequest::getSalario, 0.0, ValidationMessage.SALARIO_INVALIDO)
                .addRuleNotEmpty(ContaRequest::getEndereco, ValidationMessage.ENDERECO_OBRIGATORIO)
                // Validações dos cartões usando o validador específico
                .addValidator(new CartaoValidator(), request.getCartoes())
                // Validações das chaves PIX usando o validador específico
                .addValidator(new ChavePixValidator(), request.getChavesPix());

        // Validação específica para conta poupança
        if (scope == ContaScope.POUPANCA) {
            builder.addRule(ValidationRule.of(
                    conta -> conta.getCartoes() != null && 
                            conta.getCartoes().stream().allMatch(cartao -> "DEBITO".equals(cartao.getTipo())),
                    ValidationMessage.CARTAO_POUPANCA_INVALIDO
            ));
        }

        return builder.validate(request);
    }
} 