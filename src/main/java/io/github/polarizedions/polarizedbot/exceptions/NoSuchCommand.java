package io.github.polarizedions.polarizedbot.exceptions;

import io.github.polarizedions.polarizedbot.util.Localizer;

public class NoSuchCommand extends CommandException {
    @Override
    public String getError() {
        return Localizer.localize("error.no_such_command");
    }
}
