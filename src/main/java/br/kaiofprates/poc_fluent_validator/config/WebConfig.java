package br.kaiofprates.poc_fluent_validator.config;

import br.kaiofprates.poc_fluent_validator.interceptor.ValidationExceptionHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração para registrar interceptors customizados.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ValidationExceptionHandlerInterceptor validationExceptionHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validationExceptionHandlerInterceptor)
                .addPathPatterns("/api/**"); // Aplica apenas nas rotas da API
    }
} 