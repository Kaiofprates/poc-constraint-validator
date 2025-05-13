package br.kaiofprates.poc_fluent_validator.validation;

public enum ErrorCode {
    // Validações gerais
    VALIDATION_ERROR("VAL001"),
    REQUIRED_FIELD("VAL002"),
    INVALID_FORMAT("VAL003"),
    
    // Validações específicas
    MAX_LENGTH_EXCEEDED("VAL101"),
    INVALID_CPF("VAL102"),
    INVALID_CNPJ("VAL103"),
    INVALID_EMAIL("VAL104"),
    INVALID_PHONE("VAL105"),
    INVALID_AMOUNT("VAL106"),
    
    // Validações de cartão
    INVALID_CARD_NUMBER("CARD001"),
    INVALID_CARD_FLAG("CARD002"),
    INVALID_CARD_TYPE("CARD003"),
    INVALID_CARD_LIMIT("CARD004"),
    INVALID_CARD_EXPIRATION("CARD005"),
    
    // Validações de PIX
    INVALID_PIX_TYPE("PIX001"),
    INVALID_PIX_KEY("PIX002");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
} 