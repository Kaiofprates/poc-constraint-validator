package br.kaiofprates.poc_fluent_validator.validation;

import br.kaiofprates.poc_fluent_validator.dto.CartaoRequest;
import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;
import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContaValidationValidatorTest {

    private ContaValidationValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new ContaValidationValidator();
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void deveValidarContaComDadosValidos() {
        // Arrange
        ContaRequest request = criarContaRequestValida();

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void deveInvalidarContaComNomeMaiorQue50Caracteres() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.setNome("a".repeat(51));

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.NOME_TAMANHO_MAXIMO.getMessage());
    }

    @Test
    void deveInvalidarContaComCNPJInvalido() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.setCnpj("12.345.678/0001-90"); // CNPJ com caracteres especiais (inválido)

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CNPJ_ALFANUMERICO.getMessage());
    }

    @Test
    void deveInvalidarContaComCartaoInvalido() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.getCartoes().forEach(c -> c.setNumero("123456789012345")); // Número com 15 dígitos

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_NUMERO_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarContaComChavePixInvalida() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.getChavesPix().forEach(c -> c.setTipo("INVALIDO"));

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.TIPO_CHAVE_PIX_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarContaComCartaoSemBandeira() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.getCartoes().forEach(c -> c.setBandeira(null));

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_BANDEIRA_OBRIGATORIA.getMessage());
    }

    @Test
    void deveInvalidarContaComCartaoTipoInvalido() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.getCartoes().forEach(c -> c.setTipo("INVALIDO"));

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_TIPO_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarContaComCartaoLimiteInvalido() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.getCartoes().forEach(c -> c.setLimite(0.0));

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_LIMITE_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarContaComCartaoDataValidadeInvalida() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.getCartoes().forEach(c -> c.setDataValidade("13/25")); // Mês inválido

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.CARTAO_DATA_VALIDADE_INVALIDA.getMessage());
    }


    @Test
    void deveInvalidarContaComEmailInvalido() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.setEmail("email-invalido");

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.EMAIL_OBRIGATORIO.getMessage());
    }

    @Test
    void deveInvalidarContaComTelefoneInvalido() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.setTelefone("123456789"); // Telefone sem formatação

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.TELEFONE_OBRIGATORIO.getMessage());
    }

    @Test
    void deveInvalidarContaComSalarioNegativo() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.setSalario(-1000.00);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.SALARIO_INVALIDO.getMessage());
    }

    @Test
    void deveInvalidarContaComEnderecoVazio() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        request.setEndereco("");

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.ENDERECO_OBRIGATORIO.getMessage());
    }

    private ContaRequest criarContaRequestValida() {
        ContaRequest request = new ContaRequest();
        request.setNome("João Silva");
        request.setCpf("123.456.789-00");
        request.setCnpj("12345678000190");
        request.setEndereco("Rua Exemplo, 123");
        request.setSalario(5000.00);
        request.setEmail("teste@email.com");
        request.setTelefone("(11) 99999-9999");

        List<CartaoRequest> cartoes = new ArrayList<>();
        CartaoRequest cartao1 = new CartaoRequest();
        cartao1.setNumero("1234567890123456");
        cartao1.setBandeira("VISA");
        cartao1.setTipo("CREDITO");
        cartao1.setLimite(5000.00);
        cartao1.setDataValidade("12/25");
        cartoes.add(cartao1);

        CartaoRequest cartao2 = new CartaoRequest();
        cartao2.setNumero("9876543210987654");
        cartao2.setBandeira("MASTERCARD");
        cartao2.setTipo("DEBITO");
        cartao2.setLimite(1000.00);
        cartao2.setDataValidade("06/24");
        cartoes.add(cartao2);

        request.setCartoes(cartoes);

        List<ChavePixRequest> chavesPix = new ArrayList<>();
        ChavePixRequest chavePix1 = new ChavePixRequest();
        chavePix1.setTipo("CPF");
        chavePix1.setValor("123.456.789-00");
        chavesPix.add(chavePix1);

        ChavePixRequest chavePix2 = new ChavePixRequest();
        chavePix2.setTipo("EMAIL");
        chavePix2.setValor("joao@email.com");
        chavesPix.add(chavePix2);

        request.setChavesPix(chavesPix);

        return request;
    }
} 