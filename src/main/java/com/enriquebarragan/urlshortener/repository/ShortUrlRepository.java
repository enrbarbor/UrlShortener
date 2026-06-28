package com.enriquebarragan.urlshortener.repository;

import com.enriquebarragan.urlshortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    List<ShortUrl> findByExpiresAtBefore(LocalDateTime dateTime);
}