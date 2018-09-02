package net.polarizedions.polarizedbot.exceptions;

public class UnknownFail extends BotExceptions {
    @Override
    public String getError() {
        return "error.unknown_command_failure";
    }
}
