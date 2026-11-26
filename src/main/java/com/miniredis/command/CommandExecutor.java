package com.miniredis.command;
import com.miniredis.store.MemoryStore;
public class CommandExecutor {
    private final MemoryStore store;
    // ^ The executor does not CREATE the store, it is GIVEN one. This is called
    //   dependency injection (the simple, manual kind). Benefits:
    //     - tests can hand in a fresh store and inspect it afterwards
    //     - the server will create ONE store and share it with every client
    //   If the executor did "new MemoryStore()" itself, neither would be possible.

    /**
     * @param store the storage engine this executor operates on, must not be null
     */
    public CommandExecutor(MemoryStore store) {
        if (store == null) {
            throw new IllegalArgumentException("store must not be null");
        }
        this.store = store;
    }

    /**
     * Parses and executes one raw line of client input.
     *
     * Never throws for bad client input: errors become "ERR ..." replies so the
     * connection stays alive. This is the single entry point the server will call.
     *
     * @param line raw text from the client
     * @return the reply text (without a trailing newline)
     */
    public String execute(String line) {
        try {
            Command command = CommandParser.parse(line);
            return execute(command);
        } catch (CommandException e) {
        // ^ try/catch: if ANY line inside the try block throws a
        //   CommandException, execution jumps here with the exception in "e".
        //   We catch ONLY CommandException on purpose. A NullPointerException
        //   or similar would mean a bug in our code, and we want that to be
        //   loud, not quietly converted to a client error.
            return "ERR " + e.getMessage();
        }
    }

    /**
     * Executes an already-parsed command.
     *
     * @param command the command
     * @return the reply text
     * @throws CommandException if the command is unknown or has wrong arguments
     */
    public String execute(Command command) {
        return switch (command.name()) {
        // ^ A SWITCH EXPRESSION (Java 14+). It compares command.name() against
        //   each "case" label and the whole switch EVALUATES to a value, which
        //   we return. Unlike the old switch statement there is no fall-through
        //   and no "break" needed: each arrow -> gives exactly one result.
            case "PING" -> ping(command);
            case "SET" -> set(command);
            case "GET" -> get(command);
            case "DEL" -> del(command);
            case "EXISTS" -> exists(command);
            case "DBSIZE" -> dbsize(command);
            default -> throw new CommandException(
                    "unknown command '" + command.name() + "'");
        };
    }

    // ---------- one private method per command ----------
    // Each one does three things: check argument count, call the store,
    // format the reply. Keeping them separate keeps execute(Command) readable
    // and means adding a command later is "add a case, add a method".

    private String ping(Command command) {
        requireArgs(command, 0);
        return "PONG";
    }

    private String set(Command command) {
        requireArgs(command, 2);
        store.set(command.args().get(0), command.args().get(1));
        return "OK";
    }

    private String get(Command command) {
        requireArgs(command, 1);
        String value = store.get(command.args().get(0));
        return value == null ? "(nil)" : value;
    }

    private String del(Command command) {
        requireArgs(command, 1);
        boolean removed = store.delete(command.args().get(0));
        return removed ? "1" : "0";
    }

    private String exists(Command command) {
        requireArgs(command, 1);
        return store.exists(command.args().get(0)) ? "1" : "0";
    }

    private String dbsize(Command command) {
        requireArgs(command, 0);
        return String.valueOf(store.size());
        // ^ store.size() is an int; the reply must be a String.
        //   String.valueOf(42) gives "42". ("" + store.size() also works but
        //   valueOf states the intent: convert this number to text.)
    }

    /**
     * Fails with a CommandException unless the command has exactly
     * {@code expected} arguments. Shared by every command so the error
     * message is consistent.
     */
    private static void requireArgs(Command command, int expected) {
        if (command.argCount() != expected) {
            throw new CommandException(
                    "wrong number of arguments for '" + command.name()
                    + "' (expected " + expected + ", got " + command.argCount() + ")");
        }
    }
}
