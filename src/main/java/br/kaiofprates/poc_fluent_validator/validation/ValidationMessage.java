package br.kaiofprates.poc_fluent_validator.validation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ValidationMessage {
    NOME_TAMANHO_MAXIMO("O nome deve ter no máximo 50 caracteres", "nome", ErrorCode.MAX_LENGTH_EXCEEDED),
    CPF_OBRIGATORIO("O CPF é obrigatório e deve estar no formato 000.000.000-00", "cpf", ErrorCode.INVALID_CPF),
    CNPJ_OBRIGATORIO("O CNPJ é obrigatório", "cnpj", ErrorCode.REQUIRED_FIELD),
    CNPJ_ALFANUMERICO("O CNPJ deve conter apenas caracteres alfanuméricos", "cnpj", ErrorCode.INVALID_CNPJ),
    ENDERECO_OBRIGATORIO("O endereço é obrigatório", "endereco", ErrorCode.REQUIRED_FIELD),
    EMAIL_OBRIGATORIO("O email é obrigatório e deve estar em um formato válido", "email", ErrorCode.INVALID_EMAIL),
    TELEFONE_OBRIGATORIO("O telefone é obrigatório e deve estar no formato (00) 00000-0000", "telefone", ErrorCode.INVALID_PHONE),
    SALARIO_INVALIDO("O salário deve ser maior que zero", "salario", ErrorCode.INVALID_AMOUNT),
    CARTAO_NUMERO_INVALIDO("O número do cartão deve conter 16 dígitos", "cartoes[].numero", ErrorCode.INVALID_CARD_NUMBER),
    CARTAO_BANDEIRA_OBRIGATORIA("A bandeira do cartão é obrigatória", "cartoes[].bandeira", ErrorCode.INVALID_CARD_FLAG),
    CARTAO_TIPO_INVALIDO("O tipo do cartão deve ser CREDITO ou DEBITO", "cartoes[].tipo", ErrorCode.INVALID_CARD_TYPE),
    CARTAO_LIMITE_INVALIDO("O limite do cartão deve ser maior que zero", "cartoes[].limite", ErrorCode.INVALID_CARD_LIMIT),
    CARTAO_DATA_VALIDADE_INVALIDA("A data de validade deve estar no formato MM/YY", "cartoes[].dataValidade", ErrorCode.INVALID_CARD_EXPIRATION),
    TIPO_CHAVE_PIX_INVALIDO("O tipo da chave PIX deve ser CPF, CNPJ, EMAIL, TELEFONE ou CHAVE_ALEATORIA", "chavesPix[].tipo", ErrorCode.INVALID_PIX_TYPE),
    VALOR_CHAVE_PIX_INVALIDO("O valor da chave PIX não corresponde ao formato esperado para o tipo informado", "chavesPix[].valor", ErrorCode.INVALID_PIX_KEY);

    private final String message;
    private final String field;
    private final ErrorCode errorCode;

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
} 