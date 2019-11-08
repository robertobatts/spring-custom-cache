package com.robertobatts.springcustom.config.cache;

import org.springframework.cache.Cache;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public abstract class CollectionHandlingDecoratedCache implements Cache {

    private final Cache cache;

    protected CollectionHandlingDecoratedCache(Cache cache) {

        Assert.notNull(cache, "Cache must not be null");

        this.cache = cache;
    }

    protected Cache getCache() {
        return this.cache;
    }

    @Override
    public String getName() {
        return getCache().getName();
    }

    @Override
    public Object getNativeCache() {
        return getCache().getNativeCache();
    }

    protected abstract boolean areAllKeysPresentInCache(Iterable<?> keys);

    protected abstract Object getRealKey(Object key);

    protected int sizeOf(Iterable<?> iterable) {
        return Long.valueOf(StreamSupport.stream(iterable.spliterator(), false).count()).intValue();
    }

    protected <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public ValueWrapper get(Object key) {

        if (key instanceof Iterable) {

            Iterable<?> keys = (Iterable<?>) key;

            if (!areAllKeysPresentInCache(keys)) {
                return null;
            }

            Collection<Object> values = new ArrayList<>();

            for (Object singleKey : keys) {
                Object realKey = getRealKey(singleKey);
                values.add(getCache().get(realKey).get());
            }

            return () -> values;
        }

        return getCache().get(getRealKey(key));
    }

    @Override
    public <T> T get(Object key, Class<T> type) {

        if (key instanceof Iterable) {

            Assert.isAssignable(Iterable.class, type,
                    String.format("Expected return type [%1$s] must be Iterable when querying multiple keys [%2$s]",
                            type.getName(), key));

            return (T) Optional.ofNullable(get(key)).map(Cache.ValueWrapper::get).orElse(null);
        }

        return getCache().get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) get(key, Object.class);
    }

    @Override
    public void put(@NonNull Object key, Object value) {

        if (key instanceof Iterable) {

            Assert.isInstanceOf(Iterable.class, value,
                    String.format("Value [%1$s] must be an instance of Iterable when caching multiple keys [%2$s]",
                            ObjectUtils.nullSafeClassName(value), key));

            pairsFromKeysAndValues(toList((Iterable<?>) key), toList((Iterable<?>) value))
                    .forEach(pair -> getCache().put(pair.getFirst(), pair.getSecond()));
        }
        else {
            getCache().put(getRealKey(key), value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {

        if (key instanceof Iterable) {

            Assert.isInstanceOf(Iterable.class, value,
                    String.format("Value [%1$s] must be an instance of Iterable when caching multiple keys [%2$s]",
                            ObjectUtils.nullSafeClassName(value), key));

            return () -> pairsFromKeysAndValues(toList((Iterable<?>) key), toList((Iterable<?>) value)).stream()
                    .map(pair -> getCache().putIfAbsent(pair.getFirst(), pair.getSecond()))
                    .collect(Collectors.toList());
        }

        return getCache().putIfAbsent(getRealKey(key), value);
    }

    @Override
    public void evict(Object key) {

        if (key instanceof Iterable) {
            StreamSupport.stream(((Iterable) key).spliterator(), false).forEach(getCache()::evict);
        }
        else {
            getCache().evict(key);
        }
    }

    @Override
    public void clear() {
        getCache().clear();
    }

    private <K, V> List<Pair<Object, V>> pairsFromKeysAndValues(List<K> keys, List<V> values) {

        final int keysSize = keys.size();

        Assert.isTrue(keysSize == values.size(),
                String.format("The number of values [%1$d] must match the number of keys [%2$d]",
                        values.size(), keysSize));

        return IntStream.range(0, keysSize)
                .mapToObj(index -> {
                    K key = keys.get(index);
                    return Pair.of(getRealKey(key), values.get(index));
                })
                .collect(Collectors.toList());
    }

}