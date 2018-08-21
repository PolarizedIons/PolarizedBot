package net.polarizedions.polarizedbot.exceptions;

import net.polarizedions.polarizedbot.util.Localizer;

public class NoSuchCommand extends CommandException {
    @Override
    public String getError() {
        return Localizer.localize("error.no_such_command");
    }
}
