package com.miniredis.store;
// ^ Every Java file starts by saying which "package" (folder) it lives in.
//   This must match the folder path: src/main/java/com/miniredis/store/

import java.util.HashMap;
import java.util.Map;
// ^ Map is the interface (the "contract") that HashMap implements.
public class MemoryStore {
    private final Map<String, String> data;
    public MemoryStore() {
        this.data = new HashMap<>();
    }

    /**
     * Stores a value under a key. If the key already exists, its value is
     * replaced silently.
     *
     * @param key   the key, must not be null or empty
     * @param value the value, must not be null
     * @throws IllegalArgumentException if key or value is invalid
     */
    public void set(String key, String value) {
        validateKey(key);
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        data.put(key, value);
    }

    /**
     * Returns the value stored under a key.
     *
     * @param key the key, must not be null or empty
     * @return the value, or null if the key does not exist
     * @throws IllegalArgumentException if key is invalid
     */
    public String get(String key) {
        validateKey(key);
        return data.get(key);
    }

    /**
     * Removes a key and its value.
     *
     * @param key the key, must not be null or empty
     * @return true if the key existed and was removed, false if it did not exist
     * @throws IllegalArgumentException if key is invalid
     */
    public boolean delete(String key) {
        validateKey(key);
        String removed = data.remove(key);
        return removed != null;
    }

    /**
     * Checks whether a key is present.
     *
     * @param key the key, must not be null or empty
     * @return true if the key exists, false otherwise
     * @throws IllegalArgumentException if key is invalid
     */
    public boolean exists(String key) {
        validateKey(key);
        return data.containsKey(key);
    }

    /**
     * @return the number of keys currently stored
     */
    public int size() {
        return data.size();
    }

    private void validateKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key must not be null or empty");
        }
    }
}
