package com.miniredis.command;

import com.miniredis.store.MemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CommandExecutor}. These go through the full text path
 * (parse + execute) because that is what the server will do.
 */
class CommandExecutorTest {

    private MemoryStore store;
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        store = new MemoryStore();
        executor = new CommandExecutor(store);
        // ^ We keep a reference to the store so tests can look inside it and
        //   confirm the executor really changed it, not just replied nicely.
    }

    // ---------- happy paths ----------

    @Test
    void pingRepliesPong() {
        assertEquals("PONG", executor.execute("PING"));
    }

    @Test
    void setStoresValueAndRepliesOk() {
        assertEquals("OK", executor.execute("SET name alice"));
        assertEquals("alice", store.get("name"));
    }

    @Test
    void getReturnsStoredValue() {
        executor.execute("SET name alice");
        assertEquals("alice", executor.execute("GET name"));
    }

    @Test
    void getRepliesNilForMissingKey() {
        assertEquals("(nil)", executor.execute("GET nothing"));
    }

    @Test
    void delRepliesOneWhenRemovedAndZeroWhenMissing() {
        executor.execute("SET name alice");
        assertEquals("1", executor.execute("DEL name"));
        assertEquals("0", executor.execute("DEL name"));
        // ^ Second DEL of the same key: nothing left to remove.
    }

    @Test
    void existsRepliesOneOrZero() {
        assertEquals("0", executor.execute("EXISTS name"));
        executor.execute("SET name alice");
        assertEquals("1", executor.execute("EXISTS name"));
    }

    @Test
    void dbsizeRepliesNumberOfKeys() {
        assertEquals("0", executor.execute("DBSIZE"));
        executor.execute("SET a 1");
        executor.execute("SET b 2");
        assertEquals("2", executor.execute("DBSIZE"));
        executor.execute("DEL a");
        assertEquals("1", executor.execute("DBSIZE"));
    }

    @Test
    void dbsizeRejectsArguments() {
        assertTrue(executor.execute("DBSIZE extra").startsWith("ERR"));
    }

    @Test
    void commandNamesAreCaseInsensitive() {
        assertEquals("OK", executor.execute("set name alice"));
        assertEquals("alice", executor.execute("Get name"));
    }

    // ---------- error paths: must REPLY with ERR, never throw ----------

    @Test
    void unknownCommandRepliesErr() {
        String reply = executor.execute("FLY away");
        assertTrue(reply.startsWith("ERR"), "reply was: " + reply);
        // ^ assertTrue takes an optional third argument: a message shown only
        //   if the assertion fails. Useful when the boolean alone would be
        //   uninformative.
        assertTrue(reply.contains("FLY"));
    }

    @Test
    void wrongArgumentCountRepliesErr() {
        assertTrue(executor.execute("SET onlykey").startsWith("ERR"));
        assertTrue(executor.execute("GET").startsWith("ERR"));
        assertTrue(executor.execute("DEL a b").startsWith("ERR"));
        assertTrue(executor.execute("PING extra").startsWith("ERR"));
    }

    @Test
    void emptyLineRepliesErr() {
        assertTrue(executor.execute("").startsWith("ERR"));
    }

    @Test
    void errorDoesNotChangeStore() {
        executor.execute("SET a b c");
        // ^ Three args: rejected. Nothing should have been stored.
        assertEquals(0, store.size());
    }

    // ---------- construction ----------

    @Test
    void rejectsNullStore() {
        assertThrows(IllegalArgumentException.class, () -> new CommandExecutor(null));
    }
}
