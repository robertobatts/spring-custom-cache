package com.robertobatts.springcustom.cache;

public interface Cacheable<T extends Key> {
    public T getKey();
}