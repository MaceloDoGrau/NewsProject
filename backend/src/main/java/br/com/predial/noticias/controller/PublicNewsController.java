package br.com.predial.noticias.controller;

import br.com.predial.noticias.dto.NewsResponse;
import br.com.predial.noticias.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class PublicNewsController {

    private final NewsService newsService;

    public PublicNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<NewsResponse> listPublished() {
        return newsService.listPublished();
    }

    @GetMapping("/{id}")
    public NewsResponse getPublished(@PathVariable Long id) {
        return newsService.getPublished(id);
    }
}
