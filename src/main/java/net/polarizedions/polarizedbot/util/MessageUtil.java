package net.polarizedions.polarizedbot.util;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.LinkedList;
import java.util.List;

public class MessageUtil {
    public static void sendAutosplit(IChannel channel, String message) {
        List<String> messages = new LinkedList<>();
        String[] splitMessage = message.split("\n");

        StringBuilder currentMsg = new StringBuilder();
        for (String m : splitMessage) {
            if (currentMsg.length() + m.length() +1 < IMessage.MAX_MESSAGE_LENGTH) { // +1 = \n
                currentMsg.append(m).append("\n");
            }
            else {
                messages.add(currentMsg.toString());
                currentMsg = new StringBuilder(m).append("\n");
            }
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
}
