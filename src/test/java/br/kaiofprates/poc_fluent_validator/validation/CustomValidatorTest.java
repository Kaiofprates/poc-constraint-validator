package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.CartaoRequest;
import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;
import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomValidatorTest {

    @Test
    void testValidContaRequest() {
        // Arrange
        ContaRequest conta = createValidContaRequest();

        // Act
        ValidationResult result = ContaFluentValidator.validate(conta);

        // Assert
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertEquals(0, result.getErrors().size());
    }

    @Test
    void testInvalidContaRequest() {
        // Arrange
        ContaRequest conta = createInvalidContaRequest();

        // Act
        ValidationResult result = ContaFluentValidator.validate(conta);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().size() > 0);
        
        // Verifica se pelo menos alguns erros estão presentes
        List<String> errorFields = result.getErrors().stream()
                .map(ValidationError::getField)
                .toList();
        
        // Verifica se pelo menos um erro está presente
        assertTrue(errorFields.size() > 0);
    }

    @Test
    void testValidateAndThrowSuccess() {
        // Arrange
        ContaRequest conta = createValidContaRequest();

        // Act & Assert
        assertDoesNotThrow(() -> ContaFluentValidator.validateAndThrow(conta));
    }

    @Test
    void testValidateAndThrowFailure() {
        // Arrange
        ContaRequest conta = createInvalidContaRequest();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, 
                () -> ContaFluentValidator.validateAndThrow(conta));
        
        assertNotNull(exception.getErrors());
        assertTrue(exception.getErrors().size() > 0);
    }

    @Test
    void testValidateAndThrowCustomMessage() {
        // Arrange
        ContaRequest conta = createInvalidContaRequest();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, 
                () -> ContaFluentValidator.validateAndThrow(conta, "Dados da conta inválidos"));
        
        assertEquals("Dados da conta inválidos", exception.getMessage());
    }

    @Test
    void testOrThrowsMethod() {
        // Arrange
        ContaRequest conta = createInvalidContaRequest();

        // Act
        ValidationResult result = ContaFluentValidator.validate(conta);

        // Assert
        assertThrows(ValidationException.class, () -> result.orThrows());
    }

    @Test
    void testOrThrowsCustomException() {
        // Arrange
        ContaRequest conta = createInvalidContaRequest();

        // Act
        ValidationResult result = ContaFluentValidator.validate(conta);

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> result.orThrows(() -> new RuntimeException("Erro customizado")));
        
        assertEquals("Erro customizado", exception.getMessage());
    }

    @Test
    void testGetFirstError() {
        // Arrange
        ContaRequest conta = createInvalidContaRequest();

        // Act
        ValidationResult result = ContaFluentValidator.validate(conta);
        ValidationError firstError = result.getFirstError();

        // Assert
        assertNotNull(firstError);
        assertNotNull(firstError.getField());
        assertNotNull(firstError.getMessage());
    }

    @Test
    void testAndMethod() {
        // Arrange
        ContaRequest conta1 = createValidContaRequest();
        ContaRequest conta2 = createInvalidContaRequest();

        // Act
        ValidationResult result1 = ContaFluentValidator.validate(conta1);
        ValidationResult result2 = ContaFluentValidator.validate(conta2);
        ValidationResult combined = result1.and(result2);

        // Assert
        assertFalse(combined.isValid());
        assertTrue(combined.getErrors().size() > 0);
    }

    private ContaRequest createValidContaRequest() {
        ContaRequest conta = new ContaRequest();
        conta.setNome("João Silva");
        conta.setCpf("123.456.789-00");
        conta.setEndereco("Rua das Flores, 123");
        conta.setEmail("joao@email.com");
        conta.setTelefone("(11) 99999-9999");
        conta.setSalario(5000.0);
        
        CartaoRequest cartao = new CartaoRequest();
        cartao.setNumero("1234567890123456");
        cartao.setBandeira("Visa");
        cartao.setTipo("CREDITO");
        cartao.setLimite(10000.0);
        cartao.setDataValidade("12/25");
        
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("EMAIL");
        chavePix.setValor("joao@email.com");
        
        conta.setCartoes(Arrays.asList(cartao));
        conta.setChavesPix(Arrays.asList(chavePix));
        
        return conta;
    }

    private ContaRequest createInvalidContaRequest() {
        ContaRequest conta = new ContaRequest();
        conta.setNome(""); // Nome vazio
        conta.setCpf("123.456.789-00");
        conta.setEndereco("Rua das Flores, 123");
        conta.setEmail("email-invalido"); // Email inválido
        conta.setTelefone("(11) 99999-9999");
        conta.setSalario(-1000.0); // Salário negativo
        
        return conta;
    }
} 