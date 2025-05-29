package br.kaiofprates.poc_fluent_validator.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ValidationMessage {
    NOME_TAMANHO_MAXIMO("O nome deve ter no máximo 50 caracteres", "nome"),
    CPF_OBRIGATORIO("O CPF é obrigatório e deve estar no formato 000.000.000-00", "cpf"),
    CNPJ_OBRIGATORIO("O CNPJ é obrigatório", "cnpj"),
    CNPJ_ALFANUMERICO("O CNPJ deve conter apenas caracteres alfanuméricos", "cnpj"),
    ENDERECO_OBRIGATORIO("O endereço é obrigatório", "endereco"),
    EMAIL_OBRIGATORIO("O email é obrigatório e deve estar em um formato válido", "email"),
    TELEFONE_OBRIGATORIO("O telefone é obrigatório e deve estar no formato (00) 00000-0000", "telefone"),
    SALARIO_INVALIDO("O salário deve ser maior que zero", "salario"),
    CARTAO_NUMERO_INVALIDO("O número do cartão deve conter 16 dígitos", "cartoes[].numero"),
    CARTAO_BANDEIRA_OBRIGATORIA("A bandeira do cartão é obrigatória", "cartoes[].bandeira"),
    CARTAO_TIPO_INVALIDO("O tipo do cartão deve ser CREDITO ou DEBITO", "cartoes[].tipo"),
    CARTAO_POUPANCA_INVALIDO("O tipo do cartão deve ser somente DEBITO", "cartoes[].tipo"),
    CARTAO_LIMITE_INVALIDO("O limite do cartão deve ser maior que zero", "cartoes[].limite"),
    CARTAO_DATA_VALIDADE_INVALIDA("A data de validade deve estar no formato MM/YY", "cartoes[].dataValidade"),
    TIPO_CHAVE_PIX_INVALIDO("O tipo da chave PIX deve ser CPF, CNPJ, EMAIL, TELEFONE ou CHAVE_ALEATORIA", "chavesPix[].tipo"),
    VALOR_CHAVE_PIX_INVALIDO("O valor da chave PIX não corresponde ao formato esperado para o tipo informado", "chavesPix[].valor");

    private final String message;
    private final String field;
} 