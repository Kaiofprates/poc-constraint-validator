package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import br.kaiofprates.poc_fluent_validator.dto.CartaoRequest;
import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * Exemplo de validador customizado que estende CustomValidator
 * Foca apenas nas validações específicas de ContaRequest
 */
public class ContaCustomValidator extends CustomValidator<ContaRequest> {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\(\\d{2}\\) \\d{5}-\\d{4}$");

    public ContaCustomValidator() {
        super();
        // Configura as validações padrão no construtor
        setupValidations();
    }

    /**
     * Configura todas as validações padrão
     */
    private void setupValidations() {
        // Validações críticas (fail-fast)
        addCriticalRule(
            conta -> conta.getNome() != null && !conta.getNome().trim().isEmpty(),
            "nome",
            "Nome é obrigatório"
        );

        addCriticalRule(
            conta -> conta.getCpf() != null && CPF_PATTERN.matcher(conta.getCpf()).matches(),
            "cpf",
            "CPF deve estar no formato 000.000.000-00"
        );

        // Validações normais
        notEmpty(ContaRequest::getEmail, "email", "Email é obrigatório");
        matches(ContaRequest::getEmail, EMAIL_PATTERN, "email", "Email deve ter formato válido");
        notEmpty(ContaRequest::getTelefone, "telefone", "Telefone é obrigatório");
        matches(ContaRequest::getTelefone, TELEFONE_PATTERN, "telefone", "Telefone deve estar no formato (00) 00000-0000");
        notEmpty(ContaRequest::getEndereco, "endereco", "Endereço é obrigatório");
        maxLength(ContaRequest::getNome, 50, "nome", "Nome deve ter no máximo 50 caracteres");
        minValue(ContaRequest::getSalario, 0.0, "salario", "Salário deve ser maior que zero");

        // Validações de coleções
        validateCollection(
            ContaRequest::getCartoes,
            createCartaoValidator(),
            "cartoes",
            "Cartões devem ser válidos"
        );

        validateCollection(
            ContaRequest::getChavesPix,
            createChavePixValidator(),
            "chavesPix",
            "Chaves PIX devem ser válidas"
        );
    }

    /**
     * Implementação do método abstrato para validações customizadas específicas
     * Aqui você pode adicionar lógicas de validação complexas específicas do domínio
     */
    @Override
    protected ValidationResult validateCustom(ContaRequest conta) {
        List<ValidationError> errors = new ArrayList<>();

        // Validação customizada: CPF e CNPJ não podem ser informados simultaneamente
        if (conta.getCpf() != null && conta.getCnpj() != null) {
            errors.add(new ValidationError("cpfCnpj", "CPF e CNPJ não podem ser informados simultaneamente"));
        }

        // Validação customizada: Se for pessoa física, CPF é obrigatório
        if (conta.getCpf() == null && conta.getCnpj() == null) {
            errors.add(new ValidationError("documento", "CPF ou CNPJ deve ser informado"));
        }

        // Validação customizada: Salário deve ser compatível com o tipo de conta
        if (conta.getSalario() != null && conta.getSalario() > 1000000.0) {
            errors.add(new ValidationError("salario", "Salário muito alto para conta padrão"));
        }

        // Validação customizada: Nome não pode conter números
        if (conta.getNome() != null && conta.getNome().matches(".*\\d.*")) {
            errors.add(new ValidationError("nome", "Nome não pode conter números"));
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }

    /**
     * Cria validador para cartões
     */
    private CustomValidator<CartaoRequest> createCartaoValidator() {
        return new CustomValidator<CartaoRequest>() {
            @Override
            protected ValidationResult validateCustom(CartaoRequest cartao) {
                List<ValidationError> errors = new ArrayList<>();

                // Validação customizada: Bandeira deve ser uma das aceitas
                if (cartao.getBandeira() != null && 
                    !cartao.getBandeira().matches("(?i)(visa|mastercard|elo|amex)")) {
                    errors.add(new ValidationError("bandeira", "Bandeira não aceita"));
                }

                // Validação customizada: Número deve ter 16 dígitos
                if (cartao.getNumero() != null && cartao.getNumero().replaceAll("\\D", "").length() != 16) {
                    errors.add(new ValidationError("numero", "Número do cartão deve ter 16 dígitos"));
                }

                return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
            }
        }
        .notEmpty(CartaoRequest::getNumero, "numero", "Número do cartão é obrigatório")
        .maxLength(CartaoRequest::getNumero, 19, "numero", "Número do cartão deve ter no máximo 19 caracteres")
        .notEmpty(CartaoRequest::getBandeira, "bandeira", "Bandeira do cartão é obrigatória")
        .notEmpty(CartaoRequest::getTipo, "tipo", "Tipo do cartão é obrigatório");
    }

    /**
     * Cria validador para chaves PIX
     */
    private CustomValidator<ChavePixRequest> createChavePixValidator() {
        return new CustomValidator<ChavePixRequest>() {
            @Override
            protected ValidationResult validateCustom(ChavePixRequest chavePix) {
                List<ValidationError> errors = new ArrayList<>();

                // Validação customizada: Tipo deve ser um dos aceitos
                if (chavePix.getTipo() != null && 
                    !chavePix.getTipo().matches("(?i)(cpf|cnpj|email|telefone|chave_aleatoria)")) {
                    errors.add(new ValidationError("tipo", "Tipo de chave PIX não aceito"));
                }

                // Validação customizada: Valor deve corresponder ao tipo
                if (chavePix.getTipo() != null && chavePix.getValor() != null) {
                    switch (chavePix.getTipo().toLowerCase()) {
                        case "email":
                            if (!chavePix.getValor().contains("@")) {
                                errors.add(new ValidationError("valor", "Valor deve ser um email válido"));
                            }
                            break;
                        case "telefone":
                            if (!chavePix.getValor().matches("^\\+55\\d{10,11}$")) {
                                errors.add(new ValidationError("valor", "Valor deve ser um telefone brasileiro válido"));
                            }
                            break;
                        case "cpf":
                            if (!chavePix.getValor().matches("^\\d{11}$")) {
                                errors.add(new ValidationError("valor", "Valor deve ser um CPF válido"));
                            }
                            break;
                        case "cnpj":
                            if (!chavePix.getValor().matches("^\\d{14}$")) {
                                errors.add(new ValidationError("valor", "Valor deve ser um CNPJ válido"));
                            }
                            break;
                        case "chave_aleatoria":
                            if (chavePix.getValor().length() != 32) {
                                errors.add(new ValidationError("valor", "Chave aleatória deve ter 32 caracteres"));
                            }
                            break;
                    }
                }

                return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
            }
        }
        .notEmpty(ChavePixRequest::getValor, "valor", "Valor da chave PIX é obrigatório")
        .notEmpty(ChavePixRequest::getTipo, "tipo", "Tipo da chave PIX é obrigatório");
    }

    /**
     * Método de conveniência para validação rápida
     */
    public static ValidationResult validateConta(ContaRequest conta) {
        return new ContaCustomValidator().validate(conta);
    }

    /**
     * Método de conveniência para validação com exceção
     */
    public static void validateContaAndThrow(ContaRequest conta) {
        new ContaCustomValidator().validateAndThrow(conta);
    }
} 