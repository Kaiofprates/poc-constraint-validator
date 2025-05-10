package br.kaiofprates.poc_fluent_validator.validation;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;
import java.util.regex.Pattern;

public enum ChavePixPatterns {
    CPF("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$"),
    CNPJ("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$"),
    EMAIL("^[A-Za-z0-9+_.-]+@(.+)$"),
    TELEFONE ("^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$"),
    CHAVE_ALEATORIA("^[a-zA-Z0-9]{32}$");
    ;

    private final Pattern pattern;

    ChavePixPatterns(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean isValid(String valor){
        return Strings.isNotBlank(valor) && this.pattern.matcher(valor).matches();
    }

    public static boolean exists(String chave){
        try {
            ChavePixPatterns.valueOf(chave.toUpperCase());
            return true;
        } catch (final Exception ex){
            return false;
        }
    }

    public static boolean isValidValor(String tipo, String valor) {
        if (Strings.isBlank(tipo) || Strings.isBlank(valor)){
            return false;
        }
        final var item = ChavePixPatterns.valueOf(tipo.toUpperCase());
        return Objects.nonNull(item) && item.isValid(valor);
    }

}
