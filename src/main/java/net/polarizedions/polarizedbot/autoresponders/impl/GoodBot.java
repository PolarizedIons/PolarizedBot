package net.polarizedions.polarizedbot.autoresponders.impl;

import com.vdurmont.emoji.EmojiManager;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.autoresponders.IResponder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoodBot implements IResponder {
    @Override
    public String getID() {
        return "goodbot";
    }

    private boolean inReplyToUs(@NotNull Message message) {
        User ourUser = Bot.instance.getClient().getSelf().block();
        List<Message> channelMessages = message.getChannel().block().getMessagesBefore(message.getId()).collectList().block();
        boolean found = false;
        int i = 0;

        for (Message msg : channelMessages) {
            if (i > 20) {
                return false;
            }

            if (found) {
                return msg.getAuthor().get().getId().asLong() == ourUser.getId().asLong();
            }

            if (msg.equals(message)) {
                found = true;
            }

            i++;
        }

        return false;
    }

    @Override
    public void run(Message message) {
        String content = message.getContent().orElse("").toLowerCase();
        User ourUser = Bot.instance.getClient().getSelf().block();
        boolean isInReplyToUs = this.inReplyToUs(message);

        if (( isInReplyToUs && content.startsWith("good bot") ) ||
                ( content.startsWith("good " + ourUser.getMention() )) ||
                ( content.startsWith(ourUser.getMention() + " good bot") )
        ) {
            message.getChannel().subscribe(channel -> channel.createMessage(EmojiManager.getForAlias("heart").getUnicode()));
        }
    }
}
