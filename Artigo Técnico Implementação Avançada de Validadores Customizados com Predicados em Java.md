# Artigo Técnico: Implementação Avançada de Validadores Customizados com Predicados em Java

## Introdução

A validação de dados é um aspecto crucial no desenvolvimento de software robusto e confiável. Em ecossistemas Java, especialmente com o uso de frameworks como o Spring Boot, o Bean Validation API (agora Jakarta Bean Validation) oferece um mecanismo poderoso e extensível para definir e aplicar regras de validação. No entanto, cenários complexos frequentemente demandam validações mais sofisticadas que vão além das anotações padrão. Este artigo explora uma implementação avançada de validadores customizados, utilizando `ConstraintValidator` em conjunto com predicados (`Predicate`) para criar regras de validação fluentes e dinâmicas, com base no projeto `poc-constraint-validator` disponível no GitHub. Analisaremos como essa abordagem permite uma maior flexibilidade e legibilidade na definição de lógicas de validação complexas, especialmente em objetos de requisição (DTOs) com múltiplas interdependências de dados.


## A Anatomia da Validação Customizada no Projeto

A implementação no repositório `poc-constraint-validator` demonstra uma abordagem elegante para construir validações complexas e reutilizáveis. O ponto central é a criação de uma anotação de validação customizada, `@ContaValidation`, que é então processada por um `ConstraintValidator` específico, o `ContaValidationValidator`.

### A Anotação `@ContaValidation`

Tudo começa com a definição da anotação `@ContaValidation`. Esta anotação é a porta de entrada para o mecanismo de validação customizado e é aplicada em nível de classe (no DTO `ContaRequest`, por exemplo). Sua definição é concisa, mas fundamental:

```java
package br.kaiofprates.poc_fluent_validator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContaValidationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContaValidation {
    String message() default "Erro na validação da conta";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

Os elementos chave aqui são:

*   `@Constraint(validatedBy = ContaValidationValidator.class)`: Esta meta-anotação é o coração da integração com o Bean Validation. Ela especifica que a lógica de validação para `@ContaValidation` será implementada pela classe `ContaValidationValidator`.
*   `@Target({ElementType.TYPE})`: Indica que esta anotação pode ser aplicada apenas a tipos (classes, interfaces, enums).
*   `@Retention(RetentionPolicy.RUNTIME)`: Garante que a anotação esteja disponível em tempo de execução para que o framework de validação possa processá-la.
*   `message()`, `groups()`, e `payload()`: São atributos padrão de anotações de constraint, permitindo a customização de mensagens de erro, agrupamento de validações e transporte de metadados adicionais.

### O Validador: `ContaValidationValidator`

A classe `ContaValidationValidator` implementa a interface `ConstraintValidator<ContaValidation, ContaRequest>`. Isso significa que ela é responsável por validar objetos do tipo `ContaRequest` quando anotados com `@ContaValidation`. A lógica principal reside no método `isValid`:

```java
package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContaValidationValidator implements ConstraintValidator<ContaValidation, ContaRequest> {

    private final CartaoValidator cartaoValidator = new CartaoValidator();
    private final ChavePixValidator chavePixValidator = new ChavePixValidator();

    @Override
    public boolean isValid(ContaRequest request, ConstraintValidatorContext context) {
        return ValidationBuilder.<ContaRequest>of(context)
                // Validações da conta
                .addRule(ValidationRule.of(
                        conta -> conta.getNome() == null || conta.getNome().length() <= 50,
                        ValidationMessage.NOME_TAMANHO_MAXIMO
                ))
                .addRule(ValidationRule.of(
                        conta -> conta.getCnpj() == null || conta.getCnpj().matches("^[a-zA-Z0-9]*$"),
                        ValidationMessage.CNPJ_ALFANUMERICO
                ))
                // Validações dos cartões usando o validador específico
                .addValidator(cartaoValidator, request.getCartoes())
                // Validações das chaves PIX usando o validador específico
                .addValidator(chavePixValidator, request.getChavesPix())
                .validate(request);
    }
}
```

Este validador utiliza um `ValidationBuilder` para construir uma cadeia de regras de validação de forma fluente. Observamos o seguinte:

1.  **Instanciação de Validadores Auxiliares**: `CartaoValidator` e `ChavePixValidator` são instanciados para delegar a validação de partes específicas do objeto `ContaRequest` (listas de cartões e chaves PIX, respectivamente). Isso promove a modularidade e reutilização da lógica de validação.
2.  **Uso do `ValidationBuilder`**: O `ValidationBuilder.of(context)` inicia a construção da cadeia de validação. O `ConstraintValidatorContext` é passado para permitir a customização de mensagens de erro e a desabilitação da mensagem padrão, se necessário.
3.  **Definição de Regras com Predicados (`ValidationRule.of`)**: As regras de validação são definidas usando `addRule`. Cada regra é encapsulada por `ValidationRule.of()`, que recebe dois argumentos principais:
    *   Um `Predicate<T>`: Uma expressão lambda que define a lógica da validação. Por exemplo, `conta -> conta.getNome() == null || conta.getNome().length() <= 50` verifica se o nome da conta é nulo ou se seu comprimento não excede 50 caracteres. O predicado deve retornar `true` se a condição de validação for atendida (ou seja, se o dado for válido para aquela regra específica) e `false` caso contrário. No entanto, a lógica no `ValidationBuilder` provavelmente inverte isso para adicionar uma violação quando o predicado é `false` (indicando uma falha na validação).
    *   Uma `ValidationMessage`: Um enum ou constante que representa a mensagem de erro a ser associada caso a regra falhe. Por exemplo, `ValidationMessage.NOME_TAMANHO_MAXIMO`.
4.  **Delegação para Validadores Especializados (`addValidator`)**: O método `addValidator` permite integrar outros validadores na cadeia. Ele recebe uma instância do validador (como `cartaoValidator`) e a coleção de objetos a ser validada por ele (como `request.getCartoes()`). Isso é crucial para validar estruturas aninhadas ou listas de objetos complexos, cada um com suas próprias regras.
5.  **Execução da Validação (`validate`)**: Finalmente, o método `validate(request)` é chamado no `ValidationBuilder`, passando o objeto `ContaRequest` a ser validado. Este método executa todas as regras e validadores adicionados, coletando quaisquer violações de constraint.

Essa abordagem com predicados customizados dentro do `ValidationRule` oferece uma maneira expressiva e concisa de definir lógicas de validação diretamente no fluxo de construção do validador, tornando o código mais legível e fácil de manter, especialmente quando comparado a múltiplas declarações `if/else` ou à necessidade de criar classes de validação separadas para cada pequena regra.


## O Poder dos Predicados e do `ValidationBuilder`

A beleza desta abordagem reside na flexibilidade e expressividade do `ValidationBuilder` e no uso de predicados. O `ValidationBuilder` permite uma construção fluente e declarativa das regras de validação. Cada chamada a `.addRule()` ou `.addValidator()` adiciona um novo passo na lógica de validação sem poluir o código com estruturas condicionais aninhadas.

Os predicados (`java.util.function.Predicate`) são interfaces funcionais que representam uma função booleana de um argumento. No contexto da validação, eles permitem que a lógica de uma regra específica seja definida de forma concisa como uma expressão lambda. Por exemplo:

`conta -> conta.getNome() != null && !conta.getNome().isBlank() && conta.getNome().length() >= 3 && conta.getNome().length() <= 50`

Este predicado verifica se o nome da conta não é nulo, não está em branco e tem entre 3 e 50 caracteres. Se o predicado retornar `false` (indicando que a validação falhou para aquela regra), o `ValidationBuilder` tipicamente registra uma violação de constraint, associando-a à mensagem de erro correspondente (como `ValidationMessage.NOME_INVALIDO`).

O arquivo `ValidationRule.java` provavelmente define a estrutura que encapsula o predicado e a mensagem de erro, e o `ValidationBuilder.java` orquestra a execução dessas regras. A capacidade de adicionar validadores inteiros (como `CartaoValidator` e `ChavePixValidator`) através de `addValidator` é particularmente poderosa para decompor validações complexas em unidades menores e mais gerenciáveis. Cada um desses validadores internos pode, por sua vez, usar o mesmo padrão de `ValidationBuilder` e predicados, promovendo um design consistente em toda a camada de validação.

## Conclusão

A implementação de validadores customizados utilizando `ConstraintValidator` e predicados, como demonstrado no repositório `poc-constraint-validator`, oferece uma alternativa robusta e elegante às abordagens de validação tradicionais em Java. Ao combinar a extensibilidade do Jakarta Bean Validation com o poder das interfaces funcionais e um design fluente (fluent builder pattern), é possível criar regras de validação complexas que são ao mesmo tempo legíveis, manuteníveis e altamente reutilizáveis.

Esta técnica é especialmente valiosa em aplicações que lidam com modelos de dados intrincados, onde as validações podem depender de múltiplas condições e interações entre diferentes campos ou objetos aninhados. A clareza proporcionada pelos predicados e a organização modular incentivada pelo `ValidationBuilder` contribuem significativamente para a qualidade e a robustez do código de validação, facilitando a vida dos desenvolvedores e garantindo a integridade dos dados da aplicação. A adoção de tais padrões avançados de validação é um indicativo de maturidade no desenvolvimento de software e uma prática recomendada para sistemas que exigem um alto grau de confiabilidade.
