package io.github.polarizedions.polarizedbot.exceptions;

import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.util.UserRank;

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
