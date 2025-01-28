package com.example.caching_system.controller;

import com.example.caching_system.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService<String, String> cacheService;

    @Autowired
    public CacheController(CacheService<String, String> cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/{key}")
    public String get(@PathVariable String key) {
        String result = cacheService.get(key);
        return result != null ? result : "Cache miss!";
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

    @GetMapping ("/")
    public String getAll(){
        return cacheService.toString();
    }

}

