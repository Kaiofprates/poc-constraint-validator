package br.kaiofprates.poc_fluent_validator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContaValidationValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContaValidation {
    String message() default "Erro na validação da conta";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    ContaScope scope() default ContaScope.COMUM;
} 