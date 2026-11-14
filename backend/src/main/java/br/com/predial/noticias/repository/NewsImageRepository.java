package br.com.predial.noticias.repository;

import br.com.predial.noticias.entity.NewsImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsImageRepository extends JpaRepository<NewsImage, String> {
}
