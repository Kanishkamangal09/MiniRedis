package com.miniredis.command;

import java.util.List;

/**
 * Turns one raw line of client text into a {@link Command}.
 *
 * This class knows NOTHING about storage. It only deals with text.
 * Keeping parsing separate from execution means we can later replace this
 * text parser with a RESP2 parser (Phase 8) without touching the executor.
 */
public final class CommandParser {
    private CommandParser() {
    }

    /**
     * Parses a line such as {@code "set name alice"} into a Command.
     *
     * Rules:
     * <ul>
     *   <li>Words are separated by one or more spaces/tabs.</li>
     *   <li>Leading and trailing whitespace is ignored.</li>
     *   <li>The command name is upper-cased: {@code set}, {@code Set} and
     *       {@code SET} are the same command.</li>
     *   <li>Arguments keep their original case: values are user data.</li>
     * </ul>
     *
     * @param line the raw text, must not be null
     * @return the parsed command
     * @throws CommandException if the line is blank
     */
    public static Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new CommandException("empty command");
        }

        String[] tokens = line.trim().split("\\s+");

        String name = tokens[0].toUpperCase();

        List<String> args = List.of(tokens).subList(1, tokens.length);

        return new Command(name, args);
    }
}
