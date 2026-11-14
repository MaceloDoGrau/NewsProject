package br.com.predial.noticias.controller;

import br.com.predial.noticias.dto.NewsPatchRequest;
import br.com.predial.noticias.dto.NewsRequest;
import br.com.predial.noticias.dto.NewsResponse;
import br.com.predial.noticias.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<NewsResponse> list() {
        return newsService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsResponse create(@Valid @RequestBody NewsRequest request, Authentication authentication) {
        return newsService.create(request, authentication);
    }

    @PutMapping("/{id}")
    public NewsResponse update(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
        return newsService.update(id, request);
    }

    @PatchMapping("/{id}")
    public NewsResponse patch(@PathVariable Long id, @RequestBody NewsPatchRequest request) {
        return newsService.patch(id, request);
    }

    @PostMapping("/{id}/publish")
    public NewsResponse publish(@PathVariable Long id) {
        return newsService.publish(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        newsService.delete(id);
    }
}
