package com.enriquebarragan.urlshortener.service;

import com.enriquebarragan.urlshortener.model.ShortUrl;
import com.enriquebarragan.urlshortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupService {

    private final ShortUrlRepository shortUrlRepository;

    @Scheduled(cron = "0 0 * * * *") // every hour at minute 0
    public void deleteExpiredUrls() {
        List<ShortUrl> urls = shortUrlRepository.findByExpiresAtBefore(LocalDateTime.now());

        if (urls.isEmpty()) {
            log.info("Cleanup scheduled: no expired URLs found");
            return;
        }

        shortUrlRepository.deleteAll(urls);
        log.info("Cleanup scheduled: {} URLs expired deleted", urls.size());
    }
}