package br.com.predial.noticias.service;

import br.com.predial.noticias.entity.NewsImage;
import br.com.predial.noticias.exception.ResourceNotFoundException;
import br.com.predial.noticias.repository.NewsImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class AssetService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private final NewsImageRepository newsImageRepository;

    public AssetService(NewsImageRepository newsImageRepository) {
        this.newsImageRepository = newsImageRepository;
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma foto de capa.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Use uma imagem JPEG, PNG, WEBP ou GIF.");
        }
        String id = UUID.randomUUID().toString();
        try {
            newsImageRepository.save(new NewsImage(id, contentType, file.getBytes(), Instant.now()));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível ler a foto de capa.");
        }
        return id;
    }

    public NewsImage get(String id) {
        return newsImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo", id));
    }
}
