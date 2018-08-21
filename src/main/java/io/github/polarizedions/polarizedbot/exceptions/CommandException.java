package io.github.polarizedions.polarizedbot.exceptions;

public abstract class CommandException extends Exception {
    public abstract String getError();
}
