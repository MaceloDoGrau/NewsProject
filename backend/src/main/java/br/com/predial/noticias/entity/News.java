package br.com.predial.noticias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "news", uniqueConstraints = {
        @UniqueConstraint(name = "uk_news_slug", columnNames = "slug")
})
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(nullable = false, length = 300)
    private String summary;

    @Column(nullable = false, length = 180)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Convert(converter = NewsKindConverter.class)
    @Column(nullable = false, length = 40)
    private NewsKind kind;

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    @Column(length = 80)
    private String overlay;

    @Column(name = "image_id", length = 64)
    private String imageId;

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private boolean popular;

    @Column(name = "read_min", nullable = false)
    private int readMin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NewsStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant publishedAt;

    protected News() {
    }

    public News(String title, String summary, String slug, String content, NewsKind kind,
                LocalDate publicationDate, String overlay, String imageId, boolean featured,
                boolean popular, int readMin, User author, Category category) {
        this.title = title;
        this.summary = summary;
        this.slug = slug;
        this.content = content;
        this.kind = kind;
        this.publicationDate = publicationDate;
        this.overlay = overlay;
        this.imageId = imageId;
        this.featured = featured;
        this.popular = popular;
        this.readMin = readMin;
        this.author = author;
        this.category = category;
        this.status = NewsStatus.DRAFT;
    }

    public void apply(String title, String summary, String slug, String content, NewsKind kind,
                      LocalDate publicationDate, String overlay, String imageId, boolean featured,
                      boolean popular, int readMin, Category category) {
        this.title = title;
        this.summary = summary;
        this.slug = slug;
        this.content = content;
        this.kind = kind;
        this.publicationDate = publicationDate;
        this.overlay = overlay;
        this.imageId = imageId;
        this.featured = featured;
        this.popular = popular;
        this.readMin = readMin;
        this.category = category;
        publish();
    }

    public void publish() {
        if (status != NewsStatus.PUBLISHED) {
            status = NewsStatus.PUBLISHED;
            publishedAt = Instant.now();
        }
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getSlug() {
        return slug;
    }

    public String getContent() {
        return content;
    }

    public NewsKind getKind() {
        return kind;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public String getOverlay() {
        return overlay;
    }

    public String getImageId() {
        return imageId;
    }

    public boolean isFeatured() {
        return featured;
    }

    public boolean isPopular() {
        return popular;
    }

    public int getReadMin() {
        return readMin;
    }

    public User getAuthor() {
        return author;
    }

    public Category getCategory() {
        return category;
    }

    public NewsStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
