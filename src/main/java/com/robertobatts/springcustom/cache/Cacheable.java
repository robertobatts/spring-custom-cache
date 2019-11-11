package com.robertobatts.springcustom.cache;

import java.io.Serializable;

public interface Cacheable<T extends Key> extends Serializable {
    public T getKey();
}