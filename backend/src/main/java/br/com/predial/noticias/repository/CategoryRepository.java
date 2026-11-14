package br.com.predial.noticias.repository;

import br.com.predial.noticias.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByNameAsc();

    Optional<Category> findBySlug(String slug);

    Optional<Category> findByName(String name);
}
