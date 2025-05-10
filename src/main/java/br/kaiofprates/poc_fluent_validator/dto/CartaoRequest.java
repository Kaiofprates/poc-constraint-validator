package br.kaiofprates.poc_fluent_validator.dto;

import lombok.Data;

@Data
public class CartaoRequest {
    private String numero;
    private String bandeira;
    private String tipo; // CREDITO, DEBITO
    private Double limite;
    private String dataValidade;
} 