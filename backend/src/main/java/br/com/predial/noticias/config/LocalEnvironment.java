package br.com.predial.noticias.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class LocalEnvironment {

    private LocalEnvironment() {
    }

    static void load() {
        for (Path candidate : List.of(
                Path.of(".env.local"),
                Path.of("backend", ".env.local"),
                Path.of("..", ".env.local"))) {
            if (Files.isRegularFile(candidate)) {
                apply(candidate);
                break;
            }
        }
        normalizeDatabaseProperties();
    }

    private static void apply(Path file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível ler o arquivo de ambiente local.");
        }
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int separator = trimmed.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            String key = trimmed.substring(0, separator).trim();
            String value = unquote(trimmed.substring(separator + 1).trim());
            if (key.isEmpty() || System.getenv(key) != null || System.getProperty(key) != null) {
                continue;
            }
            System.setProperty(key, value);
        }
    }

    private static void normalizeDatabaseProperties() {
        String rawUrl = firstNonBlank(System.getProperty("DB_URL"), System.getenv("DB_URL"));
        if (rawUrl == null) {
            return;
        }
        String username = firstNonBlank(System.getProperty("DB_USERNAME"), System.getenv("DB_USERNAME"));
        String password = firstNonBlank(System.getProperty("DB_PASSWORD"), System.getenv("DB_PASSWORD"));
        if (username == null) {
            String extracted = JdbcUrlNormalizer.userInfoUsername(rawUrl);
            if (extracted != null && !extracted.isBlank()) {
                System.setProperty("DB_USERNAME", extracted);
            }
        }
        if (password == null) {
            String extracted = JdbcUrlNormalizer.userInfoPassword(rawUrl);
            if (extracted != null) {
                System.setProperty("DB_PASSWORD", extracted);
            }
        }
        System.setProperty("DB_URL", JdbcUrlNormalizer.toJdbcUrl(rawUrl));
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char start = value.charAt(0);
            char end = value.charAt(value.length() - 1);
            if ((start == '"' && end == '"') || (start == '\'' && end == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
