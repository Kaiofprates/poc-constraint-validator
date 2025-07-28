package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import br.kaiofprates.poc_fluent_validator.dto.CartaoRequest;
import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;

import java.util.regex.Pattern;

/**
 * Exemplo de uso do CustomValidator para validar ContaRequest
 */
public class ContaFluentValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");
    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\(\\d{2}\\) \\d{4,5}-\\d{4}$");
    
    private static final CustomValidator<ContaRequest> validator = CustomValidator.<ContaRequest>create()
            // Regras críticas (falham imediatamente)
            .addCriticalRule(
                    conta -> conta.getNome() != null && !conta.getNome().trim().isEmpty(),
                    "nome",
                    "O nome é obrigatório"
            )
            .addCriticalRule(
                    conta -> conta.getCpf() != null && !conta.getCpf().trim().isEmpty(),
                    "cpf",
                    "O CPF é obrigatório"
            )
            
            // Regras normais
            .notEmpty(
                    ContaRequest::getEndereco,
                    "endereco",
                    "O endereço é obrigatório"
            )
            .notEmpty(
                    ContaRequest::getEmail,
                    "email",
                    "O email é obrigatório"
            )
            .matches(
                    ContaRequest::getEmail,
                    EMAIL_PATTERN,
                    "email",
                    "Email deve ter formato válido"
            )
            .notEmpty(
                    ContaRequest::getTelefone,
                    "telefone",
                    "O telefone é obrigatório"
            )
            .matches(
                    ContaRequest::getTelefone,
                    TELEFONE_PATTERN,
                    "telefone",
                    "Telefone deve ter formato (XX) XXXXX-XXXX"
            )
            .matches(
                    ContaRequest::getCpf,
                    CPF_PATTERN,
                    "cpf",
                    "CPF deve ter formato XXX.XXX.XXX-XX"
            )
            .custom(
                    conta -> conta.getCnpj() == null || CNPJ_PATTERN.matcher(conta.getCnpj()).matches(),
                    "cnpj",
                    "CNPJ deve ter formato XX.XXX.XXX/XXXX-XX"
            )
            .minValue(
                    ContaRequest::getSalario,
                    0.0,
                    "salario",
                    "Salário deve ser maior ou igual a zero"
            )
            .maxValue(
                    ContaRequest::getSalario,
                    1000000.0,
                    "salario",
                    "Salário deve ser menor ou igual a 1.000.000"
            )
            .custom(
                    conta -> conta.getCartoes() != null && !conta.getCartoes().isEmpty(),
                    "cartoes",
                    "Pelo menos um cartão deve ser informado"
            )
            .validateCollection(
                    ContaRequest::getCartoes,
                    createCartaoValidator(),
                    "cartoes",
                    "Cartões devem ser válidos"
            )
            .validateCollection(
                    ContaRequest::getChavesPix,
                    createChavePixValidator(),
                    "chavesPix",
                    "Chaves PIX devem ser válidas"
            );
    
    /**
     * Valida uma conta e retorna o resultado
     */
    public static ValidationResult validate(ContaRequest conta) {
        return validator.validate(conta);
    }
    
    /**
     * Valida uma conta e lança exceção se falhar
     */
    public static void validateAndThrow(ContaRequest conta) {
        validator.validateAndThrow(conta);
    }
    
    /**
     * Valida uma conta e lança exceção customizada se falhar
     */
    public static void validateAndThrow(ContaRequest conta, String message) {
        validator.validateAndThrow(conta, message);
    }
    
    /**
     * Cria validador para cartões
     */
    private static CustomValidator<CartaoRequest> createCartaoValidator() {
        return CustomValidator.<CartaoRequest>create()
                .notEmpty(
                        CartaoRequest::getNumero,
                        "numero",
                        "Número do cartão é obrigatório"
                )
                .maxLength(
                        CartaoRequest::getNumero,
                        19,
                        "numero",
                        "Número do cartão deve ter no máximo 19 caracteres"
                )
                .notEmpty(
                        CartaoRequest::getBandeira,
                        "bandeira",
                        "Bandeira do cartão é obrigatória"
                )
                .notEmpty(
                        CartaoRequest::getTipo,
                        "tipo",
                        "Tipo do cartão é obrigatório"
                );
    }
    
    /**
     * Cria validador para chaves PIX
     */
    private static CustomValidator<ChavePixRequest> createChavePixValidator() {
        return CustomValidator.<ChavePixRequest>create()
                .notEmpty(
                        ChavePixRequest::getValor,
                        "valor",
                        "Valor da chave PIX é obrigatório"
                )
                .notEmpty(
                        ChavePixRequest::getTipo,
                        "tipo",
                        "Tipo da chave PIX é obrigatório"
                );
    }
} 