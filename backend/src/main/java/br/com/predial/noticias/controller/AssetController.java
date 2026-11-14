package br.com.predial.noticias.controller;

import br.com.predial.noticias.dto.AssetResponse;
import br.com.predial.noticias.entity.NewsImage;
import br.com.predial.noticias.service.AssetService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping(value = "/api/admin/assets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AssetResponse upload(@RequestPart("file") MultipartFile file) {
        return new AssetResponse(assetService.store(file));
    }

    @GetMapping("/_blob/{id}")
    public ResponseEntity<byte[]> blob(@PathVariable String id) {
        NewsImage image = assetService.get(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(image.getData());
    }
}
