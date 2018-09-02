package net.polarizedions.polarizedbot.exceptions;

public class NoSuchCommand extends BotExceptions {
    @Override
    public String getError() {
        return "error.no_such_command";
    }
}
