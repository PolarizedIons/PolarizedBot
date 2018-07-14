package io.github.polarizedions.polarizedbot.announcer;

import sx.blah.discord.handle.obj.IChannel;

import java.util.List;

public interface IAnnouncer {
    String getName();
    long updateFrequency();

    boolean check();
    void execute(List<IChannel> channels);
}
