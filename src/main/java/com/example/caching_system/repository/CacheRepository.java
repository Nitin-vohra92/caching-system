package com.example.caching_system.repository;

import com.example.caching_system.entity.CacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheRepository extends JpaRepository<CacheEntity, String> {
    void deleteByKey(String key);
}
