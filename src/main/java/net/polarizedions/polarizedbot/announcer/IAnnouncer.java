package net.polarizedions.polarizedbot.announcer;

import discord4j.core.object.entity.TextChannel;

import java.util.List;

public interface IAnnouncer {
    String getID();

    long updateFrequency();

    boolean check();

    void execute(List<TextChannel> channels);
}
