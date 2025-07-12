package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import br.kaiofprates.poc_fluent_validator.validation.ValidationUtils;
import br.kaiofprates.poc_fluent_validator.validation.ContaScope;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.util.Strings;

import java.util.regex.Pattern;

public class ContaValidationValidator implements ConstraintValidator<ContaValidation, ContaRequest> {

    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
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

        System.out.println("=== DEBUG VALIDATION ===");
        System.out.println("Nome: " + request.getNome());
        System.out.println("CPF: " + request.getCpf());
        System.out.println("Email: " + request.getEmail());
        System.out.println("Telefone: " + request.getTelefone());
        System.out.println("Endereco: " + request.getEndereco());
        System.out.println("Salario: " + request.getSalario());
        System.out.println("========================");

        ValidationBuilder<ContaRequest> builder = ValidationBuilder.<ContaRequest>of(context)
                // Regra crítica para CPF
                .addCriticalRule(ValidationRule.of(
                        conta -> conta.getCpf() != null && CPF_PATTERN.matcher(conta.getCpf()).matches(),
                        ValidationMessage.CPF_OBRIGATORIO
                ))
                // Validações da conta usando ValidationUtils
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = ValidationUtils.quandoNaoNulo(conta.getNome())
                                .entao(() -> conta.getNome().length() <= 50);
                            System.out.println("Validação NOME_TAMANHO_MAXIMO: " + result);
                            return result;
                        },
                        ValidationMessage.NOME_TAMANHO_MAXIMO
                ))
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = conta.getCnpj() == null || conta.getCnpj().matches("^[a-zA-Z0-9]*$");
                            System.out.println("Validação CNPJ_ALFANUMERICO: " + result);
                            return result;
                        },
                        ValidationMessage.CNPJ_ALFANUMERICO
                ))
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = ValidationUtils.quandoNaoNulo(conta.getEmail())
                                .entao(() -> EMAIL_PATTERN.matcher(conta.getEmail()).matches());
                            System.out.println("Validação EMAIL_OBRIGATORIO: " + result);
                            return result;
                        },
                        ValidationMessage.EMAIL_OBRIGATORIO
                ))
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = ValidationUtils.quandoNaoNulo(conta.getTelefone())
                                .entao(() -> TELEFONE_PATTERN.matcher(conta.getTelefone()).matches());
                            System.out.println("Validação TELEFONE_OBRIGATORIO: " + result);
                            return result;
                        },
                        ValidationMessage.TELEFONE_OBRIGATORIO
                ))
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = ValidationUtils.quandoNaoNulo(conta.getSalario())
                                .entao(() -> conta.getSalario() >= 0.0);
                            System.out.println("Validação SALARIO_INVALIDO: " + result);
                            return result;
                        },
                        ValidationMessage.SALARIO_INVALIDO
                ))
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = ValidationUtils.naoDeveSerVazio(conta.getEndereco());
                            System.out.println("Validação ENDERECO_OBRIGATORIO: " + result);
                            return result;
                        },
                        ValidationMessage.ENDERECO_OBRIGATORIO
                ))
                // Validações dos cartões usando o validador específico
                .addValidator(new CartaoValidator(), request.getCartoes())
                // Validações das chaves PIX usando o validador específico
                .addValidator(new ChavePixValidator(), request.getChavesPix())
                // Validação de conflito entre idConta na raiz e recebedor
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = (conta.getIdConta() == null && conta.getRecebedor() == null)
                                || ValidationUtils.apenasUmPreenchido(conta.getIdConta(), conta.getRecebedor());
                            System.out.println("Validação ID_CONTA_RECEBEDOR_CONFLITO: " + result);
                            return result;
                        },
                        ValidationMessage.ID_CONTA_RECEBEDOR_CONFLITO
                ))
                // Validações do recebedor
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = ValidationUtils.quandoNaoNulo(conta.getRecebedor())
                                .entao(() -> ValidationUtils.peloMenosUmPreenchido(
                                        conta.getRecebedor().getCpfCnpj(), 
                                        conta.getRecebedor().getIdConta()
                                ));
                            System.out.println("Validação RECEBEDOR_OBJETO_VAZIO: " + result);
                            return result;
                        },
                        ValidationMessage.RECEBEDOR_OBJETO_VAZIO
                ))
                .addRule(ValidationRule.of(
                        conta -> {
                            boolean result = ValidationUtils.quandoNaoNulo(conta.getRecebedor())
                                .entao(() -> ValidationUtils.apenasUmPreenchido(
                                        conta.getRecebedor().getCpfCnpj(), 
                                        conta.getRecebedor().getIdConta()
                                ));
                            System.out.println("Validação RECEBEDOR_CAMPOS_OBRIGATORIOS: " + result);
                            return result;
                        },
                        ValidationMessage.RECEBEDOR_CAMPOS_OBRIGATORIOS
                ));

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