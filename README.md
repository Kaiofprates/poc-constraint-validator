# POC Fluent Validator

Este projeto demonstra uma implementação avançada de validadores customizados com predicados em Java, oferecendo duas abordagens de validação:

1. **ValidationBuilder** - Integrado com Bean Validation (ConstraintValidator)
2. **CustomValidator** - Sistema independente de validação customizada

## Características

### ValidationBuilder (Integrado com Bean Validation)
- Integração com `@Valid` e `@Validated`
- Suporte a validações customizadas com predicados
- Regras críticas que falham imediatamente
- Validação de coleções aninhadas

### CustomValidator (Independente)
- API fluente e intuitiva
- Independente do Bean Validation
- Retorna `ValidationResult` com status e lista de erros
- Suporte ao método `orThrows()` para lançar exceções
- Validação de coleções e objetos aninhados
- Regras críticas e normais
- **Arquitetura Flexível**: Interface permite múltiplas implementações
- **Validação Assíncrona**: Suporte a execução paralela para melhor performance

## Estrutura do Projeto

```
src/main/java/br/kaiofprates/poc_fluent_validator/
├── config/
│   └── WebConfig.java
├── controller/
│   ├── ContaController.java (ValidationBuilder)
│   ├── ContaFluentController.java (FluentValidator)
│   └── ContaPoupancaController.java
├── dto/
│   ├── CartaoRequest.java
│   ├── ChavePixRequest.java
│   ├── ContaRequest.java
│   ├── ContaResponse.java
│   └── RecebedorRequest.java
├── exception/
│   └── ValidationExceptionHandler.java
├── interceptor/
│   └── ValidationExceptionHandlerInterceptor.java
├── validation/
│   ├── ValidationBuilder.java (Sistema original)
│   ├── CustomValidator.java (Novo sistema)
│   ├── ValidationResult.java
│   ├── ValidationError.java
│   ├── ValidationException.java
│   ├── ContaFluentValidator.java
│   └── ... (outros validadores)
└── PocFluentValidatorApplication.java
```

## Arquitetura do CustomValidator

O `CustomValidator` foi projetado como uma **interface** para permitir múltiplas implementações:

### Implementações Disponíveis

1. **DefaultCustomValidator** - Implementação padrão síncrona
2. **AsyncCustomValidator** - Implementação assíncrona para melhor performance

### Vantagens da Arquitetura de Interface

- **Flexibilidade**: Permite diferentes estratégias de validação
- **Extensibilidade**: Fácil adição de novas implementações
- **Testabilidade**: Facilita mock e testes unitários
- **Princípio de Inversão de Dependência**: Depende de abstrações, não de implementações

## Uso do CustomValidator

### 1. Criação de um Validador

```java
CustomValidator<ContaRequest> validator = CustomValidator.<ContaRequest>create()
    .notNull(ContaRequest::getNome, "nome", "Nome é obrigatório")
    .notEmpty(ContaRequest::getEmail, "email", "Email é obrigatório")
    .matches(ContaRequest::getEmail, EMAIL_PATTERN, "email", "Email inválido")
    .minValue(ContaRequest::getSalario, 0.0, "salario", "Salário deve ser positivo")
    .maxValue(ContaRequest::getSalario, 1000000.0, "salario", "Salário muito alto");
```

### 2. Validação com Resultado

```java
ContaRequest conta = new ContaRequest();
ValidationResult result = validator.validate(conta);

if (result.isValid()) {
    // Processa a conta
    System.out.println("Conta válida!");
} else {
    // Trata os erros
    result.getErrors().forEach(error -> 
        System.out.println(error.getField() + ": " + error.getMessage())
    );
}
```

### 3. Validação com Exceção

```java
// Usando orThrows()
validator.validate(conta).orThrows();

// Usando validateAndThrow()
validator.validateAndThrow(conta, "Erro na validação da conta");

// Usando exceção customizada
validator.validate(conta).orThrows(() -> 
    new BusinessException("Dados inválidos")
);
```

### 4. Validação de Coleções

```java
CustomValidator<CartaoRequest> cartaoValidator = CustomValidator.<CartaoRequest>create()
    .notEmpty(CartaoRequest::getNumero, "numero", "Número é obrigatório")
    .maxLength(CartaoRequest::getNumero, 19, "numero", "Número muito longo");

CustomValidator<ContaRequest> contaValidator = CustomValidator.<ContaRequest>create()
    .validateCollection(
        ContaRequest::getCartoes,
        cartaoValidator,
        "cartoes",
        "Cartões inválidos"
    );
```

### 5. Regras Críticas

```java
CustomValidator<ContaRequest> validator = CustomValidator.<ContaRequest>create()
    .addCriticalRule(
        conta -> conta.getNome() != null && !conta.getNome().trim().isEmpty(),
        "nome",
        "Nome é obrigatório"
    )
    .addRule(
        conta -> conta.getEmail() != null && conta.getEmail().contains("@"),
        "email",
        "Email inválido"
    );
```

### 6. Usando ValidationRule Existente

```java
// Cria regras de validação reutilizáveis
ValidationRule<ContaRequest> nomeRule = ValidationRule.of(
    conta -> conta.getNome() != null && !conta.getNome().trim().isEmpty(),
    ValidationMessage.CPF_OBRIGATORIO
);

ValidationRule<ContaRequest> emailRule = ValidationRule.of(
    conta -> conta.getEmail() != null && conta.getEmail().contains("@"),
    ValidationMessage.EMAIL_OBRIGATORIO
);

// Usa as regras no CustomValidator
CustomValidator<ContaRequest> validator = CustomValidator.<ContaRequest>create()
    .addRule(nomeRule)
    .addRule(emailRule)
    .addRule(
        conta -> conta.getTelefone() != null && conta.getTelefone().contains("("),
        "telefone",
        "Telefone deve ter formato brasileiro"
    );
```

### 7. Validação Assíncrona

```java
// Cria validador assíncrono para melhor performance
AsyncCustomValidator<ContaRequest> asyncValidator = new AsyncCustomValidator<>();

asyncValidator
    .notNull(ContaRequest::getNome, "nome", "Nome é obrigatório")
    .notEmpty(ContaRequest::getEmail, "email", "Email é obrigatório");

// Executa validação de forma assíncrona
CompletableFuture<ValidationResult> future = asyncValidator.validateAsync(request);
ValidationResult result = future.get();

// Não esqueça de fechar o executor
asyncValidator.shutdown();
```

## Endpoints de Exemplo

### ValidationBuilder (Sistema Original)
- `POST /api/contas` - Cria conta com validação Bean Validation

### CustomValidator (Novo Sistema)
- `POST /api/v2/contas/validar` - Validação com resultado
- `POST /api/v2/contas/criar` - Validação com exceção
- `POST /api/v2/contas/criar-or-throws` - Validação usando orThrows()
- `POST /api/v2/contas/validar-apenas` - Retorna apenas o resultado da validação

## Exemplo de Resposta de Erro

```json
{
  "success": false,
  "message": "Dados inválidos",
  "errors": {
    "nome": "Nome é obrigatório",
    "email": "Email deve ter formato válido",
    "salario": "Salário deve ser maior ou igual a zero"
  }
}
```

## Vantagens do CustomValidator

1. **Independência**: Não depende do Bean Validation
2. **Flexibilidade**: API fluente e intuitiva
3. **Controle**: Você decide quando lançar exceções
4. **Reutilização**: Validadores podem ser compostos
5. **Performance**: Validação otimizada com regras críticas
6. **Testabilidade**: Fácil de testar e mockar

## Executando o Projeto

```bash
# Compilar
mvn clean compile

# Executar testes
mvn test

# Executar aplicação
mvn spring-boot:run
```

## Testes

O projeto inclui testes abrangentes para ambos os sistemas de validação:

- `ValidationBuilderTest` - Testes do sistema original
- `CustomValidatorTest` - Testes do novo sistema
- `ContaFluentValidatorTest` - Testes específicos do validador de conta

## Contribuição

Este projeto serve como POC para demonstrar diferentes abordagens de validação em Java. Sinta-se à vontade para contribuir com melhorias e novas funcionalidades. 