package net.polarizedions.polarizedbot.exceptions;

public class NoSuchCommand extends CommandException {
    @Override
    public String getError() {
        return "error.no_such_command";
    }
}
