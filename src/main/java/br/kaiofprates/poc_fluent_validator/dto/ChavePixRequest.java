package br.kaiofprates.poc_fluent_validator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChavePixRequest {
    @NotBlank(message = "O tipo da chave PIX é obrigatório")
    private String tipo; // CPF, CNPJ, EMAIL, TELEFONE, CHAVE_ALEATORIA

    @NotBlank(message = "O valor da chave PIX é obrigatório")
    private String valor;
} 