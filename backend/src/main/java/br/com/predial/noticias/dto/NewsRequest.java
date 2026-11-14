package br.com.predial.noticias.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsRequest(
        @NotBlank @Size(max = 140) String title,
        @NotBlank @Size(max = 80) String category,
        @NotBlank @Size(max = 40) String kind,
        @NotBlank @Size(max = 300) String summary,
        String body,
        @NotNull LocalDate date,
        @Size(max = 80) String overlay,
        @Size(max = 64) String imageId,
        boolean featured,
        boolean popular
) {
}
