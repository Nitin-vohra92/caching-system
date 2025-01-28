package com.example.caching_system.strategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LRUCacheStrategy<K, V> implements Cache<K, V> {
    private final int capacity;
    private final long ttl;
    private final Map<K, Node<K, V>> map = new ConcurrentHashMap<>();
    private final DoublyLinkedList<K, V> dll = new DoublyLinkedList<>();
    private final ReentrantReadWriteLock lock;

    public LRUCacheStrategy(int capacity, long ttl) {
        this.capacity = capacity;
        this.ttl = ttl;
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();

            // Remove expired entries before adding a new one
            evictExpiredEntries(currentTime);

            if (map.containsKey(key)) {
                Node<K, V> node = map.get(key);
                node.value = value;
                node.timestamp = currentTime;  // Update timestamp
                dll.moveToFront(node);
            } else {
                if (map.size() >= capacity) {
                    Node<K, V> lru = dll.removeLast();
                    map.remove(lru.key);
                }
                Node<K, V> newNode = new Node<>(key, value, currentTime);
                map.put(key, newNode);
                dll.addToFront(newNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            long currentTime = System.currentTimeMillis();

            if (!map.containsKey(key)) {
                return null;
            }
            Node<K, V> node = map.get(key);

            if (isExpired(node, currentTime)) {
                return null;
            }

            dll.moveToFront(node);
            return node.value;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
            Node<K, V> node = map.remove(key);
            if (node != null) {
                dll.remove(node);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            StringBuilder result = new StringBuilder("Cache Contents:\n");
            Node<K, V> current = dll.head.next;
            while (current != dll.tail) {
                result.append("Key: ").append(current.key)
                        .append(", Value: ").append(current.value)
                        .append(", Timestamp: ").append(current.timestamp)
                        .append("\n");
                current = current.next;
            }
            return result.toString();
        } finally {
            lock.readLock().unlock();
        }
    }


    private void evictExpiredEntries(long currentTime) {
        Iterator<Map.Entry<K, Node<K, V>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Node<K, V>> entry = iterator.next();
            Node<K, V> node = entry.getValue();
            if (isExpired(node, currentTime)) {
                iterator.remove();
                dll.remove(node);
            }
        }
    }

    private boolean isExpired(Node<K, V> node, long currentTime) {
        return (currentTime - node.timestamp) > ttl;
    }

    private static class Node<K, V> {
        K key;
        V value;
        long timestamp;
        Node<K, V> prev, next;

        Node(K key, V value, long timestamp) {
            this.key = key;
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    private static class DoublyLinkedList<K, V> {
        private final Node<K, V> head = new Node<>(null, null, 0);
        private final Node<K, V> tail = new Node<>(null, null, 0);

        DoublyLinkedList() {
            head.next = tail;
            tail.prev = head;
        }

        void addToFront(Node<K, V> node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        void moveToFront(Node<K, V> node) {
            remove(node);
            addToFront(node);
        }

        void remove(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        Node<K, V> removeLast() {
            if (tail.prev == head) {
                return null;
            }
            Node<K, V> last = tail.prev;
            remove(last);
            return last;
        }
    }
}
