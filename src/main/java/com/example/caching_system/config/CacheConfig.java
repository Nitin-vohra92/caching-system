package com.example.caching_system.config;

import com.example.caching_system.strategy.Cache;
import com.example.caching_system.strategy.LFUCacheStrategy;
import com.example.caching_system.strategy.LRUCacheStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    private final CacheProperties cacheProperties;

    public CacheConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    public Cache<String, String> cache() {
        return "LFU".equalsIgnoreCase(cacheProperties.getEvictionPolicy())
                ? new LFUCacheStrategy<>(cacheProperties.getCapacity(), cacheProperties.getTtl())
                : new LRUCacheStrategy<>(cacheProperties.getCapacity(), cacheProperties.getTtl());
    }
}