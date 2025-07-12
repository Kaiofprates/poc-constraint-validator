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
                        conta -> ValidationUtils.quandoNaoNulo(conta.getNome())
                                .entao(() -> conta.getNome().length() <= 50),
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
                // Validação de conflito entre idConta na raiz e recebedor
                .addRule(ValidationRule.of(
                        conta -> ValidationUtils.apenasUmPreenchido(conta.getIdConta(), conta.getRecebedor()),
                        ValidationMessage.ID_CONTA_RECEBEDOR_CONFLITO
                ))
                // Validações do recebedor
                .addRule(ValidationRule.of(
                        conta -> ValidationUtils.quandoNaoNulo(conta.getRecebedor())
                                .entao(() -> ValidationUtils.peloMenosUmPreenchido(
                                        conta.getRecebedor().getCpfCnpj(), 
                                        conta.getRecebedor().getIdConta()
                                )),
                        ValidationMessage.RECEBEDOR_OBJETO_VAZIO
                ))
                .addRule(ValidationRule.of(
                        conta -> ValidationUtils.quandoNaoNulo(conta.getRecebedor())
                                .entao(() -> ValidationUtils.apenasUmPreenchido(
                                        conta.getRecebedor().getCpfCnpj(), 
                                        conta.getRecebedor().getIdConta()
                                )),
                        ValidationMessage.RECEBEDOR_CAMPOS_OBRIGATORIOS
                ))
                .validate(request);

    }
} 