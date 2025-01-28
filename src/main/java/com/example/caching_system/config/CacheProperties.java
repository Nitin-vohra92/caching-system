package com.example.caching_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private String evictionPolicy;
    private int capacity;
    private long ttl;

    // Getters and Setters
    public String getEvictionPolicy() {
        return evictionPolicy;
    }

    public void setEvictionPolicy(String evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
