package br.com.predial.noticias.dto;

public record NewsResponse(
        String id,
        String title,
        String category,
        String kind,
        String summary,
        String body,
        String date,
        String overlay,
        String imageId,
        boolean featured,
        boolean popular,
        int readMin,
        String createdAt,
        String updatedAt
) {
}
