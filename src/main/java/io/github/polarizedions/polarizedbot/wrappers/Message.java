package io.github.polarizedions.polarizedbot.wrappers;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.util.Localizer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class Message {
    private IMessage wrappedMessage;
    private User author;
    private Guild guild;

    public Message(IMessage wrappingMessage) {
        this.wrappedMessage = wrappingMessage;
        this.author = new User(wrappedMessage.getAuthor());
        this.guild = new Guild(wrappedMessage.getGuild());
    }

    public void replyLocalized(String messageKey, String... values) {
        Localizer localizer = Bot.instance.getLocalizer();
        getChannel().sendMessage(localizer.localize(getGuild(), messageKey, values));
    }

    public String getContent() {
        return wrappedMessage.getContent();
    }

    public User getAuthor() {
        return author;
    }

    public Guild getGuild() {
        return guild;
    }

    public IChannel getChannel() {
        return wrappedMessage.getChannel();
    }

    public boolean isCommand() {
        return getContent().startsWith(guild.getConfig().commandPrefix);
    }

    public IMessage getWrappedMessage() {
        return wrappedMessage;
    }

    public CommandMessage getAsCommand() {
        return new CommandMessage(this);
    }

    @Override
    public String toString() {
        return "Message[Guild: " + getGuild().getId() + ", User: " + getAuthor().getId() + ", Rank: " + getAuthor().getRank(getGuild()) + "] " + getAuthor().getFullName() + ": " + getContent();
    }
}
