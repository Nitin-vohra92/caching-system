package com.example.caching_system.strategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LRUCacheStrategy<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map = new ConcurrentHashMap<>();
    private final DoublyLinkedList<K, V> dll = new DoublyLinkedList<>();
    private final ReentrantReadWriteLock lock;


    public LRUCacheStrategy(int capacity) {
        this.capacity = capacity;
        this.lock = new ReentrantReadWriteLock();

    }

    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        try{
            if (map.containsKey(key)) {
                Node<K, V> node = map.get(key);
                node.value = value;
                dll.moveToFront(node);
            } else {
                if (map.size() >= capacity) {
                    Node<K, V> lru = dll.removeLast();
                    map.remove(lru.key);
                }
                Node<K, V> newNode = new Node<>(key, value);
                map.put(key, newNode);
                dll.addToFront(newNode);
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        lock.readLock().lock();
        try{
            if (!map.containsKey(key)) {
                return null;
            }
            Node<K, V> node = map.get(key);
            dll.moveToFront(node);
            return node.value;
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public void delete(K key) {
        lock.writeLock().lock();
       try{
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

    // Doubly linked list node and helper classes
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class DoublyLinkedList<K, V> {
        private final Node<K, V> head = new Node<>(null, null);
        private final Node<K, V> tail = new Node<>(null, null);

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
