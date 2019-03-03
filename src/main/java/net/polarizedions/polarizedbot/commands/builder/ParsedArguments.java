package net.polarizedions.polarizedbot.commands.builder;

import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import org.jetbrains.annotations.Contract;

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
    public TextChannel getAsChannel(int i) {
        return (TextChannel) this.get(i);
    }

    @SuppressWarnings("deprecation")
    public User getAsUser(int i) {
        return (User) this.get(i);
    }
}
