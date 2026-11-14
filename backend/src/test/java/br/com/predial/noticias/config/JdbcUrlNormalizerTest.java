package br.com.predial.noticias.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class JdbcUrlNormalizerTest {

    @Test
    void convertsNeonUrlToJdbcAndRequiresSsl() {
        String jdbc = JdbcUrlNormalizer.toJdbcUrl(
                "postgresql://user:secret@ep-example.sa-east-1.aws.neon.tech/predial?sslmode=require"
        );

        assertEquals("jdbc:postgresql://ep-example.sa-east-1.aws.neon.tech/predial?sslmode=require", jdbc);
        assertFalse(jdbc.contains("secret"));
    }

    @Test
    void addsSslWhenTheNeonUrlOmitsIt() {
        String jdbc = JdbcUrlNormalizer.toJdbcUrl(
                "postgres://ep-example.sa-east-1.aws.neon.tech/predial"
        );

        assertEquals("jdbc:postgresql://ep-example.sa-east-1.aws.neon.tech/predial?sslmode=require", jdbc);
    }

    @Test
    void readsCredentialsFromUserInfoWithoutKeepingThemInTheUrl() {
        String raw = "postgresql://editor:p%40ss@ep-example.sa-east-1.aws.neon.tech/predial";

        assertEquals("editor", JdbcUrlNormalizer.userInfoUsername(raw));
        assertEquals("p@ss", JdbcUrlNormalizer.userInfoPassword(raw));
        assertNull(JdbcUrlNormalizer.userInfoPassword("jdbc:postgresql://localhost/predial"));
    }
}
