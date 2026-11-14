package br.com.predial.noticias.service;

import br.com.predial.noticias.dto.NewsPatchRequest;
import br.com.predial.noticias.dto.NewsRequest;
import br.com.predial.noticias.dto.NewsResponse;
import br.com.predial.noticias.entity.Category;
import br.com.predial.noticias.entity.News;
import br.com.predial.noticias.entity.NewsKind;
import br.com.predial.noticias.entity.NewsStatus;
import br.com.predial.noticias.entity.User;
import br.com.predial.noticias.exception.ResourceNotFoundException;
import br.com.predial.noticias.repository.CategoryRepository;
import br.com.predial.noticias.repository.NewsImageRepository;
import br.com.predial.noticias.repository.NewsRepository;
import br.com.predial.noticias.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final NewsImageRepository newsImageRepository;

    public NewsService(NewsRepository newsRepository, CategoryRepository categoryRepository,
                       UserRepository userRepository, NewsImageRepository newsImageRepository) {
        this.newsRepository = newsRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.newsImageRepository = newsImageRepository;
    }

    @Transactional(readOnly = true)
    public List<NewsResponse> listPublished() {
        return newsRepository.findAllByStatusAndPublishedAtIsNotNullOrderByPublishedAtDesc(NewsStatus.PUBLISHED)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NewsResponse getPublished(Long id) {
        News news = newsRepository.findById(id)
                .filter(item -> item.getStatus() == NewsStatus.PUBLISHED && item.getPublishedAt() != null)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia", id));
        return toResponse(news);
    }

    @Transactional(readOnly = true)
    public List<NewsResponse> listAll() {
        return newsRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public NewsResponse create(NewsRequest request, Authentication authentication) {
        User author = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Autor", authentication.getName()));
        if (request.featured()) {
            newsRepository.clearFeatured();
        }
        News news = new News(
                request.title().trim(),
                request.summary().trim(),
                uniqueSlug(request.title(), null),
                normalizeBody(request.body()),
                NewsKind.fromLabel(request.kind().trim()),
                request.date(),
                normalizeNullable(request.overlay()),
                normalizeImage(request.imageId()),
                request.featured(),
                request.popular(),
                readMinutes(request.body(), request.summary()),
                author,
                getCategory(request.category())
        );
        news.publish();
        return toResponse(newsRepository.saveAndFlush(news));
    }

    @Transactional
    public NewsResponse update(Long id, NewsRequest request) {
        News news = getNews(id);
        if (request.featured()) {
            newsRepository.clearFeaturedExcept(id);
        }
        news.apply(
                request.title().trim(),
                request.summary().trim(),
                uniqueSlug(request.title(), id),
                normalizeBody(request.body()),
                NewsKind.fromLabel(request.kind().trim()),
                request.date(),
                normalizeNullable(request.overlay()),
                normalizeImage(request.imageId()),
                request.featured(),
                request.popular(),
                readMinutes(request.body(), request.summary()),
                getCategory(request.category())
        );
        return toResponse(newsRepository.saveAndFlush(news));
    }

    @Transactional
    public NewsResponse patch(Long id, NewsPatchRequest request) {
        if (request.featured() == null && request.popular() == null) {
            throw new IllegalArgumentException("Informe featured ou popular.");
        }
        News news = getNews(id);
        if (Boolean.TRUE.equals(request.featured())) {
            newsRepository.clearFeaturedExcept(id);
            news.setFeatured(true);
        } else if (Boolean.FALSE.equals(request.featured())) {
            news.setFeatured(false);
        }
        if (request.popular() != null) {
            news.setPopular(request.popular());
        }
        return toResponse(newsRepository.saveAndFlush(news));
    }

    @Transactional
    public NewsResponse publish(Long id) {
        News news = getNews(id);
        news.publish();
        return toResponse(newsRepository.saveAndFlush(news));
    }

    @Transactional
    public void delete(Long id) {
        newsRepository.delete(getNews(id));
    }

    private News getNews(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notícia", id));
    }

    private Category getCategory(String name) {
        return categoryRepository.findByName(name.trim())
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida."));
    }

    private String normalizeImage(String imageId) {
        if (imageId == null || imageId.isBlank()) {
            return null;
        }
        String id = imageId.trim();
        if (!newsImageRepository.existsById(id)) {
            throw new IllegalArgumentException("Foto de capa não encontrada.");
        }
        return id;
    }

    static int readMinutes(String body, String summary) {
        String text = (normalizeBody(body) + " " + (summary == null ? "" : summary)).trim();
        if (text.isEmpty()) {
            return 1;
        }
        int words = text.split("\\s+").length;
        return Math.max(1, (int) Math.round(words / 180.0));
    }

    private String uniqueSlug(String title, Long currentNewsId) {
        String base = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.isBlank()) {
            base = "noticia";
        }
        String slug = base;
        int suffix = 2;
        while (currentNewsId == null
                ? newsRepository.existsBySlug(slug)
                : newsRepository.existsBySlugAndIdNot(slug, currentNewsId)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }

    private static String normalizeBody(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private NewsResponse toResponse(News news) {
        return new NewsResponse(
                Long.toString(news.getId()),
                news.getTitle(),
                news.getCategory().getName(),
                news.getKind().label(),
                news.getSummary(),
                news.getContent(),
                news.getPublicationDate().toString(),
                news.getOverlay(),
                news.getImageId(),
                news.isFeatured(),
                news.isPopular(),
                news.getReadMin(),
                news.getCreatedAt().toString(),
                news.getUpdatedAt().toString()
        );
    }
}
