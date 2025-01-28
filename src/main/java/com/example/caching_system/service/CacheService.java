package com.example.caching_system.service;

import com.example.caching_system.entity.CacheEntity;
import com.example.caching_system.repository.CacheRepository;
import com.example.caching_system.strategy.Cache;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CacheService<K, V> {

    private final Cache<K, V> cache;
    private final CacheRepository cacheRepository;

    @Autowired
    public CacheService(Cache<K, V> cache, CacheRepository cacheRepository) {
        this.cache = cache;
        this.cacheRepository = cacheRepository;
    }

    @PostConstruct
    public void initializeCacheFromDatabase() {
        List<CacheEntity> entities = cacheRepository.findAll();
        entities.sort(Comparator.comparingLong(CacheEntity::getTimestamp));
        entities.forEach(entity -> cache.put((K) entity.getKey(), (V) entity.getValue()));
    }

    public void put(K key, V value) {
        cache.put(key, value);
        cacheRepository.save(new CacheEntity((String) key, (String) value, System.currentTimeMillis()));
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void delete(K key) {
        cache.delete(key);
        cacheRepository.deleteById((String) key);
    }

    public int size() {
        return cache.size();
    }

    public String toString() {
        return cache.toString();
    }
}
