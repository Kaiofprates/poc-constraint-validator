# Exemplos de Uso do CustomValidator

Este documento demonstra como usar o novo sistema de validação `CustomValidator` através dos endpoints do controller.

## Endpoints Disponíveis

### 1. Validação Básica com Resultado
**Endpoint:** `POST /api/v2/contas/validar`

Retorna um resultado de validação com status e lista de erros.

**Exemplo de Request:**
```json
{
  "nome": "",
  "cpf": "123.456.789-00",
  "email": "email-invalido",
  "telefone": "(11) 99999-9999",
  "salario": -1000.0
}
```

**Exemplo de Response (Erro):**
```json
{
  "success": false,
  "message": "Dados inválidos",
  "errors": {
    "nome": "O nome é obrigatório",
    "email": "Email deve ter formato válido",
    "salario": "Salário deve ser maior ou igual a zero"
  }
}
```

**Exemplo de Response (Sucesso):**
```json
{
  "success": true,
  "message": "Conta criada com sucesso",
  "data": {
    "numeroConta": "CONTA-1700841600000",
    "agencia": "0001",
    "nome": "João Silva",
    "cpf": "123.456.789-00",
    "status": "ATIVA",
    "mensagem": "Conta criada com sucesso"
  }
}
```

### 2. Validação com Exceção
**Endpoint:** `POST /api/v2/contas/criar`

Lança uma exceção se a validação falhar.

**Exemplo de Request:**
```json
{
  "nome": "",
  "cpf": "123.456.789-00"
}
```

**Exemplo de Response (Erro):**
```json
{
  "success": false,
  "message": "Erro na validação dos dados da conta",
  "errors": {
    "nome": "O nome é obrigatório"
  }
}
```

### 3. Validação usando orThrows()
**Endpoint:** `POST /api/v2/contas/criar-or-throws`

Usa o método `orThrows()` para lançar exceção automaticamente.

**Exemplo de Request:**
```json
{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "email": "joao@email.com",
  "telefone": "(11) 99999-9999",
  "salario": 5000.0
}
```

**Exemplo de Response (Sucesso):**
```json
{
  "success": true,
  "message": "Conta criada com sucesso",
  "data": {
    "numeroConta": "CONTA-1700841600000",
    "agencia": "0001",
    "nome": "João Silva",
    "cpf": "123.456.789-00",
    "status": "ATIVA",
    "mensagem": "Conta criada com sucesso"
  }
}
```

### 4. Validação Apenas (Sem Processamento)
**Endpoint:** `POST /api/v2/contas/validar-apenas`

Retorna apenas o resultado da validação sem processar a criação da conta.

**Exemplo de Request:**
```json
{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "email": "joao@email.com"
}
```

**Exemplo de Response:**
```json
{
  "valid": true,
  "errors": []
}
```

### 5. Validação Customizada em Tempo Real
**Endpoint:** `POST /api/v2/contas/validacao-customizada`

Demonstra como criar um validador customizado para regras específicas.

**Exemplo de Request:**
```json
{
  "nome": "Jo",
  "email": "email-invalido",
  "salario": 500.0
}
```

**Exemplo de Response:**
```json
{
  "valid": false,
  "errors": [
    {
      "field": "nome",
      "message": "Nome deve ter pelo menos 3 caracteres"
    },
    {
      "field": "email",
      "message": "Email deve ter formato válido"
    },
    {
      "field": "salario",
      "message": "Salário deve ser pelo menos R$ 1.000,00"
    }
  ],
  "message": "Dados inválidos"
}
```

### 6. Validação com Regras Críticas
**Endpoint:** `POST /api/v2/contas/validacao-critica`

Demonstra o uso de regras críticas que falham imediatamente.

**Exemplo de Request:**
```json
{
  "nome": "",
  "cpf": "",
  "email": "joao@email.com",
  "salario": 5000.0
}
```

**Exemplo de Response:**
```json
{
  "valid": false,
  "errors": [
    {
      "field": "nome",
      "message": "Nome é obrigatório e crítico"
    }
  ],
  "message": "Dados inválidos",
  "criticalValidation": "Regras críticas foram validadas primeiro"
}
```

### 7. Validação Múltipla
**Endpoint:** `POST /api/v2/contas/validacao-multipla`

Demonstra como combinar múltiplos validadores.

**Exemplo de Request:**
```json
{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "email": "joao@email.com",
  "salario": -1000.0,
  "cartoes": []
}
```

**Exemplo de Response:**
```json
{
  "valid": false,
  "errors": [
    {
      "field": "salario",
      "message": "Salário deve ser não negativo"
    },
    {
      "field": "cartoes",
      "message": "Pelo menos um cartão deve ser informado"
    }
  ],
  "dadosPessoaisValid": true,
  "dadosFinanceirosValid": false,
  "message": "Alguns dados são inválidos"
}
```

### 8. Validação com Exceção Customizada
**Endpoint:** `POST /api/v2/contas/validacao-excecao-customizada`

Demonstra como usar exceções customizadas com o método `orThrows()`.

**Exemplo de Request:**
```json
{
  "nome": "",
  "cpf": "123.456.789-00"
}
```

**Exemplo de Response:**
```json
{
  "success": false,
  "message": "Dados da conta estão inválidos. Verifique os campos obrigatórios.",
  "exceptionType": "RuntimeException"
}
```

### 9. Validação usando ValidationRule
**Endpoint:** `POST /api/v2/contas/validacao-com-validation-rule`

Demonstra como usar `ValidationRule` existentes com o `CustomValidator`.

**Exemplo de Request:**
```json
{
  "nome": "João Silva",
  "email": "email-invalido",
  "telefone": "99999-9999",
  "salario": -1000.0
}
```

**Exemplo de Response:**
```json
{
  "valid": false,
  "errors": [
    {
      "field": "email",
      "message": "O email é obrigatório e deve estar em um formato válido"
    },
    {
      "field": "telefone",
      "message": "Telefone deve ter formato brasileiro"
    },
    {
      "field": "salario",
      "message": "O salário deve ser maior que zero"
    }
  ],
  "message": "Dados inválidos",
  "validationMethod": "ValidationRule + CustomValidator"
}
```

### 10. Validação Assíncrona
**Endpoint:** `POST /api/v2/contas/validacao-assincrona`

Demonstra como usar o `AsyncCustomValidator` para validações em paralelo.

**Exemplo de Request:**
```json
{
  "nome": "João Silva",
  "email": "email-invalido",
  "cpf": "123.456.789-00",
  "salario": -1000.0
}
```

**Exemplo de Response:**
```json
{
  "valid": false,
  "errors": [
    {
      "field": "email",
      "message": "Email deve ter formato válido"
    },
    {
      "field": "salario",
      "message": "Salário deve ser maior que zero"
    }
  ],
  "message": "Dados inválidos",
  "validationMethod": "AsyncCustomValidator",
  "executionType": "Asynchronous"
}
```

## Como Testar

### Usando cURL

```bash
# Teste de validação básica
curl -X POST http://localhost:8080/api/v2/contas/validar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "",
    "cpf": "123.456.789-00",
    "email": "email-invalido",
    "telefone": "(11) 99999-9999",
    "salario": -1000.0
  }'

# Teste de validação customizada
curl -X POST http://localhost:8080/api/v2/contas/validacao-customizada \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Jo",
    "email": "email-invalido",
    "salario": 500.0
  }'

# Teste de validação com regras críticas
curl -X POST http://localhost:8080/api/v2/contas/validacao-critica \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "",
    "cpf": "",
    "email": "joao@email.com"
  }'
```

### Usando Postman

1. Configure o método como `POST`
2. Use a URL: `http://localhost:8080/api/v2/contas/{endpoint}`
3. Configure o header: `Content-Type: application/json`
4. Use o body raw com JSON
5. Execute a requisição

## Vantagens Demonstradas

1. **Flexibilidade**: Cada endpoint pode ter suas próprias regras de validação
2. **Controle**: Você decide quando lançar exceções ou retornar resultados
3. **Composição**: Múltiplos validadores podem ser combinados
4. **Performance**: Regras críticas falham imediatamente
5. **Legibilidade**: API fluente e intuitiva
6. **Reutilização**: Validadores podem ser reutilizados em diferentes contextos

## Casos de Uso Reais

- **Validação de Formulários**: Use validação com resultado para mostrar erros na UI
- **Validação de API**: Use validação com exceção para respostas padronizadas
- **Validação de Negócio**: Use regras críticas para validações essenciais
- **Validação Modular**: Use múltiplos validadores para diferentes aspectos do objeto
- **Validação Customizada**: Crie validadores específicos para cada contexto 