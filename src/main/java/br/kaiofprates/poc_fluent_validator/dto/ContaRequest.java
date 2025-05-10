package br.kaiofprates.poc_fluent_validator.dto;

import br.kaiofprates.poc_fluent_validator.validation.ContaValidation;
import lombok.Data;

@Data
@ContaValidation
public class ContaRequest {
    private String nome;
    private String cpf;
    private String cnpj;
    private String endereco;
    private Double salario;
    private String email;
    private String telefone;
} 