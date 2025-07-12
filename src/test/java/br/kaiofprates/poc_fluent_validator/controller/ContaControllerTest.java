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
class ContaControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveAbrirContaComSucesso() {
        // Arrange
        ContaRequest request = criarContaRequestValida();
        String url = "http://localhost:" + port + "/api/contas";

        // Act
        ResponseEntity<ContaResponse> response = restTemplate.postForEntity(url, request, ContaResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("123456", response.getBody().getNumeroConta());
        assertEquals("0001", response.getBody().getAgencia());
        assertEquals("ATIVA", response.getBody().getStatus());
        assertEquals("Conta aberta com sucesso!", response.getBody().getMensagem());
    }

    @Test
    void deveRejeitarContaComDadosInvalidos() {
        // Arrange
        ContaRequest request = criarContaRequestInvalida();
        String url = "http://localhost:" + port + "/api/contas";

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private ContaRequest criarContaRequestValida() {
        ContaRequest request = new ContaRequest();
        request.setNome("João Silva");
        request.setCpf("123.456.789-00");
        request.setEmail("joao.silva@email.com");
        request.setTelefone("(11) 98765-4321");
        request.setEndereco("Rua das Flores, 123");
        request.setSalario(5000.00);

        List<CartaoRequest> cartoes = new ArrayList<>();
        CartaoRequest cartao = new CartaoRequest();
        cartao.setBandeira("VISA");
        cartao.setTipo("CREDITO");
        cartao.setLimite(2000.00);
        cartao.setDataValidade("12/25");
        cartoes.add(cartao);
        request.setCartoes(cartoes);

        List<ChavePixRequest> chavesPix = new ArrayList<>();
        ChavePixRequest chavePix = new ChavePixRequest();
        chavePix.setTipo("EMAIL");
        chavePix.setValor("joao.silva@email.com");
        chavesPix.add(chavePix);
        request.setChavesPix(chavesPix);

        return request;
    }

    private ContaRequest criarContaRequestInvalida() {
        ContaRequest request = new ContaRequest();
        request.setNome("João Silva");
        request.setCpf("123.456.789-00");
        request.setEmail("email-invalido");
        request.setTelefone("123456789");
        request.setEndereco("");
        request.setSalario(-1000.00);
        return request;
    }
} 