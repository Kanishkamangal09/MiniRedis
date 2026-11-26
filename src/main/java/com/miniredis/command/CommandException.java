package com.miniredis.command;
public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
        // ^ "super(...)" calls the constructor of the parent class
        //   (RuntimeException) and hands it the message, so getMessage() works like on any other exception.
    }
}
