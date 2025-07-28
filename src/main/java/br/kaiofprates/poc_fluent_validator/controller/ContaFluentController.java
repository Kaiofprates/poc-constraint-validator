package br.kaiofprates.poc_fluent_validator.controller;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import br.kaiofprates.poc_fluent_validator.dto.ContaResponse;
import br.kaiofprates.poc_fluent_validator.validation.ContaFluentValidator;
import br.kaiofprates.poc_fluent_validator.validation.CustomValidator;
import br.kaiofprates.poc_fluent_validator.validation.ValidationException;
import br.kaiofprates.poc_fluent_validator.validation.ValidationResult;
import br.kaiofprates.poc_fluent_validator.validation.ValidationRule;
import br.kaiofprates.poc_fluent_validator.validation.ValidationMessage;
import br.kaiofprates.poc_fluent_validator.validation.AsyncCustomValidator;
import br.kaiofprates.poc_fluent_validator.validation.ContaCustomValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Controller que demonstra o uso do FluentValidator independente do ConstraintValidator
 */
@RestController
@RequestMapping("/api/v2/contas")
public class ContaFluentController {

    /**
     * Cria uma conta usando validação com resultado
     */
    @PostMapping("/validar")
    public ResponseEntity<Map<String, Object>> criarContaComValidacao(@RequestBody ContaRequest request) {
        // Valida o request
        ValidationResult result = ContaFluentValidator.validate(request);
        
        Map<String, Object> response = new HashMap<>();
        
        if (result.isValid()) {
            // Se válido, processa a criação da conta
            ContaResponse contaResponse = processarCriacaoConta(request);
            response.put("success", true);
            response.put("message", "Conta criada com sucesso");
            response.put("data", contaResponse);
            return ResponseEntity.ok(response);
        } else {
            // Se inválido, retorna os erros
            response.put("success", false);
            response.put("message", "Dados inválidos");
            response.put("errors", result.getErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getMessage()
                    )));
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Cria uma conta usando validação com exceção
     */
    @PostMapping("/criar")
    public ResponseEntity<Map<String, Object>> criarContaComExcecao(@RequestBody ContaRequest request) {
        try {
            // Valida e lança exceção se falhar
            ContaFluentValidator.validateAndThrow(request, "Erro na validação dos dados da conta");
            
            // Se chegou aqui, a validação passou
            ContaResponse contaResponse = processarCriacaoConta(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Conta criada com sucesso");
            response.put("data", contaResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (ValidationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("errors", e.getErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getMessage()
                    )));
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Cria uma conta usando o método orThrows
     */
    @PostMapping("/criar-or-throws")
    public ResponseEntity<Map<String, Object>> criarContaComOrThrows(@RequestBody ContaRequest request) {
        // Valida e usa orThrows para lançar exceção se falhar
        ContaFluentValidator.validate(request).orThrows();
        
        // Se chegou aqui, a validação passou
        ContaResponse contaResponse = processarCriacaoConta(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Conta criada com sucesso");
        response.put("data", contaResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Valida uma conta e retorna apenas o resultado da validação
     */
    @PostMapping("/validar-apenas")
    public ResponseEntity<ValidationResult> validarApenas(@RequestBody ContaRequest request) {
        ValidationResult result = ContaFluentValidator.validate(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Exemplo de validação customizada em tempo real
     */
    @PostMapping("/validacao-customizada")
    public ResponseEntity<Map<String, Object>> validacaoCustomizada(@RequestBody ContaRequest request) {
        // Cria um validador customizado para este endpoint específico
        CustomValidator<ContaRequest> customValidator = CustomValidator.<ContaRequest>create()
                .notNull(ContaRequest::getNome, "nome", "Nome é obrigatório")
                .custom(
                        conta -> conta.getNome() != null && conta.getNome().length() >= 3,
                        "nome",
                        "Nome deve ter pelo menos 3 caracteres"
                )
                .custom(
                        conta -> conta.getSalario() == null || conta.getSalario() >= 1000.0,
                        "salario",
                        "Salário deve ser pelo menos R$ 1.000,00"
                )
                .custom(
                        conta -> {
                            if (conta.getEmail() == null) return true;
                            return Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(conta.getEmail()).matches();
                        },
                        "email",
                        "Email deve ter formato válido"
                );

        // Executa a validação
        ValidationResult result = customValidator.validate(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("errors", result.getErrors());
        response.put("message", result.isValid() ? "Dados válidos" : "Dados inválidos");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Exemplo de validação com regras críticas
     */
    @PostMapping("/validacao-critica")
    public ResponseEntity<Map<String, Object>> validacaoCritica(@RequestBody ContaRequest request) {
        // Cria um validador com regras críticas (falham imediatamente)
        CustomValidator<ContaRequest> criticalValidator = CustomValidator.<ContaRequest>create()
                .addCriticalRule(
                        conta -> conta.getNome() != null && !conta.getNome().trim().isEmpty(),
                        "nome",
                        "Nome é obrigatório e crítico"
                )
                .addCriticalRule(
                        conta -> conta.getCpf() != null && !conta.getCpf().trim().isEmpty(),
                        "cpf",
                        "CPF é obrigatório e crítico"
                )
                .addRule(
                        conta -> conta.getEmail() != null && conta.getEmail().contains("@"),
                        "email",
                        "Email deve conter @"
                )
                .addRule(
                        conta -> conta.getSalario() == null || conta.getSalario() > 0,
                        "salario",
                        "Salário deve ser positivo"
                );

        // Executa a validação
        ValidationResult result = criticalValidator.validate(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("errors", result.getErrors());
        response.put("message", result.isValid() ? "Dados válidos" : "Dados inválidos");
        response.put("criticalValidation", "Regras críticas foram validadas primeiro");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Exemplo de validação com múltiplos validadores
     */
    @PostMapping("/validacao-multipla")
    public ResponseEntity<Map<String, Object>> validacaoMultipla(@RequestBody ContaRequest request) {
        // Validador para dados pessoais
        CustomValidator<ContaRequest> dadosPessoaisValidator = CustomValidator.<ContaRequest>create()
                .notNull(ContaRequest::getNome, "nome", "Nome é obrigatório")
                .notNull(ContaRequest::getCpf, "cpf", "CPF é obrigatório")
                .notNull(ContaRequest::getEmail, "email", "Email é obrigatório");

        // Validador para dados financeiros
        CustomValidator<ContaRequest> dadosFinanceirosValidator = CustomValidator.<ContaRequest>create()
                .custom(
                        conta -> conta.getSalario() == null || conta.getSalario() >= 0,
                        "salario",
                        "Salário deve ser não negativo"
                )
                .custom(
                        conta -> conta.getCartoes() == null || !conta.getCartoes().isEmpty(),
                        "cartoes",
                        "Pelo menos um cartão deve ser informado"
                );

        // Executa as validações
        ValidationResult resultDadosPessoais = dadosPessoaisValidator.validate(request);
        ValidationResult resultDadosFinanceiros = dadosFinanceirosValidator.validate(request);
        
        // Combina os resultados
        ValidationResult resultFinal = resultDadosPessoais.and(resultDadosFinanceiros);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", resultFinal.isValid());
        response.put("errors", resultFinal.getErrors());
        response.put("dadosPessoaisValid", resultDadosPessoais.isValid());
        response.put("dadosFinanceirosValid", resultDadosFinanceiros.isValid());
        response.put("message", resultFinal.isValid() ? "Todos os dados são válidos" : "Alguns dados são inválidos");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Exemplo de validação com tratamento de exceção customizada
     */
    @PostMapping("/validacao-excecao-customizada")
    public ResponseEntity<Map<String, Object>> validacaoExcecaoCustomizada(@RequestBody ContaRequest request) {
        try {
            // Valida e lança exceção customizada se falhar
            ContaFluentValidator.validate(request).orThrows(() -> 
                new RuntimeException("Dados da conta estão inválidos. Verifique os campos obrigatórios.")
            );
            
            // Se chegou aqui, a validação passou
            ContaResponse contaResponse = processarCriacaoConta(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Conta criada com sucesso usando exceção customizada");
            response.put("data", contaResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("exceptionType", e.getClass().getSimpleName());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Exemplo de validação usando ValidationRule existente
     */
    @PostMapping("/validacao-com-validation-rule")
    public ResponseEntity<Map<String, Object>> validacaoComValidationRule(@RequestBody ContaRequest request) {
        // Cria regras de validação reutilizáveis
        ValidationRule<ContaRequest> nomeRule = ValidationRule.of(
            conta -> conta.getNome() != null && !conta.getNome().trim().isEmpty(),
            ValidationMessage.CPF_OBRIGATORIO // Usando uma mensagem existente como exemplo
        );
        
        ValidationRule<ContaRequest> emailRule = ValidationRule.of(
            conta -> conta.getEmail() != null && conta.getEmail().contains("@"),
            ValidationMessage.EMAIL_OBRIGATORIO
        );
        
        ValidationRule<ContaRequest> salarioRule = ValidationRule.of(
            conta -> conta.getSalario() == null || conta.getSalario() >= 0,
            ValidationMessage.SALARIO_INVALIDO
        );

        // Cria validador usando ValidationRule existentes
        CustomValidator<ContaRequest> validator = CustomValidator.<ContaRequest>create()
                .addRule(nomeRule)
                .addRule(emailRule)
                .addRule(salarioRule)
                .addRule(
                    conta -> conta.getTelefone() != null && conta.getTelefone().contains("("),
                    "telefone",
                    "Telefone deve ter formato brasileiro"
                );

        // Executa a validação
        ValidationResult result = validator.validate(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("errors", result.getErrors());
        response.put("message", result.isValid() ? "Dados válidos usando ValidationRule" : "Dados inválidos");
        response.put("validationMethod", "ValidationRule + CustomValidator");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Exemplo de validação assíncrona
     */
    @PostMapping("/validacao-assincrona")
    public ResponseEntity<Map<String, Object>> validacaoAssincrona(@RequestBody ContaRequest request) {
        // Cria validador assíncrono
        AsyncCustomValidator<ContaRequest> asyncValidator = new AsyncCustomValidator<>();
        
        // Adiciona regras de validação
        asyncValidator
            .notNull(ContaRequest::getNome, "nome", "Nome é obrigatório")
            .notEmpty(ContaRequest::getEmail, "email", "Email é obrigatório")
            .matches(ContaRequest::getEmail, Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$"), "email", "Email deve ter formato válido")
            .notNull(ContaRequest::getCpf, "cpf", "CPF é obrigatório")
            .minValue(ContaRequest::getSalario, 0.0, "salario", "Salário deve ser maior que zero");

        try {
            // Executa validação assíncrona
            CompletableFuture<ValidationResult> future = asyncValidator.validateAsync(request);
            ValidationResult result = future.get(); // Aguarda o resultado
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", result.isValid());
            response.put("errors", result.getErrors());
            response.put("message", result.isValid() ? "Dados válidos (validação assíncrona)" : "Dados inválidos");
            response.put("validationMethod", "AsyncCustomValidator");
            response.put("executionType", "Asynchronous");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Erro durante validação assíncrona: " + e.getMessage());
            response.put("validationMethod", "AsyncCustomValidator");
            response.put("executionType", "Asynchronous");
            
            return ResponseEntity.badRequest().body(response);
        } finally {
            // Fecha o executor
            asyncValidator.shutdown();
        }
    }

    /**
     * Exemplo de validação usando ContaCustomValidator
     */
    @PostMapping("/validacao-custom-validator")
    public ResponseEntity<Map<String, Object>> validacaoCustomValidator(@RequestBody ContaRequest request) {
        // Usa o ContaCustomValidator que estende a classe abstrata
        ContaCustomValidator validator = new ContaCustomValidator();
        
        // Executa a validação
        ValidationResult result = validator.validate(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("errors", result.getErrors());
        response.put("message", result.isValid() ? "Dados válidos usando ContaCustomValidator" : "Dados inválidos");
        response.put("validationMethod", "ContaCustomValidator (extends CustomValidator)");
        response.put("totalErrors", result.getErrors().size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Exemplo de validação usando método estático do ContaCustomValidator
     */
    @PostMapping("/validacao-custom-validator-static")
    public ResponseEntity<Map<String, Object>> validacaoCustomValidatorStatic(@RequestBody ContaRequest request) {
        // Usa o método estático de conveniência
        ValidationResult result = ContaCustomValidator.validateConta(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("errors", result.getErrors());
        response.put("message", result.isValid() ? "Dados válidos usando método estático" : "Dados inválidos");
        response.put("validationMethod", "ContaCustomValidator.validateConta()");
        response.put("totalErrors", result.getErrors().size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simula o processamento da criação da conta
     */
    private ContaResponse processarCriacaoConta(ContaRequest request) {
        // Simula processamento
        return ContaResponse.builder()
                .numeroConta("CONTA-" + System.currentTimeMillis())
                .agencia("0001")
                .nome(request.getNome())
                .cpf(request.getCpf())
                .cnpj(request.getCnpj())
                .status("ATIVA")
                .mensagem("Conta criada com sucesso")
                .build();
    }
} 