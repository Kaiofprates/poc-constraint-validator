package br.kaiofprates.poc_fluent_validator.dto;

import lombok.Data;

@Data
public class RecebedorRequest {
    private String cpfCnpj;
    private String idConta;
} 