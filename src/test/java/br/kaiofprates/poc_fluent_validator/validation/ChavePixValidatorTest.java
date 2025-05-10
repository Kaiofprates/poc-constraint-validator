package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChavePixValidatorTest {

    private ChavePixValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new ChavePixValidator();
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void deveValidarChavePixComDadosValidos() {
        // Arrange
        ChavePixRequest chavePix = criarChavePixValida();

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveInvalidarChavePixComTipoInvalido() {
        // Arrange
        ChavePixRequest chavePix = criarChavePixValida();
        chavePix.setTipo("INVALIDO");

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.TIPO_CHAVE_PIX_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarChavePixComValorInvalido() {
        // Arrange
        ChavePixRequest chavePix = criarChavePixValida();
        chavePix.setValor("email-invalido");

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.VALOR_CHAVE_PIX_INVALIDO.getMessage());
    }

    @Test
    void deveValidarChavePixNula() {
        // Act
        boolean isValid = validator.isValid(null, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveValidarChavePixComTipoCPF() {
        // Arrange
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("CPF");
        chavePix.setValor("123.456.789-00");

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveValidarChavePixComTipoCNPJ() {
        // Arrange
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("CNPJ");
        chavePix.setValor("12.345.678/0001-90");

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveValidarChavePixComTipoEMAIL() {
        // Arrange
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("EMAIL");
        chavePix.setValor("teste@email.com");

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveValidarChavePixComTipoTELEFONE() {
        // Arrange
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("TELEFONE");
        chavePix.setValor("(11) 99999-9999");

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveValidarChavePixComTipoCHAVE_ALEATORIA() {
        // Arrange
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("CHAVE_ALEATORIA");
        chavePix.setValor("a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6");

        // Act
        boolean isValid = validator.isValid(chavePix, context);

        // Assert
        assertTrue(isValid);
    }

    private ChavePixRequest criarChavePixValida() {
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("CPF");
        chavePix.setValor("123.456.789-00");
        return chavePix;
    }
} 