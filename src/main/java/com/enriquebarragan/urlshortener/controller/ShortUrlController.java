package com.enriquebarragan.urlshortener.controller;

import com.enriquebarragan.urlshortener.dto.ShortUrlRequest;
import com.enriquebarragan.urlshortener.dto.ShortUrlResponse;
import com.enriquebarragan.urlshortener.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "URLs", description = "URL shortener")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @PostMapping("/api/urls")
    @Operation(summary = "Shorten URL")
    public ResponseEntity<ShortUrlResponse> create(
            @Valid @RequestBody ShortUrlRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(shortUrlService.create(request));
    }

    @GetMapping("/r/{shortCode}")
    @Operation(summary = "Redirect to the original URL")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = shortUrlService.getOriginalUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/api/urls")
    @Operation(summary = "List all URLs")
    public ResponseEntity<List<ShortUrlResponse>> findAll() {
        return ResponseEntity.ok(shortUrlService.findAll());
    }

    @DeleteMapping("/api/urls/{shortCode}")
    @Operation(summary = "Delete a short URL")
    public ResponseEntity<Void> delete(@PathVariable String shortCode) {
        shortUrlService.delete(shortCode);
        return ResponseEntity.noContent().build();
    }
}