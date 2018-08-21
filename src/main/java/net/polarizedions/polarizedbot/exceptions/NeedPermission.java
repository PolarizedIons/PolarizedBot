package net.polarizedions.polarizedbot.exceptions;

import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.UserRank;

public class NeedPermission extends CommandException {
    private final UserRank rank;

    public NeedPermission(UserRank rank) {
        this.rank = rank;
    }

    @Override
    public String getError() {
        return Localizer.localize("error.need_permission", this.rank);
    }


}
