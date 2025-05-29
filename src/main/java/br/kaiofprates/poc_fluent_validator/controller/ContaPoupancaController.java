package br.kaiofprates.poc_fluent_validator.controller;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import br.kaiofprates.poc_fluent_validator.dto.ContaResponse;
import br.kaiofprates.poc_fluent_validator.validation.ContaScope;
import br.kaiofprates.poc_fluent_validator.validation.ContaValidation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contas-poupanca")
@Validated
public class ContaPoupancaController {

    @PostMapping
    public ResponseEntity<ContaResponse> abrirContaPoupanca(@Valid @ContaValidation(scope = ContaScope.POUPANCA) @RequestBody ContaRequest request) {
        // Aqui você implementaria a lógica de negócio para abertura de conta poupança
        ContaResponse response = ContaResponse.builder()
                .numeroConta("654321")
                .agencia("0001")
                .nome(request.getNome())
                .cpf(request.getCpf())
                .cnpj(request.getCnpj())
                .status("ATIVA")
                .mensagem("Conta poupança aberta com sucesso!")
                .build();

        return ResponseEntity.ok(response);
    }
} 