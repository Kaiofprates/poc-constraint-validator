package br.kaiofprates.poc_fluent_validator.controller;

import br.kaiofprates.poc_fluent_validator.dto.CartaoRequest;
import br.kaiofprates.poc_fluent_validator.dto.ChavePixRequest;
import br.kaiofprates.poc_fluent_validator.dto.ContaRequest;
import br.kaiofprates.poc_fluent_validator.dto.ContaResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContaPoupancaControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveAbrirContaPoupancaComSucesso() {
        // Arrange
        ContaRequest request = criarContaPoupancaRequestValida();
        String url = "http://localhost:" + port + "/api/contas-poupanca";

        // Act
        ResponseEntity<ContaResponse> response = restTemplate.postForEntity(url, request, ContaResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("654321", response.getBody().getNumeroConta());
        assertEquals("0001", response.getBody().getAgencia());
        assertEquals("ATIVA", response.getBody().getStatus());
        assertEquals("Conta poupança aberta com sucesso!", response.getBody().getMensagem());
    }

    @Test
    void deveRejeitarContaPoupancaComDadosInvalidos() {
        // Arrange
        ContaRequest request = criarContaPoupancaRequestInvalida();
        String url = "http://localhost:" + port + "/api/contas-poupanca";

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private ContaRequest criarContaPoupancaRequestValida() {
        ContaRequest request = new ContaRequest();
        request.setNome("Maria Silva");
        request.setCpf("987.654.321-00");
        request.setEmail("maria.silva@email.com");
        request.setTelefone("(11) 91234-5678");
        request.setEndereco("Avenida Principal, 456");
        request.setSalario(3000.00);

        List<CartaoRequest> cartoes = new ArrayList<>();
        CartaoRequest cartao = new CartaoRequest();
        cartao.setBandeira("MASTERCARD");
        cartao.setTipo("DEBITO");
        cartao.setLimite(1000.00);
        cartao.setDataValidade("12/25");
        cartoes.add(cartao);
        request.setCartoes(cartoes);

        List<ChavePixRequest> chavesPix = new ArrayList<>();
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("CPF");
        chavePix.setValor("987.654.321-00");
        chavesPix.add(chavePix);
        request.setChavesPix(chavesPix);

        return request;
    }

    private ContaRequest criarContaPoupancaRequestInvalida() {
        ContaRequest request = new ContaRequest();
        request.setNome("Maria Silva");
        request.setCpf("987.654.321-00");
        request.setEmail("email-invalido");
        request.setTelefone("123456789");
        request.setEndereco("");
        request.setSalario(-500.00);
        return request;
    }
} 