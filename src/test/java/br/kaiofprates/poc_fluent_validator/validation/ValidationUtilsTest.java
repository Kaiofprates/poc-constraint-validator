package br.kaiofprates.poc_fluent_validator.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

class ValidationUtilsTest {

    @Nested
    @DisplayName("Testes para ConditionalValidator")
    class ConditionalValidatorTests {
        
        @Test
        @DisplayName("deve retornar true quando valor é null")
        void quandoNaoNulo_ComValorNull_DeveRetornarTrue() {
            ValidationUtils.ConditionalValidator validator = ValidationUtils.quandoNaoNulo(null);
            assertTrue(validator.entao(() -> false));
        }
        
        @Test
        @DisplayName("deve retornar true quando condição é true")
        void quandoNaoNulo_ComCondicaoTrue_DeveRetornarTrue() {
            ValidationUtils.ConditionalValidator validator = ValidationUtils.quandoNaoNulo("teste");
            assertTrue(validator.entao(() -> true));
        }
        
        @Test
        @DisplayName("deve retornar false quando valor não é null e condição é false")
        void quandoNaoNulo_ComCondicaoFalse_DeveRetornarFalse() {
            ValidationUtils.ConditionalValidator validator = ValidationUtils.quandoNaoNulo("teste");
            assertFalse(validator.entao(() -> false));
        }
        
        @Test
        @DisplayName("deve funcionar com Boolean direto")
        void entao_ComBooleanDireto_DeveFuncionar() {
            ValidationUtils.ConditionalValidator validator = ValidationUtils.quandoNaoNulo("teste");
            assertTrue(validator.entao(true));
            assertFalse(validator.entao(false));
        }
    }

    @Nested
    @DisplayName("Testes para validações básicas")
    class ValidacoesBasicasTests {
        
        @Test
        @DisplayName("naoDeveSerNulo deve retornar true para valores não nulos")
        void naoDeveSerNulo_ComValorNaoNull_DeveRetornarTrue() {
            assertTrue(ValidationUtils.naoDeveSerNulo("teste"));
            assertTrue(ValidationUtils.naoDeveSerNulo(123));
            assertTrue(ValidationUtils.naoDeveSerNulo(new Object()));
        }
        
        @Test
        @DisplayName("naoDeveSerNulo deve retornar false para valores nulos")
        void naoDeveSerNulo_ComValorNull_DeveRetornarFalse() {
            assertFalse(ValidationUtils.naoDeveSerNulo(null));
        }
        
        @Test
        @DisplayName("deveSerNulo deve retornar true para valores nulos")
        void deveSerNulo_ComValorNull_DeveRetornarTrue() {
            assertTrue(ValidationUtils.deveSerNulo(null));
        }
        
        @Test
        @DisplayName("deveSerNulo deve retornar false para valores não nulos")
        void deveSerNulo_ComValorNaoNull_DeveRetornarFalse() {
            assertFalse(ValidationUtils.deveSerNulo("teste"));
            assertFalse(ValidationUtils.deveSerNulo(123));
        }
        
        @Test
        @DisplayName("deveSerIgual deve retornar true para valores iguais")
        void deveSerIgual_ComValoresIguais_DeveRetornarTrue() {
            assertTrue(ValidationUtils.deveSerIgual("teste", "teste"));
            assertTrue(ValidationUtils.deveSerIgual(123, 123));
            assertTrue(ValidationUtils.deveSerIgual(null, null));
        }
        
        @Test
        @DisplayName("deveSerIgual deve retornar false para valores diferentes")
        void deveSerIgual_ComValoresDiferentes_DeveRetornarFalse() {
            assertFalse(ValidationUtils.deveSerIgual("teste", "outro"));
            assertFalse(ValidationUtils.deveSerIgual(123, 456));
            assertFalse(ValidationUtils.deveSerIgual("teste", null));
        }
        
        @Test
        @DisplayName("deveSerIgual com múltiplos valores deve funcionar")
        void deveSerIgual_ComMultiplosValores_DeveFuncionar() {
            assertTrue(ValidationUtils.deveSerIgual("teste", "teste", "teste", "teste"));
            assertFalse(ValidationUtils.deveSerIgual("teste", "teste", "outro"));
        }
        
        @Test
        @DisplayName("apenasUmPreenchido deve retornar true quando apenas um valor não é null")
        void apenasUmPreenchido_ComApenasUmValor_DeveRetornarTrue() {
            assertTrue(ValidationUtils.apenasUmPreenchido("teste", null, null));
            assertTrue(ValidationUtils.apenasUmPreenchido(null, 123, null));
        }
        
        @Test
        @DisplayName("apenasUmPreenchido deve retornar false quando múltiplos valores não são null")
        void apenasUmPreenchido_ComMultiplosValores_DeveRetornarFalse() {
            assertFalse(ValidationUtils.apenasUmPreenchido("teste", 123, null));
            assertFalse(ValidationUtils.apenasUmPreenchido(null, null, null));
        }
        
        @Test
        @DisplayName("peloMenosUmPreenchido deve retornar true quando pelo menos um valor não é null")
        void peloMenosUmPreenchido_ComPeloMenosUmValor_DeveRetornarTrue() {
            assertTrue(ValidationUtils.peloMenosUmPreenchido("teste", null, null));
            assertTrue(ValidationUtils.peloMenosUmPreenchido(null, 123, "teste"));
        }
        
        @Test
        @DisplayName("peloMenosUmPreenchido deve retornar false quando todos os valores são null")
        void peloMenosUmPreenchido_ComTodosNull_DeveRetornarFalse() {
            assertFalse(ValidationUtils.peloMenosUmPreenchido(null, null, null));
        }
    }

    @Nested
    @DisplayName("Testes para validações de String")
    class ValidacoesStringTests {
        
        @Test
        @DisplayName("deveSerVazio deve retornar true para strings vazias")
        void deveSerVazio_ComStringVazia_DeveRetornarTrue() {
            assertTrue(ValidationUtils.deveSerVazio(""));
            assertTrue(ValidationUtils.deveSerVazio("   "));
            assertTrue(ValidationUtils.deveSerVazio("\t\n"));
        }
        
        @Test
        @DisplayName("deveSerVazio deve retornar false para strings não vazias")
        void deveSerVazio_ComStringNaoVazia_DeveRetornarFalse() {
            assertFalse(ValidationUtils.deveSerVazio("teste"));
            assertFalse(ValidationUtils.deveSerVazio("  teste  "));
        }
        
        @Test
        @DisplayName("deveSerVazio deve retornar false para null")
        void deveSerVazio_ComNull_DeveRetornarFalse() {
            assertFalse(ValidationUtils.deveSerVazio((String) null));
        }
        
        @Test
        @DisplayName("naoDeveSerVazio deve retornar true para strings não vazias")
        void naoDeveSerVazio_ComStringNaoVazia_DeveRetornarTrue() {
            assertTrue(ValidationUtils.naoDeveSerVazio("teste"));
            assertTrue(ValidationUtils.naoDeveSerVazio("  teste  "));
        }
        
        @Test
        @DisplayName("naoDeveSerVazio deve retornar false para strings vazias")
        void naoDeveSerVazio_ComStringVazia_DeveRetornarFalse() {
            assertFalse(ValidationUtils.naoDeveSerVazio(""));
            assertFalse(ValidationUtils.naoDeveSerVazio("   "));
            assertFalse(ValidationUtils.naoDeveSerVazio((String) null));
        }
        
        @Test
        @DisplayName("deveTerTamanho deve funcionar corretamente")
        void deveTerTamanho_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveTerTamanho("teste", 5));
            assertFalse(ValidationUtils.deveTerTamanho("teste", 4));
            assertFalse(ValidationUtils.deveTerTamanho((String) null, 5));
        }
        
        @Test
        @DisplayName("deveTerTamanhoMinimo deve funcionar corretamente")
        void deveTerTamanhoMinimo_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveTerTamanhoMinimo("teste", 4));
            assertTrue(ValidationUtils.deveTerTamanhoMinimo("teste", 5));
            assertFalse(ValidationUtils.deveTerTamanhoMinimo("teste", 6));
            assertFalse(ValidationUtils.deveTerTamanhoMinimo((String) null, 5));
        }
        
        @Test
        @DisplayName("deveTerTamanhoMaximo deve funcionar corretamente")
        void deveTerTamanhoMaximo_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveTerTamanhoMaximo("teste", 6));
            assertTrue(ValidationUtils.deveTerTamanhoMaximo("teste", 5));
            assertFalse(ValidationUtils.deveTerTamanhoMaximo("teste", 4));
            assertFalse(ValidationUtils.deveTerTamanhoMaximo((String) null, 5));
        }
        
        @Test
        @DisplayName("deveTerTamanhoEntre deve funcionar corretamente")
        void deveTerTamanhoEntre_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveTerTamanhoEntre("teste", 4, 6));
            assertTrue(ValidationUtils.deveTerTamanhoEntre("teste", 5, 5));
            assertFalse(ValidationUtils.deveTerTamanhoEntre("teste", 6, 8));
            assertFalse(ValidationUtils.deveTerTamanhoEntre("teste", 1, 3));
            assertFalse(ValidationUtils.deveTerTamanhoEntre((String) null, 4, 6));
        }
        
        @Test
        @DisplayName("deveSerEmail deve validar emails corretamente")
        void deveSerEmail_DeveValidarCorretamente() {
            assertTrue(ValidationUtils.deveSerEmail("teste@email.com"));
            assertTrue(ValidationUtils.deveSerEmail("usuario.teste@dominio.com.br"));
            assertFalse(ValidationUtils.deveSerEmail("email-invalido"));
            assertFalse(ValidationUtils.deveSerEmail("@email.com"));
            assertFalse(ValidationUtils.deveSerEmail("teste@"));
            assertFalse(ValidationUtils.deveSerEmail(null));
        }
        
        @Test
        @DisplayName("deveSerCpf deve validar CPFs corretamente")
        void deveSerCpf_DeveValidarCorretamente() {
            assertTrue(ValidationUtils.deveSerCpf("12345678901"));
            assertTrue(ValidationUtils.deveSerCpf("123.456.789-01"));
            assertFalse(ValidationUtils.deveSerCpf("1234567890"));
            assertFalse(ValidationUtils.deveSerCpf("123456789012"));
            assertFalse(ValidationUtils.deveSerCpf(null));
        }
        
        @Test
        @DisplayName("deveSerCnpj deve validar CNPJs corretamente")
        void deveSerCnpj_DeveValidarCorretamente() {
            assertTrue(ValidationUtils.deveSerCnpj("12345678901234"));
            assertTrue(ValidationUtils.deveSerCnpj("12.345.678/9012-34"));
            assertFalse(ValidationUtils.deveSerCnpj("1234567890123"));
            assertFalse(ValidationUtils.deveSerCnpj("123456789012345"));
            assertFalse(ValidationUtils.deveSerCnpj(null));
        }
        
        @Test
        @DisplayName("deveSerCep deve validar CEPs corretamente")
        void deveSerCep_DeveValidarCorretamente() {
            assertTrue(ValidationUtils.deveSerCep("12345678"));
            assertTrue(ValidationUtils.deveSerCep("12345-678"));
            assertFalse(ValidationUtils.deveSerCep("1234567"));
            assertFalse(ValidationUtils.deveSerCep("123456789"));
            assertFalse(ValidationUtils.deveSerCep(null));
        }
        
        @Test
        @DisplayName("deveSerTelefone deve validar telefones corretamente")
        void deveSerTelefone_DeveValidarCorretamente() {
            assertTrue(ValidationUtils.deveSerTelefone("11987654321"));
            assertTrue(ValidationUtils.deveSerTelefone("(11) 98765-4321"));
            assertTrue(ValidationUtils.deveSerTelefone("1187654321"));
            assertFalse(ValidationUtils.deveSerTelefone("123456789"));
            assertFalse(ValidationUtils.deveSerTelefone("123456789012"));
            assertFalse(ValidationUtils.deveSerTelefone(null));
        }
    }

    @Nested
    @DisplayName("Testes para validações de Números")
    class ValidacoesNumeroTests {
        
        @Test
        @DisplayName("deveSerPositivo deve funcionar corretamente")
        void deveSerPositivo_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerPositivo(1));
            assertTrue(ValidationUtils.deveSerPositivo(100L));
            assertTrue(ValidationUtils.deveSerPositivo(1.5));
            assertTrue(ValidationUtils.deveSerPositivo(1.5f));
            assertFalse(ValidationUtils.deveSerPositivo(0));
            assertFalse(ValidationUtils.deveSerPositivo(-1));
            assertFalse(ValidationUtils.deveSerPositivo(null));
        }
        
        @Test
        @DisplayName("deveSerNegativo deve funcionar corretamente")
        void deveSerNegativo_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerNegativo(-1));
            assertTrue(ValidationUtils.deveSerNegativo(-100L));
            assertTrue(ValidationUtils.deveSerNegativo(-1.5));
            assertTrue(ValidationUtils.deveSerNegativo(-1.5f));
            assertFalse(ValidationUtils.deveSerNegativo(0));
            assertFalse(ValidationUtils.deveSerNegativo(1));
            assertFalse(ValidationUtils.deveSerNegativo(null));
        }
        
        @Test
        @DisplayName("deveSerZero deve funcionar corretamente")
        void deveSerZero_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerZero(0));
            assertTrue(ValidationUtils.deveSerZero(0L));
            assertTrue(ValidationUtils.deveSerZero(0.0));
            assertTrue(ValidationUtils.deveSerZero(0.0f));
            assertFalse(ValidationUtils.deveSerZero(1));
            assertFalse(ValidationUtils.deveSerZero(-1));
            assertFalse(ValidationUtils.deveSerZero(null));
        }
        
        @Test
        @DisplayName("deveSerMaiorQue deve funcionar corretamente")
        void deveSerMaiorQue_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerMaiorQue(5, 3));
            assertTrue(ValidationUtils.deveSerMaiorQue(5.5, 3.2));
            assertFalse(ValidationUtils.deveSerMaiorQue(3, 5));
            assertFalse(ValidationUtils.deveSerMaiorQue(3, 3));
            assertFalse(ValidationUtils.deveSerMaiorQue(null, 3));
            assertFalse(ValidationUtils.deveSerMaiorQue(3, null));
        }
        
        @Test
        @DisplayName("deveSerMenorQue deve funcionar corretamente")
        void deveSerMenorQue_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerMenorQue(3, 5));
            assertTrue(ValidationUtils.deveSerMenorQue(3.2, 5.5));
            assertFalse(ValidationUtils.deveSerMenorQue(5, 3));
            assertFalse(ValidationUtils.deveSerMenorQue(3, 3));
            assertFalse(ValidationUtils.deveSerMenorQue(null, 3));
            assertFalse(ValidationUtils.deveSerMenorQue(3, null));
        }
        
        @Test
        @DisplayName("deveEstarEntre deve funcionar corretamente")
        void deveEstarEntre_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveEstarEntre(5, 3, 7));
            assertTrue(ValidationUtils.deveEstarEntre(3, 3, 7));
            assertTrue(ValidationUtils.deveEstarEntre(7, 3, 7));
            assertFalse(ValidationUtils.deveEstarEntre(2, 3, 7));
            assertFalse(ValidationUtils.deveEstarEntre(8, 3, 7));
            assertFalse(ValidationUtils.deveEstarEntre(null, 3, 7));
            assertFalse(ValidationUtils.deveEstarEntre(5, null, 7));
            assertFalse(ValidationUtils.deveEstarEntre(5, 3, null));
        }
    }

    @Nested
    @DisplayName("Testes para validações de Coleções")
    class ValidacoesColecaoTests {
        
        @Test
        @DisplayName("deveSerVazio deve funcionar corretamente para collections")
        void deveSerVazio_ComCollection_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerVazio(Collections.emptyList()));
            assertTrue(ValidationUtils.deveSerVazio(new ArrayList<>()));
            assertFalse(ValidationUtils.deveSerVazio(Arrays.asList("teste")));
            assertFalse(ValidationUtils.deveSerVazio((java.util.Collection<?>) null));
        }
        
        @Test
        @DisplayName("naoDeveSerVazio deve funcionar corretamente para collections")
        void naoDeveSerVazio_ComCollection_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.naoDeveSerVazio(Arrays.asList("teste")));
            assertFalse(ValidationUtils.naoDeveSerVazio(Collections.emptyList()));
            assertFalse(ValidationUtils.naoDeveSerVazio((java.util.Collection<?>) null));
        }
        
        @Test
        @DisplayName("deveTerTamanho deve funcionar corretamente para collections")
        void deveTerTamanho_ComCollection_DeveFuncionarCorretamente() {
            List<String> lista = Arrays.asList("a", "b", "c");
            assertTrue(ValidationUtils.deveTerTamanho(lista, 3));
            assertFalse(ValidationUtils.deveTerTamanho(lista, 2));
            assertFalse(ValidationUtils.deveTerTamanho((java.util.Collection<?>) null, 3));
        }
        
        @Test
        @DisplayName("deveTerTamanhoMinimo deve funcionar corretamente para collections")
        void deveTerTamanhoMinimo_ComCollection_DeveFuncionarCorretamente() {
            List<String> lista = Arrays.asList("a", "b", "c");
            assertTrue(ValidationUtils.deveTerTamanhoMinimo(lista, 2));
            assertTrue(ValidationUtils.deveTerTamanhoMinimo(lista, 3));
            assertFalse(ValidationUtils.deveTerTamanhoMinimo(lista, 4));
            assertFalse(ValidationUtils.deveTerTamanhoMinimo((java.util.Collection<?>) null, 3));
        }
        
        @Test
        @DisplayName("deveTerTamanhoMaximo deve funcionar corretamente para collections")
        void deveTerTamanhoMaximo_ComCollection_DeveFuncionarCorretamente() {
            List<String> lista = Arrays.asList("a", "b", "c");
            assertTrue(ValidationUtils.deveTerTamanhoMaximo(lista, 4));
            assertTrue(ValidationUtils.deveTerTamanhoMaximo(lista, 3));
            assertFalse(ValidationUtils.deveTerTamanhoMaximo(lista, 2));
            assertFalse(ValidationUtils.deveTerTamanhoMaximo((java.util.Collection<?>) null, 3));
        }
        
        @Test
        @DisplayName("deveTerTamanhoEntre deve funcionar corretamente para collections")
        void deveTerTamanhoEntre_ComCollection_DeveFuncionarCorretamente() {
            List<String> lista = Arrays.asList("a", "b", "c");
            assertTrue(ValidationUtils.deveTerTamanhoEntre(lista, 2, 4));
            assertTrue(ValidationUtils.deveTerTamanhoEntre(lista, 3, 3));
            assertFalse(ValidationUtils.deveTerTamanhoEntre(lista, 4, 6));
            assertFalse(ValidationUtils.deveTerTamanhoEntre(lista, 1, 2));
            assertFalse(ValidationUtils.deveTerTamanhoEntre((java.util.Collection<?>) null, 2, 4));
        }
    }

    @Nested
    @DisplayName("Testes para validações de Data")
    class ValidacoesDataTests {
        
        @Test
        @DisplayName("deveSerDataFutura deve funcionar corretamente")
        void deveSerDataFutura_DeveFuncionarCorretamente() {
            LocalDate hoje = LocalDate.now();
            LocalDate amanha = hoje.plusDays(1);
            LocalDate ontem = hoje.minusDays(1);
            
            assertTrue(ValidationUtils.deveSerDataFutura(amanha));
            assertFalse(ValidationUtils.deveSerDataFutura(hoje));
            assertFalse(ValidationUtils.deveSerDataFutura(ontem));
            assertFalse(ValidationUtils.deveSerDataFutura(null));
        }
        
        @Test
        @DisplayName("deveSerDataPassada deve funcionar corretamente")
        void deveSerDataPassada_DeveFuncionarCorretamente() {
            LocalDate hoje = LocalDate.now();
            LocalDate amanha = hoje.plusDays(1);
            LocalDate ontem = hoje.minusDays(1);
            
            assertTrue(ValidationUtils.deveSerDataPassada(ontem));
            assertFalse(ValidationUtils.deveSerDataPassada(hoje));
            assertFalse(ValidationUtils.deveSerDataPassada(amanha));
            assertFalse(ValidationUtils.deveSerDataPassada(null));
        }
        
        @Test
        @DisplayName("deveSerDataHoje deve funcionar corretamente")
        void deveSerDataHoje_DeveFuncionarCorretamente() {
            LocalDate hoje = LocalDate.now();
            LocalDate amanha = hoje.plusDays(1);
            LocalDate ontem = hoje.minusDays(1);
            
            assertTrue(ValidationUtils.deveSerDataHoje(hoje));
            assertFalse(ValidationUtils.deveSerDataHoje(amanha));
            assertFalse(ValidationUtils.deveSerDataHoje(ontem));
            assertFalse(ValidationUtils.deveSerDataHoje(null));
        }
        
        @Test
        @DisplayName("deveSerDataEntre deve funcionar corretamente")
        void deveSerDataEntre_DeveFuncionarCorretamente() {
            LocalDate inicio = LocalDate.of(2023, 1, 1);
            LocalDate meio = LocalDate.of(2023, 6, 15);
            LocalDate fim = LocalDate.of(2023, 12, 31);
            LocalDate antes = LocalDate.of(2022, 12, 31);
            LocalDate depois = LocalDate.of(2024, 1, 1);
            
            assertTrue(ValidationUtils.deveSerDataEntre(meio, inicio, fim));
            assertTrue(ValidationUtils.deveSerDataEntre(inicio, inicio, fim));
            assertTrue(ValidationUtils.deveSerDataEntre(fim, inicio, fim));
            assertFalse(ValidationUtils.deveSerDataEntre(antes, inicio, fim));
            assertFalse(ValidationUtils.deveSerDataEntre(depois, inicio, fim));
            assertFalse(ValidationUtils.deveSerDataEntre(null, inicio, fim));
            assertFalse(ValidationUtils.deveSerDataEntre(meio, null, fim));
            assertFalse(ValidationUtils.deveSerDataEntre(meio, inicio, null));
        }
    }

    @Nested
    @DisplayName("Testes para validações de Padrão")
    class ValidacoesPadraoTests {
        
        @Test
        @DisplayName("deveSeguirPadrao deve funcionar corretamente")
        void deveSeguirPadrao_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSeguirPadrao("123", "\\d+"));
            assertTrue(ValidationUtils.deveSeguirPadrao("abc", "[a-z]+"));
            assertFalse(ValidationUtils.deveSeguirPadrao("abc123", "[a-z]+"));
            assertFalse(ValidationUtils.deveSeguirPadrao(null, "\\d+"));
            assertFalse(ValidationUtils.deveSeguirPadrao("123", null));
        }
        
        @Test
        @DisplayName("deveConterApenasNumeros deve funcionar corretamente")
        void deveConterApenasNumeros_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveConterApenasNumeros("123"));
            assertTrue(ValidationUtils.deveConterApenasNumeros("0"));
            assertFalse(ValidationUtils.deveConterApenasNumeros("123abc"));
            assertFalse(ValidationUtils.deveConterApenasNumeros("abc"));
            assertFalse(ValidationUtils.deveConterApenasNumeros(""));
            assertFalse(ValidationUtils.deveConterApenasNumeros(null));
        }
        
        @Test
        @DisplayName("deveConterApenasLetras deve funcionar corretamente")
        void deveConterApenasLetras_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveConterApenasLetras("abc"));
            assertTrue(ValidationUtils.deveConterApenasLetras("ABC"));
            assertTrue(ValidationUtils.deveConterApenasLetras("João"));
            assertTrue(ValidationUtils.deveConterApenasLetras("João Silva"));
            assertFalse(ValidationUtils.deveConterApenasLetras("abc123"));
            assertFalse(ValidationUtils.deveConterApenasLetras("123"));
            assertFalse(ValidationUtils.deveConterApenasLetras(""));
            assertFalse(ValidationUtils.deveConterApenasLetras(null));
        }
        
        @Test
        @DisplayName("deveConterApenasLetrasENumeros deve funcionar corretamente")
        void deveConterApenasLetrasENumeros_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveConterApenasLetrasENumeros("abc123"));
            assertTrue(ValidationUtils.deveConterApenasLetrasENumeros("ABC123"));
            assertTrue(ValidationUtils.deveConterApenasLetrasENumeros("João123"));
            assertTrue(ValidationUtils.deveConterApenasLetrasENumeros("João Silva 123"));
            assertFalse(ValidationUtils.deveConterApenasLetrasENumeros("abc@123"));
            assertFalse(ValidationUtils.deveConterApenasLetrasENumeros("abc-123"));
            assertFalse(ValidationUtils.deveConterApenasLetrasENumeros(""));
            assertFalse(ValidationUtils.deveConterApenasLetrasENumeros(null));
        }
    }

    @Nested
    @DisplayName("Testes para validações de Lógica de Negócio")
    class ValidacoesLogicaNegocioTests {
        
        @Test
        @DisplayName("deveSerPar deve funcionar corretamente")
        void deveSerPar_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerPar(2));
            assertTrue(ValidationUtils.deveSerPar(0));
            assertTrue(ValidationUtils.deveSerPar(-2));
            assertTrue(ValidationUtils.deveSerPar(100L));
            assertFalse(ValidationUtils.deveSerPar(1));
            assertFalse(ValidationUtils.deveSerPar(-1));
            assertFalse(ValidationUtils.deveSerPar(99L));
            assertFalse(ValidationUtils.deveSerPar(null));
        }
        
        @Test
        @DisplayName("deveSerImpar deve funcionar corretamente")
        void deveSerImpar_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerImpar(1));
            assertTrue(ValidationUtils.deveSerImpar(-1));
            assertTrue(ValidationUtils.deveSerImpar(99L));
            assertFalse(ValidationUtils.deveSerImpar(2));
            assertFalse(ValidationUtils.deveSerImpar(0));
            assertFalse(ValidationUtils.deveSerImpar(-2));
            assertFalse(ValidationUtils.deveSerImpar(100L));
            assertFalse(ValidationUtils.deveSerImpar(null));
        }
        
        @Test
        @DisplayName("deveSerDivisivelPor deve funcionar corretamente")
        void deveSerDivisivelPor_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerDivisivelPor(10, 2));
            assertTrue(ValidationUtils.deveSerDivisivelPor(15, 3));
            assertTrue(ValidationUtils.deveSerDivisivelPor(0, 5));
            assertFalse(ValidationUtils.deveSerDivisivelPor(10, 3));
            assertFalse(ValidationUtils.deveSerDivisivelPor(5, 0));
            assertFalse(ValidationUtils.deveSerDivisivelPor(null, 2));
            assertFalse(ValidationUtils.deveSerDivisivelPor(10, null));
        }
    }

    @Nested
    @DisplayName("Testes para validações específicas")
    class ValidacoesEspecificasTests {
        
        @Test
        @DisplayName("deveSerUrl deve funcionar corretamente")
        void deveSerUrl_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerUrl("https://www.google.com"));
            assertTrue(ValidationUtils.deveSerUrl("http://localhost:8080"));
            assertTrue(ValidationUtils.deveSerUrl("ftp://files.example.com"));
            assertFalse(ValidationUtils.deveSerUrl("not-a-url"));
            assertFalse(ValidationUtils.deveSerUrl("http://"));
            assertFalse(ValidationUtils.deveSerUrl(null));
        }
        
        @Test
        @DisplayName("deveSerIpv4 deve funcionar corretamente")
        void deveSerIpv4_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveSerIpv4("192.168.1.1"));
            assertTrue(ValidationUtils.deveSerIpv4("10.0.0.1"));
            assertTrue(ValidationUtils.deveSerIpv4("172.16.0.1"));
            assertTrue(ValidationUtils.deveSerIpv4("255.255.255.255"));
            assertTrue(ValidationUtils.deveSerIpv4("0.0.0.0"));
            assertFalse(ValidationUtils.deveSerIpv4("256.1.2.3"));
            assertFalse(ValidationUtils.deveSerIpv4("1.2.3.256"));
            assertFalse(ValidationUtils.deveSerIpv4("192.168.1"));
            assertFalse(ValidationUtils.deveSerIpv4("192.168.1.1.1"));
            assertFalse(ValidationUtils.deveSerIpv4("192.168.1.a"));
            assertFalse(ValidationUtils.deveSerIpv4(null));
        }
        
        @Test
        @DisplayName("deveTerSenhaForte deve funcionar corretamente")
        void deveTerSenhaForte_DeveFuncionarCorretamente() {
            assertTrue(ValidationUtils.deveTerSenhaForte("Senha@123"));
            assertTrue(ValidationUtils.deveTerSenhaForte("MyP@ssw0rd"));
            assertFalse(ValidationUtils.deveTerSenhaForte("senha123")); // sem maiúscula e caractere especial
            assertFalse(ValidationUtils.deveTerSenhaForte("SENHA123")); // sem minúscula e caractere especial
            assertFalse(ValidationUtils.deveTerSenhaForte("Senha123")); // sem caractere especial
            assertFalse(ValidationUtils.deveTerSenhaForte("Sen@ha")); // sem número
            assertFalse(ValidationUtils.deveTerSenhaForte("Sen@1")); // muito curta
            assertFalse(ValidationUtils.deveTerSenhaForte(null));
        }
    }
} 