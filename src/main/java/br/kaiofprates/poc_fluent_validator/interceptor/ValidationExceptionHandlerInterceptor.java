package br.kaiofprates.poc_fluent_validator.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para capturar e lançar exceções de validação customizadas após o processo de validação.
 * Este interceptor é executado após a validação do Bean Validation e antes da execução do controller.
 */
@Component
public class ValidationExceptionHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Por enquanto, apenas retorna true
        // Pode ser expandido para capturar exceções de validação específicas
        return true;
    }
} 