package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommandShutdown implements ICommand {

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Shutdown")
                 .setRank(UserRank.GUILD_ADMIN)
                 .command("shutdown", shutdown -> shutdown.onExecute(this::shutdown))
                 .command("restart", restart -> restart
                         .stringArg("soft", soft -> soft
                                 .onExecute(this::softRestart)
                         )
                         .stringArg("hard", hard -> hard
                                 .onExecute(this::hardRestart)
                         )
                         .onExecute(this::softRestart)
                         .onFail(this::fail)
                         .setHelp("command.restart.help")
                 )
                 .setHelp("command.shutdown.help")
                 .buildCommand();
    }

    private void shutdown(IMessage message, List<Object> args) {
        Bot.logger.info("Shutting down bot...");
        message.getChannel().sendMessage(Localizer.localize("command.shutdown.success"));
        Bot.instance.shutdown();
    }

    private void fail(IMessage message, List<Object> parsed, List<String> unparsed) {
        message.getChannel().sendMessage(Localizer.localize("command.shutdown.error.restart_subcommand", "hard, soft"));
    }

    private void hardRestart(IMessage message, List<Object> objects) {
        message.getChannel().sendMessage(Localizer.localize("command.shutdown.success.restart_hard"));

        try {
            String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
            File currentJar = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if(!currentJar.getName().endsWith(".jar")) {
                message.getChannel().sendMessage(Localizer.localize("command.restart.error.not_jar"));
                return;
            }

            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            builder.start();
            message.getChannel().setTypingStatus(false);
            Bot.logger.info("Hard restarting bot...");
            Bot.instance.shutdown();
            System.exit(0);
        } catch (URISyntaxException e) {
            message.getChannel().sendMessage(Localizer.localize("command.shutdown.error.jar_uri_error"));
        } catch (IOException e) {
            message.getChannel().sendMessage(Localizer.localize("command.shutdown.error.cannot_spawn_process"));
        }
    }

    private void softRestart(IMessage message, List<Object> objects) {
        message.getChannel().sendMessage(Localizer.localize("command.shutdown.success.restart_soft"));
        message.getChannel().setTypingStatus(false);
        Bot.logger.info("Soft restarting bot...");
        Bot.instance.softRestart();
    }
}
