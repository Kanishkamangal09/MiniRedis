package com.miniredis.command;

import java.util.List;

/**
 * A parsed client command: the command name plus its arguments.
 *
 * Example: the text {@code "SET name alice"} becomes
 * {@code new Command("SET", List.of("name", "alice"))}.
 *
 * @param name the command name, always upper case (SET, GET, ...)
 * @param args the arguments after the name, possibly empty, never null
 */
public record Command(String name, List<String> args) {
// ^ A RECORD is a special kind of class (Java 16+) for data that never changes
//   after creation. This one line gives us, for free:
//     - two private final fields: name and args
//     - a constructor: new Command("SET", List.of("a", "b"))
//     - getter methods named after the fields: cmd.name(), cmd.args()
//     - equals(), hashCode() and toString() that compare/print the contents

    /**
     * @return how many arguments the command has (not counting the name)
     */
    public int argCount() {
        return args.size();
    }
}
