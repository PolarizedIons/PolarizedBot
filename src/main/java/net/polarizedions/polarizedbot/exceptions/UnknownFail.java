package net.polarizedions.polarizedbot.exceptions;

import net.polarizedions.polarizedbot.util.Localizer;

public class UnknownFail extends CommandException {
    @Override
    public String getError() {
        return Localizer.localize("error.unknown_command_failure");
    }
}
