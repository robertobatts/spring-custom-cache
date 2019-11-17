package com.robertobatts.springcustom.config.cache;

import com.robertobatts.springcustom.cache.Cacheable;
import org.springframework.cache.Cache;

import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

public class ConcurrentMapCollectionHandlingDecoratedCache extends CollectionHandlingDecoratedCache {

    public ConcurrentMapCollectionHandlingDecoratedCache(final Cache cache) {
        super(cache);
    }

    @Override
    protected boolean areAllKeysPresentInCache(Iterable<?> keys) {

        ConcurrentMap nativeCache = (ConcurrentMap) getNativeCache();

        return StreamSupport.stream(keys.spliterator(), false)
                .map(key -> getRealKey(key))
                .allMatch(nativeCache::containsKey);
    }


    /**
     * If I insert a Cacheable object inside the cache, it's collected with its Key object as key
     * Otherwise, if the object isn't Cacheable, it's collected with the standard key declared inside @Cacheable(...)
     * @param key
     * @return
     */
    @Override
    protected Object getRealKey(Object key) {
        return key instanceof Cacheable<?> ?  ((Cacheable) key).getKey() : key;
    }
}