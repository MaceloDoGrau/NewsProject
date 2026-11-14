package br.com.predial.noticias.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NewsKindConverter implements AttributeConverter<NewsKind, String> {

    @Override
    public String convertToDatabaseColumn(NewsKind attribute) {
        return attribute == null ? null : attribute.label();
    }

    @Override
    public NewsKind convertToEntityAttribute(String dbData) {
        return dbData == null ? null : NewsKind.fromLabel(dbData);
    }
}
