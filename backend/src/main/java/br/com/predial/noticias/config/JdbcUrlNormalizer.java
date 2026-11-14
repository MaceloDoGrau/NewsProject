package br.com.predial.noticias.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

final class JdbcUrlNormalizer {

    private JdbcUrlNormalizer() {
    }

    static String toJdbcUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return rawUrl;
        }
        String url = rawUrl.trim();
        if (url.startsWith("postgres://")) {
            url = "jdbc:postgresql://" + url.substring("postgres://".length());
        } else if (url.startsWith("postgresql://")) {
            url = "jdbc:postgresql://" + url.substring("postgresql://".length());
        }
        url = stripUserInfo(url);
        if (url.contains("neon.tech") && !url.contains("sslmode=")) {
            url = url + (url.contains("?") ? "&" : "?") + "sslmode=require";
        }
        return url;
    }

    static String userInfoUsername(String rawUrl) {
        String info = userInfo(rawUrl);
        if (info == null) {
            return null;
        }
        int separator = info.indexOf(':');
        String username = separator < 0 ? info : info.substring(0, separator);
        return decode(username);
    }

    static String userInfoPassword(String rawUrl) {
        String info = userInfo(rawUrl);
        if (info == null) {
            return null;
        }
        int separator = info.indexOf(':');
        if (separator < 0 || separator == info.length() - 1) {
            return null;
        }
        return decode(info.substring(separator + 1));
    }

    private static String stripUserInfo(String jdbcUrl) {
        String marker = "jdbc:postgresql://";
        if (!jdbcUrl.startsWith(marker)) {
            return jdbcUrl;
        }
        String rest = jdbcUrl.substring(marker.length());
        int at = rest.lastIndexOf('@');
        if (at < 0) {
            return jdbcUrl;
        }
        return marker + rest.substring(at + 1);
    }

    private static String userInfo(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }
        String url = rawUrl.trim();
        if (url.startsWith("jdbc:")) {
            url = url.substring("jdbc:".length());
        }
        int scheme = url.indexOf("://");
        if (scheme < 0) {
            return null;
        }
        String rest = url.substring(scheme + 3);
        int at = rest.lastIndexOf('@');
        if (at <= 0) {
            return null;
        }
        return rest.substring(0, at);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
