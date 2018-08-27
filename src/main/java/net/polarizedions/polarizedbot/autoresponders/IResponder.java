package net.polarizedions.polarizedbot.autoresponders;

import sx.blah.discord.handle.obj.IMessage;

public interface IResponder {
    void run(IMessage message);

    String getID();
}
