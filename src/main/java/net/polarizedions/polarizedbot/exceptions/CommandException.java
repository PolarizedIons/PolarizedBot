package net.polarizedions.polarizedbot.exceptions;

public abstract class CommandException extends Exception {
    Object[] context = new Object[] {};

    public abstract String getError();
    public Object[] getErrorConext() {
        return this.context;
    }
}
