package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.autoresponders.IResponder;
import net.polarizedions.polarizedbot.util.MessageUtil;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

public class GoodBot implements IResponder {
    @Override
    public String getID() {
        return "goodbot";
    }

    private boolean inReplyToUs(@NotNull IMessage message) {
        IUser ourUser = Bot.instance.getClient().getOurUser();
        MessageHistory messageHistory = message.getChannel().getMessageHistory(2);

        if (messageHistory.size() < 2) {
            return false;
        }

        return messageHistory.get(1).getAuthor().equals(ourUser);
    }

    @Override
    public void run(IMessage message) {
        String content = message.getContent().toLowerCase();
        IUser ourUser = Bot.instance.getClient().getOurUser();
        boolean isInReplyToUs = this.inReplyToUs(message);

        if (( isInReplyToUs && content.startsWith("good bot") ) ||
                ( content.startsWith("good " + ourUser.mention()) ) ||
                ( content.startsWith(ourUser.mention() + " good bot") )
        ) {
            MessageUtil.replyUnlocalized(message.getChannel(), "❤️");
        }
    }
}
