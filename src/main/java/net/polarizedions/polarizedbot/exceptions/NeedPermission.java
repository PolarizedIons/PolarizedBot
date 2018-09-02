package net.polarizedions.polarizedbot.exceptions;

import net.polarizedions.polarizedbot.util.UserRank;

public class NeedPermission extends BotExceptions {

    public NeedPermission(UserRank rank) {
        this.context = new Object[] { rank };
    }

    @Override
    public String getError() {
        return "error.need_permission";
    }
}
