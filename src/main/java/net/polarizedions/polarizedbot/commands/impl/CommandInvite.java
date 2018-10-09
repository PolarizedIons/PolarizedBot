//package net.polarizedions.polarizedbot.commands.impl;
//
//import net.polarizedions.polarizedbot.Bot;
//import net.polarizedions.polarizedbot.commands.ICommand;
//import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
//import net.polarizedions.polarizedbot.commands.builder.CommandTree;
//import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
//import org.jetbrains.annotations.NotNull;
//import sx.blah.discord.handle.obj.IMessage;
//
//public class CommandInvite implements ICommand {
//    private static final String INVITE_URL = "https://discordapp.com/oauth2/authorize?&client_id=%s&scope=bot";
//
//    @Override
//    public CommandTree getCommand() {
//        return CommandBuilder.create("Invite")
//                .command("invite", invite -> invite
//                        .onExecute(this::invite)
//                        .setHelp("command.invite.help")
//                )
//                .setHelp("command.invite.help")
//                .buildCommand();
//    }
//
//    private void invite(@NotNull IMessage message, ParsedArguments args) {
//        message.getChannel().sendMessage(String.format(INVITE_URL, Bot.instance.getClient().getApplicationClientID()));
//    }
//}
