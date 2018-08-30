package net.polarizedions.polarizedbot.util;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.LinkedList;
import java.util.List;

public class MessageUtil {
    public static void sendAutosplit(IChannel channel, String message) {
        sendAutosplit(channel, message, "", "");
    }

    public static void sendAutosplit(IChannel channel, String message, String splitPrefix, String splitSuffix) {
        List<String> messages = new LinkedList<>();
        String[] splitMessage = message.split("\n");

        StringBuilder currentMsg = new StringBuilder();
        for (String m : splitMessage) {
            int newLength = currentMsg.length() + m.length() + 1 + splitSuffix.length(); // +1 = \n
            if (newLength < IMessage.MAX_MESSAGE_LENGTH) {
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


        final int[] i = {0};
        RequestBuffer.request(() -> {
            for (/*NOOP*/; i[0] < messages.size(); /*NOOP*/) {
                if (channel.sendMessage(messages.get(i[0])) != null) {
                    i[0]++;
                }
            }
        });
    }

    public static void reply(IMessage message, String localizationKey, Object... context) {
        String localized = new Localizer(message).localize(localizationKey, context);

        RequestBuffer.request(() -> {
           message.getChannel().sendMessage(localized);
        });
    }
}
