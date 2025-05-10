package br.kaiofprates.poc_fluent_validator.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ValidationMessage {
    NOME_TAMANHO_MAXIMO("O nome deve ter no máximo 50 caracteres", "nome"),
    CPF_OBRIGATORIO("O CPF é obrigatório", "cpf"),
    CNPJ_OBRIGATORIO("O CNPJ é obrigatório", "cnpj"),
    ENDERECO_OBRIGATORIO("O endereço é obrigatório", "endereco"),
    EMAIL_OBRIGATORIO("O email é obrigatório", "email"),
    TELEFONE_OBRIGATORIO("O telefone é obrigatório", "telefone");

    private final String message;
    private final String field;
} 