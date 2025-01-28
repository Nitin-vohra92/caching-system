package com.example.caching_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CacheEntity {

    @Id
    @Column(name = "`key`")
    private String key;

    private String value;

    @Column(name = "`timestamp`")
    private long timestamp;

    public CacheEntity() {}

    public CacheEntity(String key, String value, long timestamp) {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
