package com.miniredis.store;
// ^ Every Java file starts by saying which "package" (folder) it lives in.
//   This must match the folder path: src/main/java/com/miniredis/store/

import java.util.HashMap;
// ^ HashMap lives in the java.util library. "import" lets us use the short
//   name HashMap instead of writing java.util.HashMap every time.
import java.util.Map;
// ^ Map is the interface (the "contract") that HashMap implements.

/**
 * In-memory key-value storage engine for MiniRedis.
 *
 * Stores string keys mapped to string values. This is the heart of the
 * database: every command (SET, GET, DEL, EXISTS) ends up calling a method
 * on this class.
 *
 * NOT thread-safe yet. Only one thread may use an instance at a time.
 * Concurrency support is added in Phase 5.
 */
// ^ This /** ... */ block is a JavaDoc comment. Tools can turn it into
//   documentation, and VS Code shows it when you hover over the class name.
public class MemoryStore {
// ^ "public" = any other class in the project may use MemoryStore.
//   "class" = a blueprint. We create objects (instances) from it with "new".

    private final Map<String, String> data;
    // ^ This is a FIELD: a variable that belongs to each MemoryStore object.
    //
    //   private  -> only code inside this class can touch it. Nobody outside
    //               can do store.data.put(...) and bypass our validation.
    //   final    -> the variable can be assigned exactly once (in the
    //               constructor). After that, "data" always points to the SAME
    //               map object. The CONTENTS of the map can still change;
    //               final only locks the reference, not the contents.
    //   Map<String, String> -> the TYPE. "Map" is the interface; the <String,
    //               String> part (generics) says keys are Strings and values
    //               are Strings. The compiler now refuses data.put(5, "x").
    //   data     -> the name we chose for the field.

    /**
     * Creates an empty store.
     */
    public MemoryStore() {
    // ^ CONSTRUCTOR: a special method with the same name as the class and no
    //   return type. It runs once when someone writes "new MemoryStore()".
    //   Its job is to put the object into a valid starting state.
        this.data = new HashMap<>();
        // ^ "this.data" = the field of THIS object.
        //   "new HashMap<>()" creates an empty HashMap. The empty <> is the
        //   "diamond": Java copies <String, String> from the field's type so we
        //   don't repeat it.
        //   Notice we declared the field as Map but created a HashMap. That is
        //   "program to the interface": in Phase 5 we change only this one
        //   line to new ConcurrentHashMap<>() and nothing else breaks.
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
    // ^ "void" = this method returns nothing. It only has a side effect
    //   (changing the map). Parameters "key" and "value" are local names for
    //   whatever the caller passes in.
        validateKey(key);
        // ^ Call our private helper (defined at the bottom). If the key is bad,
        //   the helper throws an exception and this method stops right here.
        //   The next line never runs.
        if (value == null) {
        // ^ "==" compares references. For null checks this is the right tool.
        //   (For comparing string CONTENTS you use .equals(), never ==.)
            throw new IllegalArgumentException("value must not be null");
            // ^ "throw" aborts the method and hands this exception object to
            //   the caller. IllegalArgumentException is Java's standard way
            //   to say "you called me with a bad argument". The string is the
            //   message a developer sees in the stack trace.
        }
        data.put(key, value);
        // ^ HashMap.put stores the pair. If the key already existed, the old
        //   value is replaced. put() also RETURNS the old value, but we don't
        //   need it, so we ignore the return value.
    }

    /**
     * Returns the value stored under a key.
     *
     * @param key the key, must not be null or empty
     * @return the value, or null if the key does not exist
     * @throws IllegalArgumentException if key is invalid
     */
    public String get(String key) {
    // ^ Return type is String: the caller receives a String (or null).
        validateKey(key);
        return data.get(key);
        // ^ HashMap.get looks up the key and returns its value, or null if the
        //   key is absent. "return" sends that value back to the caller and
        //   ends the method.
        //   Because set() refuses null values, a null result here ALWAYS means
        //   "key not found". That is why we validated in set().
    }

    /**
     * Removes a key and its value.
     *
     * @param key the key, must not be null or empty
     * @return true if the key existed and was removed, false if it did not exist
     * @throws IllegalArgumentException if key is invalid
     */
    public boolean delete(String key) {
    // ^ Returns boolean (true/false) so the caller knows whether anything
    //   actually happened. Redis DEL reports how many keys were removed; this
    //   is the single-key version of that idea.
        validateKey(key);
        String removed = data.remove(key);
        // ^ HashMap.remove deletes the entry and RETURNS the value that was
        //   there, or null if there was no such key. We save it in a local
        //   variable named "removed".
        return removed != null;
        // ^ "removed != null" is an expression that evaluates to true or false.
        //   We return that directly. Same as:
        //       if (removed != null) { return true; } else { return false; }
        //   but shorter and idiomatic.
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
        // ^ containsKey does exactly what it says. We could also have written
        //   data.get(key) != null, but containsKey states the INTENT clearly:
        //   we only care about presence, not the value.
    }

    /**
     * @return the number of keys currently stored
     */
    public int size() {
        return data.size();
        // ^ No validation needed: there are no arguments to validate.
    }

    /**
     * Rejects null and empty keys. Called at the top of every public method
     * that takes a key, so validation lives in exactly one place.
     */
    private void validateKey(String key) {
    // ^ private: only this class calls it. It is an internal detail. If we made
    //   it public, outside code would depend on it and we could never change
    //   or remove it freely.
        if (key == null || key.isEmpty()) {
        // ^ "||" means OR. Java evaluates left to right and STOPS as soon as
        //   the result is known ("short-circuit"). That matters here: if key is
        //   null, the left side is true, so Java never runs key.isEmpty().
        //   If we wrote the checks in the other order, key.isEmpty() on a null
        //   key would crash with NullPointerException.
        //   isEmpty() is true for "" (zero characters).
            throw new IllegalArgumentException("key must not be null or empty");
        }
        // Design choice: a key of only spaces ("   ") is ALLOWED. Redis allows
        // it too. We only reject what is genuinely meaningless.
    }
}
