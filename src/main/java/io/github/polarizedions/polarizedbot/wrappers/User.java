package io.github.polarizedions.polarizedbot.wrappers;

import io.github.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IUser;

public class User {
    private IUser wrappedUser;

    public User (IUser wrappingUser) {
        this.wrappedUser = wrappingUser;
    }

    public IUser getWrappedUser() {
        return wrappedUser;
    }

    public UserRank getRank(Guild guild) {
        return guild.getUserRank(this);
    }

    public String getFullName() {
        return wrappedUser.getName() + "#" + wrappedUser.getDiscriminator();
    }

    public String getPingString() {
        return "<@!" + wrappedUser.getStringID() + ">";
    }

    public String getId() {
        return wrappedUser.getStringID();
    }

    public long getLongId() {
        return wrappedUser.getLongID();
    }

    @Override
    public String toString() {
        return "User[Name: " + getFullName() + ", ID:" + getId() + "]";
    }
}
