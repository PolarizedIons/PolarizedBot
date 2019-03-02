package net.polarizedions.polarizedbot.exceptions;

public abstract class BotExceptions extends Exception {
    Object[] context = new Object[] {};

    public abstract String getError();

    public Object[] getErrorContext() {
        return this.context;
    }
}
