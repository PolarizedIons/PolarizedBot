package net.polarizedions.polarizedbot.util;

import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.RequestBuffer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageUtil {
    // Only used for unit testing. don't change these >.>
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean ENABLE_RATELMIT_HANDLING = true;
    @SuppressWarnings("FieldCanBeLocal")
    private static int MAX_MESSAGE_LENGTH = IMessage.MAX_MESSAGE_LENGTH;

    public static void sendAutosplit(IChannel channel, String message) {
        sendAutosplit(channel, message, "", "");
    }

    public static void sendAutosplit(IChannel channel, @NotNull String message, String splitPrefix, String splitSuffix) {
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

        handleRateLimit(channel, messages);
    }

    public static void reply(@NotNull IMessage message, String localizationKey, Object... context) {
        reply(message.getChannel(), localizationKey, context);
    }

    public static void reply(@NotNull IChannel channel, String localizationKey, Object... context) {
        IGuild guild = channel instanceof IPrivateChannel ? null : channel.getGuild();
        String localized = new Localizer(guild).localize(localizationKey, context);

        handleRateLimit(channel, Collections.singletonList(localized));
    }

    public static void replyUnlocalized(IChannel channel, String reply) {
        handleRateLimit(channel, Collections.singletonList(reply));
    }

    public static void handleRateLimit(IChannel channel, List<String> messages) {
        if (ENABLE_RATELMIT_HANDLING) {
            AtomicInteger i = new AtomicInteger();
            RequestBuffer.request(() -> {
                while (i.get() < messages.size() && channel.sendMessage(messages.get(i.get())) != null) {
                    i.getAndIncrement();
                }
            });
        }
        else {
            for (String m : messages) {
                channel.sendMessage(m);
            }
        }
    }
}
