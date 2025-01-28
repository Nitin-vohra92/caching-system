package com.example.caching_system.controller;

import com.example.caching_system.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService<String, String> cacheService;
    private final Map<String, Integer> cacheMetrics;

    @Autowired
    public CacheController(CacheService<String, String> cacheService) {
        this.cacheService = cacheService;
        this.cacheMetrics = new HashMap<>();
        this.cacheMetrics.put("hits", 0);
        this.cacheMetrics.put("misses", 0);
    }

    @GetMapping("/{key}")
    public String get(@PathVariable String key) {
        String result = cacheService.get(key);
        if (result != null) {
            cacheMetrics.put("hits", cacheMetrics.get("hits") + 1); // Increment hit count
            return result;
        } else {
            cacheMetrics.put("misses", cacheMetrics.get("misses") + 1); // Increment miss count
            return "Cache miss!";
        }
    }

    @PostMapping
    public String put(@RequestParam String key, @RequestParam String value) {
        cacheService.put(key, value);
        return "Value added to cache.";
    }

    @DeleteMapping("/{key}")
    public String delete(@PathVariable String key) {
        cacheService.delete(key);
        return "Value deleted from cache.";
    }

    @GetMapping("/size")
    public String size() {
        return "Cache size: " + cacheService.size();
    }

    @GetMapping("/")
    public String getAll() {
        return cacheService.toString();
    }

    @GetMapping("/metrics")
    public String getCacheMetrics() {
        return "Cache Hits: " + cacheMetrics.get("hits") + ", Cache Misses: " + cacheMetrics.get("misses");
    }
}
