package com.example.caching_system.strategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class LFUCacheStrategy<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map = new ConcurrentHashMap<>();
    private final TreeMap<Integer, LinkedHashSet<Node<K, V>>> freqMap = new TreeMap<>();

    public LFUCacheStrategy(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void put(K key, V value) {
        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value;
            incrementFrequency(node);
        } else {
            if (map.size() >= capacity) {
                evictLFU();
            }
            Node<K, V> newNode = new Node<>(key, value, 1);
            map.put(key, newNode);
            freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(newNode);
        }
    }

    @Override
    public V get(K key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node<K, V> node = map.get(key);
        incrementFrequency(node);
        return node.value;
    }

    @Override
    public void delete(K key) {
        Node<K, V> node = map.remove(key);
        if (node != null) {
            freqMap.get(node.freq).remove(node);
            if (freqMap.get(node.freq).isEmpty()) {
                freqMap.remove(node.freq);
            }
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    private void incrementFrequency(Node<K, V> node) {
        freqMap.get(node.freq).remove(node);
        if (freqMap.get(node.freq).isEmpty()) {
            freqMap.remove(node.freq);
        }
        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }

    private void evictLFU() {
        Map.Entry<Integer, LinkedHashSet<Node<K, V>>> entry = freqMap.firstEntry();
        Node<K, V> lfu = entry.getValue().iterator().next();
        entry.getValue().remove(lfu);
        if (entry.getValue().isEmpty()) {
            freqMap.remove(entry.getKey());
        }
        map.remove(lfu.key);
    }

    private static class Node<K, V> {
        K key;
        V value;
        int freq;

        Node(K key, V value, int freq) {
            this.key = key;
            this.value = value;
            this.freq = freq;
        }
    }
}
