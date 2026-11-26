package com.miniredis.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// ^ These are "annotations" from JUnit 5. An annotation is a label starting
//   with @ that you stick on a method or class. JUnit reads the labels to
//   decide what to run.

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
// ^ "import static" lets us call assertEquals(...) directly instead of
//   Assertions.assertEquals(...). An assertion is a check: if it is false,
//   the test FAILS and JUnit prints what was expected versus what it got.


class MemoryStoreTest {
    private MemoryStore store;
    @BeforeEach
    void setUp() {
        store = new MemoryStore();
    }

    // ---------- set + get ----------

    @Test
    void getReturnsValueAfterSet() {
    // ^ @Test marks this as a test. The method name is a sentence describing
    //   the behaviour. When it fails, the name alone tells you what broke.
        store.set("name", "alice");
        assertEquals("alice", store.get("name"));
        // ^ assertEquals(EXPECTED, ACTUAL). Order matters only for the error
        //   message JUnit prints ("expected: <alice> but was: <bob>").
    }

    @Test
    void getReturnsNullForMissingKey() {
        assertNull(store.get("missing"));
        // ^ We never set "missing", so get must return null, not crash.
    }

    @Test
    void setOverwritesExistingValue() {
        store.set("name", "alice");
        store.set("name", "bob");
        assertEquals("bob", store.get("name"));
        assertEquals(1, store.size());
        // ^ Overwriting must not create a second entry.
    }

    // ---------- delete ----------

    @Test
    void deleteRemovesKeyAndReturnsTrue() {
        store.set("name", "alice");
        assertTrue(store.delete("name"));
        // ^ delete reports true because something was removed...
        assertNull(store.get("name"));
        // ^ ...and afterwards the key is really gone.
    }

    @Test
    void deleteReturnsFalseForMissingKey() {
        assertFalse(store.delete("missing"));
    }

    // ---------- exists ----------

    @Test
    void existsReturnsTrueWhenKeyPresent() {
        store.set("name", "alice");
        assertTrue(store.exists("name"));
    }

    @Test
    void existsReturnsFalseWhenKeyAbsent() {
        assertFalse(store.exists("name"));
    }

    // ---------- size ----------

    @Test
    void sizeCountsDistinctKeys() {
        assertEquals(0, store.size());
        store.set("a", "1");
        store.set("b", "2");
        assertEquals(2, store.size());
        store.delete("a");
        assertEquals(1, store.size());
    }

    // ---------- validation ----------

    @Test
    void setRejectsNullKey() {
        assertThrows(IllegalArgumentException.class, () -> store.set(null, "x"));
    }

    @Test
    void setRejectsEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> store.set("", "x"));
    }

    @Test
    void setRejectsNullValue() {
        assertThrows(IllegalArgumentException.class, () -> store.set("k", null));
    }

    @Test
    void getRejectsNullKey() {
        assertThrows(IllegalArgumentException.class, () -> store.get(null));
    }

    @Test
    void deleteRejectsEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> store.delete(""));
    }

    @Test
    void existsRejectsNullKey() {
        assertThrows(IllegalArgumentException.class, () -> store.exists(null));
    }

    @Test
    void keyMadeOfSpacesIsAllowed() {
        store.set("   ", "blank");
        assertEquals("blank", store.get("   "));
        // ^ Documents a design decision in code: only null/empty are rejected.
    }
}
