package com.example.caching_system.service;

import com.example.caching_system.strategy.CacheFactory;
import com.example.caching_system.strategy.CacheStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService<K, V> {

    private final CacheFactory<K, V> cacheFactory;

    @Autowired
    public CacheService(CacheFactory<K, V> cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    public V get(K key) {
        CacheStrategy<K, V> cacheStrategy = cacheFactory.getCacheStrategy();
        return cacheStrategy.get(key);
    }

    public void put(K key, V value) {
        CacheStrategy<K, V> cacheStrategy = cacheFactory.getCacheStrategy();
        cacheStrategy.put(key, value);
    }

    public void delete(K key) {
        CacheStrategy<K, V> cacheStrategy = cacheFactory.getCacheStrategy();
        cacheStrategy.delete(key);
    }

    public int size() {
        CacheStrategy<K, V> cacheStrategy = cacheFactory.getCacheStrategy();
        return cacheStrategy.size();
    }
}
