package com.example.caching_system.strategy;

public interface Cache<K, V> {
    V get(K key);

    void put(K key, V value);

    void delete(K key);

    int size();

    String toString();
}
