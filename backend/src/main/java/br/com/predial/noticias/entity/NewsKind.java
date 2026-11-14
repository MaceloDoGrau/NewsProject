package br.com.predial.noticias.entity;

import java.util.Arrays;

public enum NewsKind {
    GUIA_INFORMATIVO("Guia informativo"),
    CONTEUDO_INFORMATIVO("Conteúdo informativo"),
    ORIENTACAO_TECNICA("Orientação técnica");

    private final String label;

    NewsKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static NewsKind fromLabel(String value) {
        return Arrays.stream(values())
                .filter(kind -> kind.label.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo inválido."));
    }
}
