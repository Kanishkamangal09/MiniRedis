package com.miniredis.store;
// ^ Same package as MemoryStore. This is deliberate: tests in the same package
//   are allowed to see package-private things, and it mirrors the folder
//   structure under src/test/java instead of src/main/java.

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

/**
 * Unit tests for {@link MemoryStore}.
 *
 * Each test creates a fresh store, performs one scenario, and checks the result.
 * A "unit test" tests one class in isolation, with no network, no files, no
 * other components.
 */
class MemoryStoreTest {
// ^ No "public" needed: JUnit 5 finds test classes regardless. Keeping it
//   package-private is the JUnit 5 convention.

    private MemoryStore store;
    // ^ A field shared by all tests in this class. Each test gets its own
    //   fresh instance, see below.

    @BeforeEach
    void setUp() {
    // ^ @BeforeEach: JUnit runs this method before EVERY @Test method. So each
    //   test starts with a brand-new empty store and cannot be affected by
    //   what another test did. Tests must be independent of each other.
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
        // ^ assertThrows(EXCEPTION TYPE, CODE TO RUN).
        //   The second argument "() -> store.set(null, "x")" is a LAMBDA: a
        //   tiny anonymous function with no parameters "()" whose body is the
        //   expression after "->". We hand the code to JUnit WITHOUT running it
        //   ourselves; JUnit runs it inside a try/catch and passes only if the
        //   named exception was thrown. If we simply wrote store.set(null,"x")
        //   as a normal statement, the exception would escape and the test
        //   would ERROR instead of PASS.
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
