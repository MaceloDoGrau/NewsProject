package br.com.predial.noticias.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class LocalEnvironment {

    private LocalEnvironment() {
    }

    public static void load() {
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

    static void normalizeDatabaseProperties() {
        normalizeDatabaseProperties(
                configuredValue("DB_URL"),
                configuredValue("DATABASE_URL"),
                configuredValue("DB_USERNAME"),
                configuredValue("DB_PASSWORD"));
    }

    static void normalizeDatabaseProperties(String dbUrl, String databaseUrl, String username, String password) {
        String rawUrl = firstNonBlank(dbUrl, databaseUrl);
        if (rawUrl == null) {
            return;
        }
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

    private static String configuredValue(String name) {
        return firstNonBlank(System.getProperty(name), System.getenv(name));
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
