package net.polarizedions.polarizedbot.commands.builder;

import org.jetbrains.annotations.Contract;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collection;
import java.util.LinkedList;

public class ParsedArguments extends LinkedList<Object> {
    public ParsedArguments() {
        super();
    }

    public ParsedArguments(Collection<?> list) {
        super(list);
    }


    @Override
    @Deprecated
    @Contract(pure = true)
    public Object get(int i) {
        return super.get(i);
    }

    @SuppressWarnings("deprecation")
    public Integer getAsInt(int i) {
        return (Integer) this.get(i);
    }

    @SuppressWarnings("deprecation")
    public String getAsString(int i) {
        return (String) this.get(i);
    }

    @SuppressWarnings("deprecation")
    public IChannel getAsChannel(int i) {
        return (IChannel) this.get(i);
    }

    @SuppressWarnings("deprecation")
    public IUser getAsUser(int i) {
        return (IUser) this.get(i);
    }
}
