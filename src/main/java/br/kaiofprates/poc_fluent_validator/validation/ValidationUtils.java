package br.kaiofprates.poc_fluent_validator.validation;

import java.util.Objects;
import java.util.function.Supplier;

public class ValidationUtils {
    
    public static class ConditionalValidator {
        private final Object value;
        
        private ConditionalValidator(Object value) {
            this.value = value;
        }
        
        public boolean entao(Supplier<Boolean> condition) {
            return Objects.isNull(value) || condition.get();
        }
        
        public boolean entao(Boolean condition) {
            return Objects.isNull(value) || Boolean.TRUE.equals(condition);
        }
    }
    
    public static ConditionalValidator quandoNaoNulo(Object value) {
        return new ConditionalValidator(value);
    }
    
    public static boolean naoDeveSerNulo(Object value) {
        return Objects.nonNull(value);
    }
    
    public static boolean deveSerNulo(Object value) {
        return Objects.isNull(value);
    }
    
    public static boolean deveSerIgual(Object value1, Object value2) {
        return Objects.equals(value1, value2);
    }
    
    public static boolean deveSerIgual(Object value1, Object value2, Object... additionalValues) {
        if (!Objects.equals(value1, value2)) {
            return false;
        }
        for (Object additionalValue : additionalValues) {
            if (!Objects.equals(value1, additionalValue)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean apenasUmPreenchido(Object... values) {
        long count = java.util.Arrays.stream(values)
                .filter(Objects::nonNull)
                .count();
        return count == 1;
    }
    
    public static boolean peloMenosUmPreenchido(Object... values) {
        return java.util.Arrays.stream(values)
                .anyMatch(Objects::nonNull);
    }
    
    // Validações de String
    public static boolean deveSerVazio(String value) {
        return value != null && value.trim().isEmpty();
    }
    
    public static boolean naoDeveSerVazio(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean deveTerTamanho(String value, int tamanho) {
        return value != null && value.length() == tamanho;
    }
    
    public static boolean deveTerTamanhoMinimo(String value, int tamanhoMinimo) {
        return value != null && value.length() >= tamanhoMinimo;
    }
    
    public static boolean deveTerTamanhoMaximo(String value, int tamanhoMaximo) {
        return value != null && value.length() <= tamanhoMaximo;
    }
    
    public static boolean deveTerTamanhoEntre(String value, int tamanhoMinimo, int tamanhoMaximo) {
        return value != null && value.length() >= tamanhoMinimo && value.length() <= tamanhoMaximo;
    }
    
    public static boolean deveSerEmail(String value) {
        if (value == null) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return value.matches(emailRegex);
    }
    
    public static boolean deveSerCpf(String value) {
        if (value == null) return false;
        String cpfLimpo = value.replaceAll("[^0-9]", "");
        return cpfLimpo.length() == 11;
    }
    
    public static boolean deveSerCnpj(String value) {
        if (value == null) return false;
        String cnpjLimpo = value.replaceAll("[^0-9]", "");
        return cnpjLimpo.length() == 14;
    }
    
    public static boolean deveSerCep(String value) {
        if (value == null) return false;
        String cepLimpo = value.replaceAll("[^0-9]", "");
        return cepLimpo.length() == 8;
    }
    
    public static boolean deveSerTelefone(String value) {
        if (value == null) return false;
        String telefoneLimpo = value.replaceAll("[^0-9]", "");
        return telefoneLimpo.length() >= 10 && telefoneLimpo.length() <= 11;
    }
    
    // Validações de Números
    public static boolean deveSerPositivo(Number value) {
        if (value == null) return false;
        if (value instanceof Integer) return (Integer) value > 0;
        if (value instanceof Long) return (Long) value > 0;
        if (value instanceof Double) return (Double) value > 0;
        if (value instanceof Float) return (Float) value > 0;
        return false;
    }
    
    public static boolean deveSerNegativo(Number value) {
        if (value == null) return false;
        if (value instanceof Integer) return (Integer) value < 0;
        if (value instanceof Long) return (Long) value < 0;
        if (value instanceof Double) return (Double) value < 0;
        if (value instanceof Float) return (Float) value < 0;
        return false;
    }
    
    public static boolean deveSerZero(Number value) {
        if (value == null) return false;
        if (value instanceof Integer) return (Integer) value == 0;
        if (value instanceof Long) return (Long) value == 0;
        if (value instanceof Double) return (Double) value == 0.0;
        if (value instanceof Float) return (Float) value == 0.0f;
        return false;
    }
    
    public static boolean deveSerMaiorQue(Number value, Number limite) {
        if (value == null || limite == null) return false;
        return value.doubleValue() > limite.doubleValue();
    }
    
    public static boolean deveSerMenorQue(Number value, Number limite) {
        if (value == null || limite == null) return false;
        return value.doubleValue() < limite.doubleValue();
    }
    
    public static boolean deveSerMaiorOuIgualA(Number value, Number limite) {
        if (value == null || limite == null) return false;
        return value.doubleValue() >= limite.doubleValue();
    }
    
    public static boolean deveSerMenorOuIgualA(Number value, Number limite) {
        if (value == null || limite == null) return false;
        return value.doubleValue() <= limite.doubleValue();
    }
    
    public static boolean deveEstarEntre(Number value, Number minimo, Number maximo) {
        if (value == null || minimo == null || maximo == null) return false;
        double valor = value.doubleValue();
        return valor >= minimo.doubleValue() && valor <= maximo.doubleValue();
    }
    
    // Validações de Coleções
    public static boolean deveSerVazio(java.util.Collection<?> collection) {
        return collection != null && collection.isEmpty();
    }
    
    public static boolean naoDeveSerVazio(java.util.Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
    
    public static boolean deveTerTamanho(java.util.Collection<?> collection, int tamanho) {
        return collection != null && collection.size() == tamanho;
    }
    
    public static boolean deveTerTamanhoMinimo(java.util.Collection<?> collection, int tamanhoMinimo) {
        return collection != null && collection.size() >= tamanhoMinimo;
    }
    
    public static boolean deveTerTamanhoMaximo(java.util.Collection<?> collection, int tamanhoMaximo) {
        return collection != null && collection.size() <= tamanhoMaximo;
    }
    
    public static boolean deveTerTamanhoEntre(java.util.Collection<?> collection, int tamanhoMinimo, int tamanhoMaximo) {
        return collection != null && collection.size() >= tamanhoMinimo && collection.size() <= tamanhoMaximo;
    }
    
    // Validações de Data
    public static boolean deveSerDataFutura(java.time.LocalDate data) {
        return data != null && data.isAfter(java.time.LocalDate.now());
    }
    
    public static boolean deveSerDataPassada(java.time.LocalDate data) {
        return data != null && data.isBefore(java.time.LocalDate.now());
    }
    
    public static boolean deveSerDataHoje(java.time.LocalDate data) {
        return data != null && data.isEqual(java.time.LocalDate.now());
    }
    
    public static boolean deveSerDataEntre(java.time.LocalDate data, java.time.LocalDate inicio, java.time.LocalDate fim) {
        return data != null && inicio != null && fim != null && 
               !data.isBefore(inicio) && !data.isAfter(fim);
    }
    
    // Validações de Padrão
    public static boolean deveSeguirPadrao(String value, String regex) {
        return value != null && regex != null && value.matches(regex);
    }
    
    public static boolean deveConterApenasNumeros(String value) {
        return value != null && value.matches("^[0-9]+$");
    }
    
    public static boolean deveConterApenasLetras(String value) {
        return value != null && value.matches("^[a-zA-ZÀ-ÿ\\s]+$");
    }
    
    public static boolean deveConterApenasLetrasENumeros(String value) {
        return value != null && value.matches("^[a-zA-Z0-9À-ÿ\\s]+$");
    }
    
    // Validações de Lógica de Negócio
    public static boolean deveSerPar(Number value) {
        if (value == null) return false;
        if (value instanceof Integer) return (Integer) value % 2 == 0;
        if (value instanceof Long) return (Long) value % 2 == 0;
        return false;
    }
    
    public static boolean deveSerImpar(Number value) {
        if (value == null) return false;
        if (value instanceof Integer) return (Integer) value % 2 != 0;
        if (value instanceof Long) return (Long) value % 2 != 0;
        return false;
    }
    
    public static boolean deveSerDivisivelPor(Number value, Number divisor) {
        if (value == null || divisor == null) return false;
        if (divisor.doubleValue() == 0) return false;
        return value.doubleValue() % divisor.doubleValue() == 0;
    }
    
    // Validações de URL
    public static boolean deveSerUrl(String value) {
        if (value == null) return false;
        try {
            new java.net.URL(value);
            return true;
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }
    
    // Validações de IP
    public static boolean deveSerIpv4(String value) {
        if (value == null) return false;
        String ipv4Regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return value.matches(ipv4Regex);
    }
    
    // Validações de Senha
    public static boolean deveTerSenhaForte(String value) {
        if (value == null) return false;
        // Pelo menos 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial
        String senhaRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return value.matches(senhaRegex);
    }
} 