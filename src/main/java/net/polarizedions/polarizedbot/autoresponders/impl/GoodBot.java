package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.autoresponders.IResponder;
import net.polarizedions.polarizedbot.util.MessageUtil;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

public class GoodBot implements IResponder {
    @Override
    public String getID() {
        return "goodbot";
    }

    @Override
    public void run(IMessage message) {
        String content = message.getContent();
        IUser ourUser = Bot.instance.getClient().getOurUser();
        if (! content.toLowerCase().equals("good bot") && !content.toLowerCase().equals("good " + ourUser.mention())) {
            return;
        }

        MessageHistory messageHistory = message.getChannel().getMessageHistory(2);
        if (messageHistory.get(1).getAuthor().equals(ourUser)) {
            MessageUtil.replyUnlocalized(message.getChannel(), "❤️");
        }
    }
}
