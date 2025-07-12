package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.CartaoRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartaoValidatorTest {

    private CartaoValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new CartaoValidator();
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void deveValidarCartaoComDadosValidos() {
        // Arrange
        CartaoRequest cartao = criarCartaoValido();

        // Act
        boolean isValid = validator.isValid(cartao, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveInvalidarCartaoComNumeroInvalido() {
        // Arrange
        CartaoRequest cartao = criarCartaoValido();
        cartao.setNumero("123456789012345"); // 15 dígitos

        // Act
        boolean isValid = validator.isValid(cartao, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_NUMERO_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarCartaoSemBandeira() {
        // Arrange
        CartaoRequest cartao = criarCartaoValido();
        cartao.setBandeira(null);

        // Act
        boolean isValid = validator.isValid(cartao, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_BANDEIRA_OBRIGATORIA.getMessage());
    }

    @Test
    void deveInvalidarCartaoComTipoInvalido() {
        // Arrange
        CartaoRequest cartao = criarCartaoValido();
        cartao.setTipo("INVALIDO");

        // Act
        boolean isValid = validator.isValid(cartao, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_TIPO_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarCartaoComLimiteInvalido() {
        // Arrange
        CartaoRequest cartao = criarCartaoValido();
        cartao.setLimite(0.0);

        // Act
        boolean isValid = validator.isValid(cartao, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_LIMITE_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarCartaoComDataValidadeInvalida() {
        // Arrange
        CartaoRequest cartao = criarCartaoValido();
        cartao.setDataValidade("13/25"); // Mês inválido

        // Act
        boolean isValid = validator.isValid(cartao, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_DATA_VALIDADE_INVALIDA.getMessage());
    }

    @Test
    void deveValidarCartaoNulo() {
        // Act
        boolean isValid = validator.isValid(null, context);

        // Assert
        assertTrue(isValid);
    }

    private CartaoRequest criarCartaoValido() {
        CartaoRequest cartao = new CartaoRequest();
        cartao.setNumero("1234567890123456");
        cartao.setBandeira("VISA");
        cartao.setTipo("CREDITO");
        cartao.setLimite(5000.00);
        cartao.setDataValidade("12/25");
        return cartao;
    }
} 