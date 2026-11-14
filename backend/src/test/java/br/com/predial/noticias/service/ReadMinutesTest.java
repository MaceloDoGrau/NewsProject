package br.com.predial.noticias.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReadMinutesTest {

    @Test
    void shortTextCountsAsOneMinute() {
        assertEquals(1, NewsService.readMinutes("Resumo curto", "Texto"));
    }

    @Test
    void roundsTheSameWayAsThePage() {
        String words = "palavra ".repeat(270).trim();
        assertEquals(2, NewsService.readMinutes(words, ""));
    }
}
