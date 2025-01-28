package com.example.caching_system.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheFactory<K, V> {
    private final CacheStrategy<K, V> cacheStrategy;

    public CacheFactory(@Value("${cache.strategy:lru}") String strategy, @Value("${cache.capacity:5}") int capacity) {
        if ("lfu".equalsIgnoreCase(strategy)) {
            this.cacheStrategy = new LFUCacheStrategy<>(capacity);
        } else {
            this.cacheStrategy = new LRUCacheStrategy<>(capacity);
        }
    }

    public CacheStrategy<K, V> getCacheStrategy() {
        return cacheStrategy;
    }
}
