package br.com.predial.noticias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "news_images")
public class NewsImage {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(name = "content_type", nullable = false, length = 80)
    private String contentType;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(nullable = false)
    private byte[] data;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected NewsImage() {
    }

    public NewsImage(String id, String contentType, byte[] data, Instant createdAt) {
        this.id = id;
        this.contentType = contentType;
        this.data = data;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }
}
