package com.example.caching_system.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class LFUCacheStrategy<K, V> implements CacheStrategy<K, V> {
    private final int capacity;
    private final Map<K, V> cacheMap;
    private final Map<K, Integer> frequencyMap;
    private final PriorityQueue<K> leastFrequentQueue;

    public LFUCacheStrategy(int capacity) {
        this.capacity = capacity;
        this.cacheMap = new HashMap<>();
        this.frequencyMap = new HashMap<>();
        this.leastFrequentQueue = new PriorityQueue<>((a, b) -> frequencyMap.get(a) - frequencyMap.get(b));
    }

    @Override
    public V get(K key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }

        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
        // removing the current ordering
        leastFrequentQueue.remove(key);
        // adding key with current ordering
        leastFrequentQueue.offer(key);

        return cacheMap.get(key);
    }

    @Override
    public void put(K key, V value) {
        if (cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
        } else {
            if (cacheMap.size() == capacity) {
                K leastFrequentKey = leastFrequentQueue.poll();
                cacheMap.remove(leastFrequentKey);
                frequencyMap.remove(leastFrequentKey);
            }

            cacheMap.put(key, value);
            frequencyMap.put(key, 1);
        }

        leastFrequentQueue.remove(key);
        leastFrequentQueue.offer(key);
    }

    @Override
    public void delete(K key) {
        if (cacheMap.containsKey(key)) {
            cacheMap.remove(key);
            frequencyMap.remove(key);
            leastFrequentQueue.remove(key);
        }
    }

    @Override
    public int size() {
        return cacheMap.size();
    }
}
