package net.polarizedions.polarizedbot.exceptions;

public class ApiException extends BotExceptions {
    private final String error;

    public ApiException(String error, Object... context) {
        this.error = error;
        this.context = context;
    }

    @Override
    public String getError() {
        return this.error;
    }
}
