package br.kaiofprates.poc_fluent_validator.dto;

import lombok.Data;

@Data
public class ContaRequest {
    private String nome;
    private String cpf;
    private String cnpj;
    private String endereco;
    private Double salario;
    private String email;
    private String telefone;
} 