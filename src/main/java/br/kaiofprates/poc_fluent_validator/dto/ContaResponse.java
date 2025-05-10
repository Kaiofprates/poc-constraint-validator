package br.kaiofprates.poc_fluent_validator.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ContaResponse {
    private String numeroConta;
    private String agencia;
    private String nome;
    private String cpf;
    private String cnpj;
    private String status;
    private String mensagem;
} 