package br.kaiofprates.poc_fluent_validator.controller;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import br.kaiofprates.poc_fluent_validator.dto.ContaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @PostMapping
    public ResponseEntity<ContaResponse> abrirConta(@RequestBody ContaRequest request) {
        // Aqui você implementaria a lógica de negócio para abertura de conta
        ContaResponse response = ContaResponse.builder()
                .numeroConta("123456")
                .agencia("0001")
                .nome(request.getNome())
                .cpf(request.getCpf())
                .cnpj(request.getCnpj())
                .status("ATIVA")
                .mensagem("Conta aberta com sucesso!")
                .build();

        return ResponseEntity.ok(response);
    }
} 