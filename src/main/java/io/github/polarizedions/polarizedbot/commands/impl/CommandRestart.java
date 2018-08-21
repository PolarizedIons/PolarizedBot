package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommandRestart implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Restart")
                .setRank(UserRank.BOT_OWNER)
                .command("restart", restart -> restart
                    .stringArg("soft", soft -> soft.onExecute(this::softRestart))
                    .stringArg("hard", hard -> hard.onExecute(this::hardRestart))
                    .onExecute(this::softRestart)
                    .onFail(this::fail)
                )
                .buildCommand();
    }

    private void fail(IMessage message, List<Object> parsed, List<String> unparsed) {
        message.getChannel().sendMessage(Localizer.localize("command.restart.error.subcommand", "hard, soft"));
    }

    private void hardRestart(IMessage message, List<Object> objects) {
        message.getChannel().sendMessage(Localizer.localize("command.restart.success.hard"));

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
            Bot.instance.shutdown();
            System.exit(0);
        } catch (URISyntaxException e) {
            message.getChannel().sendMessage(Localizer.localize("command.restart.error.jar_uri_error"));
        } catch (IOException e) {
            message.getChannel().sendMessage(Localizer.localize("command.restart.error.cannot_spawn_process"));
        }
    }

    private void softRestart(IMessage message, List<Object> objects) {
        message.getChannel().sendMessage(Localizer.localize("command.restart.success.soft"));
        Bot.instance.softRestart();
    }
}
