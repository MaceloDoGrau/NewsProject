package br.com.predial.noticias.repository;

import br.com.predial.noticias.entity.News;
import br.com.predial.noticias.entity.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAllByOrderByCreatedAtDesc();

    List<News> findAllByStatusAndPublishedAtIsNotNullOrderByPublishedAtDesc(NewsStatus status);

    Optional<News> findBySlugAndStatusAndPublishedAtIsNotNull(String slug, NewsStatus status);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Modifying(flushAutomatically = true)
    @Query("update News n set n.featured = false where n.featured = true")
    int clearFeatured();

    @Modifying(flushAutomatically = true)
    @Query("update News n set n.featured = false where n.featured = true and n.id <> :id")
    int clearFeaturedExcept(@Param("id") Long id);
}
