package net.polarizedions.polarizedbot.exceptions;

public class NoSuchCommand extends BotExceptions {

    public NoSuchCommand(String command) {
        this.context = new Object[] { command };
    }

    @Override
    public String getError() {
        return "error.no_such_command";
    }
}
