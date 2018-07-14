package io.github.polarizedions.polarizedbot.wrappers;

import io.github.polarizedions.polarizedbot.util.UserRank;

import java.util.Arrays;

public class CommandMessage extends Message {
    private Message message;
    private String command;
    private String[] args;

    public CommandMessage(Message wrappingMessage) {
        super(wrappingMessage.getWrappedMessage());
        this.message = wrappingMessage;
        String[] tmp = getContent().substring(getGuild().getConfig().commandPrefix.length()).split(" ");
        this.command = tmp[0];
        this.args = Arrays.copyOfRange(tmp, 1, tmp.length);
    }

    public Message getAsMessage() {
        return message;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    public UserRank getUserRank() {
        return getAuthor().getRank(getGuild());
    }
}
