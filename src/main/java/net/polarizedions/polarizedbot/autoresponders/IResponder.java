package net.polarizedions.polarizedbot.autoresponders;

import sx.blah.discord.handle.obj.IMessage;

import java.util.Collections;
import java.util.List;

public interface IResponder {
    String getID();

    default List<String> getPrefixWhitelist() {
        return Collections.emptyList();
    }

    void run(IMessage message);
}
