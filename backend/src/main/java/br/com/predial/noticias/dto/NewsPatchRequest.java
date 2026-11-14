package br.com.predial.noticias.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsPatchRequest(
        Boolean featured,
        Boolean popular
) {
}
