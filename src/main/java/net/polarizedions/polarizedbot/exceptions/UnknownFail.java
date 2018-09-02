package net.polarizedions.polarizedbot.exceptions;

public class UnknownFail extends CommandExceptions {
    @Override
    public String getError() {
        return "error.unknown_command_failure";
    }
}
