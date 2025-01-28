package com.example.caching_system.service;

import com.example.caching_system.strategy.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService<K, V> {

    private final Cache<K, V> cache;

    @Autowired
    public CacheService(Cache<K, V> cache) {
        this.cache = cache;
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void delete(K key) {
        cache.delete(key);
    }

    public int size() {
        return cache.size();
    }
}
