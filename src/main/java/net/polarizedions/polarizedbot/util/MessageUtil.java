package net.polarizedions.polarizedbot.util;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.PrivateChannel;
import discord4j.core.object.entity.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class MessageUtil {
    // Only used for unit testing. don't change these >.>
    @SuppressWarnings("FieldCanBeLocal")
    private static int MAX_MESSAGE_LENGTH = Message.MAX_CONTENT_LENGTH;

    public static void sendAutosplit(@NotNull Message msg, String message) {
        msg.getChannel().subscribe(channel -> sendAutosplit((TextChannel)channel, message));
    }

    public static void sendAutosplit(@NotNull Message msg, String message, String splitPrefix, String splitSuffix) {
        msg.getChannel().subscribe(channel -> sendAutosplit(channel, message, splitPrefix, splitSuffix));
    }

    public static void sendAutosplit(MessageChannel channel, String message) {
        sendAutosplit(channel, message, "", "");
    }

    public static void sendAutosplit(MessageChannel channel, @NotNull String message, String splitPrefix, String splitSuffix) {
        List<String> messages = new LinkedList<>();
        String[] splitMessage = message.split("\n");

        StringBuilder currentMsg = new StringBuilder();
        for (String m : splitMessage) {
            int newLength = currentMsg.length() + m.length() + 1 + splitSuffix.length(); // +1 = \n
            if (newLength < MAX_MESSAGE_LENGTH) {
                currentMsg.append(m).append("\n");
            }
            else {
                messages.add(currentMsg.append(splitSuffix).toString());
                currentMsg = new StringBuilder(splitPrefix).append(m).append("\n");
            }
        }

        if (currentMsg.length() > 0) {
            messages.add(currentMsg.toString());
        }

        for (String msg : messages) {
            channel.createMessage(msg);
        }
    }

    public static void reply(@NotNull Message message, String localizationKey, Object... context) {
        message.getChannel().subscribe(channel -> reply((TextChannel)channel, localizationKey, context));
    }

    public static void reply(@NotNull TextChannel channel, String localizationKey, Object... context) {
            channel.getGuild().subscribe(guild ->
                channel.createMessage(new Localizer(guild).localize(localizationKey, context))
            );
    }

    public static void reply(@NotNull PrivateChannel channel, String localizationKey, Object... context) {
        channel.createMessage(new Localizer().localize(localizationKey, context));
    }
}
