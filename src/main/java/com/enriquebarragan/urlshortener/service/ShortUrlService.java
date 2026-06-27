package com.enriquebarragan.urlshortener.service;

import com.enriquebarragan.urlshortener.dto.ShortUrlRequest;
import com.enriquebarragan.urlshortener.dto.ShortUrlResponse;
import com.enriquebarragan.urlshortener.model.ShortUrl;
import com.enriquebarragan.urlshortener.repository.ShortUrlRepository;
import com.enriquebarragan.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public ShortUrlResponse create(ShortUrlRequest request) {
        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode("temp")
                .build();

        ShortUrl saved = shortUrlRepository.save(shortUrl);

        String code = Base62Encoder.encode(saved.getId());
        saved.setShortCode(code);
        ShortUrl updated = shortUrlRepository.save(saved);

        return toResponse(updated);
    }

    public String getOriginalUrl(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Code not found: " + shortCode));

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);

        return shortUrl.getOriginalUrl();
    }

    public List<ShortUrlResponse> findAll() {
        return shortUrlRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Code not found: " + shortCode));
        shortUrlRepository.delete(shortUrl);
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl) {
        return ShortUrlResponse.builder()
                .shortCode(shortUrl.getShortCode())
                .originalUrl(shortUrl.getOriginalUrl())
                .shortUrl(baseUrl + "/r/" + shortUrl.getShortCode())
                .clickCount(shortUrl.getClickCount())
                .createdAt(shortUrl.getCreatedAt())
                .build();
    }
}