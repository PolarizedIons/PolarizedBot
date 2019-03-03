package net.polarizedions.polarizedbot.autoresponders;

import discord4j.core.object.entity.Message;

import java.util.Collections;
import java.util.List;

public interface IResponder {
    String getID();

    default List<String> getPrefixWhitelist() {
        return Collections.emptyList();
    }

    void run(Message message);
}
