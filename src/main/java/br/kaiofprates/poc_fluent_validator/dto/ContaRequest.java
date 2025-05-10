package br.kaiofprates.poc_fluent_validator.dto;

import br.kaiofprates.poc_fluent_validator.validation.ContaValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@ContaValidation
public class ContaRequest {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;
    
    private String cnpj;
    
    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;
    
    private Double salario;
    
    @NotBlank(message = "O email é obrigatório")
    private String email;
    
    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;

    @NotEmpty(message = "Pelo menos um cartão deve ser informado")
    private List<CartaoRequest> cartoes;
} 