package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Message;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import org.jetbrains.annotations.NotNull;

public class CommandInvite implements ICommand {
    private static final String INVITE_URL = "https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot";
    private final Bot bot;

    public CommandInvite(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Invite")
                .command("invite", invite -> invite
                        .onExecute(this::invite)
                        .setHelp("command.invite.help")
                )
                .setHelp("command.invite.help")
                .buildCommand();
    }

    private void invite(@NotNull Message message, ParsedArguments args) {
        message.getChannel().subscribe(channel ->
            this.bot.getClient().getApplicationInfo().subscribe(appInfo ->
                    channel.createMessage(String.format(INVITE_URL, appInfo.getId().asString()))
            )
        );
    }
}
