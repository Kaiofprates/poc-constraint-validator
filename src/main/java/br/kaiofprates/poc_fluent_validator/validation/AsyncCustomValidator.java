package br.kaiofprates.poc_fluent_validator.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Implementação assíncrona do CustomValidator
 * Executa validações em paralelo para melhor performance
 */
public class AsyncCustomValidator<T> extends CustomValidator<T> {
    private final ExecutorService executor;

    public AsyncCustomValidator() {
        super();
        this.executor = Executors.newFixedThreadPool(4);
    }

    public AsyncCustomValidator(ExecutorService executor) {
        super();
        this.executor = executor;
    }

    @Override
    protected ValidationResult validateCustom(T object) {
        // Implementação assíncrona não adiciona validações customizadas específicas
        return ValidationResult.success();
    }

    @Override
    public ValidationResult validate(T object) {
        if (object == null) {
            return ValidationResult.success();
        }

        List<ValidationError> errors = new ArrayList<>();

        // Valida regras críticas primeiro (síncrono para fail-fast)
        for (ValidationRule<T> rule : criticalRules) {
            if (!rule.getPredicate().test(object)) {
                errors.add(rule.getValidationError());
                return ValidationResult.failure(errors);
            }
        }

        // Valida regras normais em paralelo
        List<CompletableFuture<ValidationError>> futures = new ArrayList<>();
        
        for (ValidationRule<T> rule : rules) {
            CompletableFuture<ValidationError> future = CompletableFuture.supplyAsync(() -> {
                if (!rule.getPredicate().test(object)) {
                    return rule.getValidationError();
                }
                return null;
            }, executor);
            futures.add(future);
        }

        // Aguarda todas as validações
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // Coleta os erros
        for (CompletableFuture<ValidationError> future : futures) {
            try {
                ValidationError error = future.get();
                if (error != null) {
                    errors.add(error);
                }
            } catch (Exception e) {
                // Em caso de erro na execução assíncrona, adiciona erro genérico
                errors.add(new ValidationError("validation", "Erro durante validação assíncrona: " + e.getMessage()));
            }
        }

        // Chama método abstrato para validações customizadas específicas
        ValidationResult customResult = validateCustom(object);
        if (customResult.hasErrors()) {
            errors.addAll(customResult.getErrors());
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }

    /**
     * Método para validar de forma assíncrona
     */
    public CompletableFuture<ValidationResult> validateAsync(T object) {
        return CompletableFuture.supplyAsync(() -> validate(object), executor);
    }

    /**
     * Método para fechar o executor
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
} 