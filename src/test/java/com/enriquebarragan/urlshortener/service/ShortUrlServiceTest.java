package com.enriquebarragan.urlshortener.service;

import com.enriquebarragan.urlshortener.dto.ShortUrlRequest;
import com.enriquebarragan.urlshortener.dto.ShortUrlResponse;
import com.enriquebarragan.urlshortener.model.ShortUrl;
import com.enriquebarragan.urlshortener.repository.ShortUrlRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @InjectMocks
    private ShortUrlService shortUrlService;

    @Test
    void create_shouldGenerateShortCode() {
        ReflectionTestUtils.setField(shortUrlService, "baseUrl", "http://localhost:8080");

        ShortUrl savedWithTemp = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("temp")
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        ShortUrl savedWithCode = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("1")
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenReturn(savedWithTemp)
                .thenReturn(savedWithCode);

        ShortUrlRequest request = new ShortUrlRequest("https://www.google.com", null);
        ShortUrlResponse response = shortUrlService.create(request);

        assertThat(response.getShortCode()).isEqualTo("1");
        assertThat(response.getOriginalUrl()).isEqualTo("https://www.google.com");
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/r/1");
    }

    @Test
    void getOriginalUrl_shouldIncrementClickCount() {
        ShortUrl shortUrl = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("1")
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        when(shortUrlRepository.findByShortCode("1")).thenReturn(Optional.of(shortUrl));
        when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(shortUrl);

        String result = shortUrlService.getOriginalUrl("1");

        assertThat(result).isEqualTo("https://www.google.com");
        assertThat(shortUrl.getClickCount()).isEqualTo(1L);
    }

    @Test
    void getOriginalUrl_shouldThrowIfExpired() {
        ShortUrl shortUrl = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("1")
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(shortUrlRepository.findByShortCode("1")).thenReturn(Optional.of(shortUrl));

        assertThrows(RuntimeException.class, () -> shortUrlService.getOriginalUrl("1"));
    }

    @Test
    void getOriginalUrl_shouldThrowIfNotFound() {
        when(shortUrlRepository.findByShortCode("xyz")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> shortUrlService.getOriginalUrl("xyz"));
    }
}