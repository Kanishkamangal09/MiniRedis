package com.miniredis.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CommandParser}: text in, Command out. No store involved.
 */
class CommandParserTest {

    @Test
    void splitsNameAndArguments() {
        Command cmd = CommandParser.parse("SET name alice");
        assertEquals("SET", cmd.name());
        assertEquals(List.of("name", "alice"), cmd.args());
        // ^ List.of builds the expected list. Records and Lists compare by
        //   CONTENT with equals(), so this checks each element.
    }

    @Test
    void upperCasesCommandNameOnly() {
        Command cmd = CommandParser.parse("set Name Alice");
        assertEquals("SET", cmd.name());
        assertEquals(List.of("Name", "Alice"), cmd.args());
        // ^ Arguments must keep their case: "Alice" is user data.
    }

    @Test
    void ignoresExtraWhitespace() {
        Command cmd = CommandParser.parse("   get    name  \t ");
        assertEquals("GET", cmd.name());
        assertEquals(List.of("name"), cmd.args());
    }

    @Test
    void commandWithNoArgumentsHasEmptyArgs() {
        Command cmd = CommandParser.parse("PING");
        assertEquals("PING", cmd.name());
        assertTrue(cmd.args().isEmpty());
        assertEquals(0, cmd.argCount());
    }

    @Test
    void rejectsNullLine() {
        assertThrows(CommandException.class, () -> CommandParser.parse(null));
    }

    @Test
    void rejectsEmptyLine() {
        assertThrows(CommandException.class, () -> CommandParser.parse(""));
    }

    @Test
    void rejectsWhitespaceOnlyLine() {
        assertThrows(CommandException.class, () -> CommandParser.parse("   "));
    }
}
