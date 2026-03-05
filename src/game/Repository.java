package game;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

// Generic repository backed by a sorted TreeMap for storing game entities by string key.
public class Repository<T> {

    private final TreeMap<String, T> store;

    public Repository() {
        this.store = new TreeMap<>();
    }

    public void put(String key, T entity) {
        store.put(key, entity);
    }

    public T get(String key) {
        return store.get(key);
    }

    public void remove(String key) {
        store.remove(key);
    }

    public boolean contains(String key) {
        return store.containsKey(key);
    }

    public List<T> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    // Filters entities using a predicate lambda.
    public List<T> findWhere(Predicate<T> predicate) {
        return store.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public void forEach(Consumer<T> action) {
        store.values().forEach(action);
    }

    public int size() {
        return store.size();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(store.keySet());
    }
}
