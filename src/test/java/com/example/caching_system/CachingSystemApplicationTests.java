package com.example.caching_system;

import com.example.caching_system.repository.CacheRepository;
import com.example.caching_system.service.CacheService;
import com.example.caching_system.strategy.Cache;
import com.example.caching_system.strategy.LRUCacheStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CachingSystemApplicationTests {
    private CacheService<String, String> cacheService;

    @Autowired
    private CacheRepository cacheRepository;

    @BeforeEach
    void setUp() {
        Cache<String, String> cache = new LRUCacheStrategy<>(3, 1000);
        cacheService = new CacheService<>(cache, cacheRepository);
    }

    @Test
    void testPutAndGet() {
        cacheService.put("A", "Apple");
        cacheService.put("B", "Banana");

        assertEquals("Apple", cacheService.get("A"));
        assertEquals("Banana", cacheService.get("B"));
    }

    @Test
    void testEvictionPolicy() {
        cacheService.put("A", "Apple");
        cacheService.put("B", "Banana");
//        assertEquals("Apple", cacheService.get("A"));

        cacheService.put("C", "Cherry");

        cacheService.put("D", "Date");

        assertNull(cacheService.get("A"));

        assertEquals("Banana", cacheService.get("B"));
    }

    @Test
    void testDelete() {
        cacheService.put("A", "Apple");
        cacheService.delete("A");

        assertNull(cacheService.get("A"));
    }

    @Test
    void testSize() {
        cacheService.put("A", "Apple");
        cacheService.put("B", "Banana");

        assertEquals(2, cacheService.size());
    }
}
