package br.com.predial.noticias.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalEnvironmentTest {

    private static final String[] PROPERTY_NAMES = {"DB_URL", "DB_USERNAME", "DB_PASSWORD"};
    private final String[] originalValues = new String[PROPERTY_NAMES.length];

    @AfterEach
    void restoreProperties() {
        for (int i = 0; i < PROPERTY_NAMES.length; i++) {
            if (originalValues[i] == null) {
                System.clearProperty(PROPERTY_NAMES[i]);
            } else {
                System.setProperty(PROPERTY_NAMES[i], originalValues[i]);
            }
        }
    }

    @Test
    void mapsNeonDatabaseUrlToSpringDatasourceProperties() {
        for (int i = 0; i < PROPERTY_NAMES.length; i++) {
            originalValues[i] = System.getProperty(PROPERTY_NAMES[i]);
            System.clearProperty(PROPERTY_NAMES[i]);
        }

        LocalEnvironment.normalizeDatabaseProperties(
                null,
                "postgresql://editor:p%40ss@ep-example.sa-east-1.aws.neon.tech/predial?sslmode=require",
                null,
                null);

        assertEquals("jdbc:postgresql://ep-example.sa-east-1.aws.neon.tech/predial?sslmode=require",
                System.getProperty("DB_URL"));
        assertEquals("editor", System.getProperty("DB_USERNAME"));
        assertEquals("p@ss", System.getProperty("DB_PASSWORD"));
    }
}
