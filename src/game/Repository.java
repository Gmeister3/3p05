package game;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * A generic repository for storing and retrieving game entities by a string key.
 * <p>
 * {@code Repository<T>} wraps a {@link TreeMap} (sorted by key) and provides
 * convenience methods that use lambdas, streams, and wildcards. This class
 * demonstrates Java Generics as required by the assignment.
 * </p>
 *
 * @param <T> the type of entity stored in this repository
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Repository<T> {

    /** Sorted backing store (key → entity). */
    private final TreeMap<String, T> store;

    /**
     * Constructs an empty Repository.
     */
    public Repository() {
        this.store = new TreeMap<>();
    }

    /**
     * Stores an entity under the given key, replacing any existing value.
     *
     * @param key    the unique key
     * @param entity the entity to store
     */
    public void put(String key, T entity) {
        store.put(key, entity);
    }

    /**
     * Retrieves an entity by key.
     *
     * @param key the key to look up
     * @return the entity, or {@code null} if not found
     */
    public T get(String key) {
        return store.get(key);
    }

    /**
     * Removes an entity by key.
     *
     * @param key the key of the entity to remove
     */
    public void remove(String key) {
        store.remove(key);
    }

    /**
     * Returns whether an entity with the given key exists.
     *
     * @param key the key to check
     * @return {@code true} if the key exists
     */
    public boolean contains(String key) {
        return store.containsKey(key);
    }

    /**
     * Returns all stored entities as an unmodifiable list.
     *
     * @return list of all entities
     */
    public List<T> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    /**
     * Returns entities that satisfy the given predicate.
     * <p>Demonstrates lambda / method reference usage with streams.</p>
     *
     * @param predicate the filter condition
     * @return filtered list of entities
     */
    public List<T> findWhere(Predicate<T> predicate) {
        return store.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Applies the given action to every stored entity.
     *
     * @param action a {@link Consumer} lambda to apply to each entity
     */
    public void forEach(Consumer<T> action) {
        store.values().forEach(action);
    }

    /**
     * Returns the number of entities in this repository.
     *
     * @return entity count
     */
    public int size() {
        return store.size();
    }

    /**
     * Returns an unmodifiable view of the key set.
     *
     * @return sorted key set
     */
    public Set<String> keySet() {
        return Collections.unmodifiableSet(store.keySet());
    }
}
