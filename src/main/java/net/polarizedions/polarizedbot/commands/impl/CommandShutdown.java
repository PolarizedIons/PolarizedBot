package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Message;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommandShutdown implements ICommand {
    private final Bot bot;

    public CommandShutdown(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Shutdown")
                .setRank(UserRank.GLOBAL_ADMIN)
                .command("shutdown", "exit", shutdown -> shutdown.onExecute(this::shutdown))
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

    private void shutdown(Message message, ParsedArguments args) {
        Bot.logger.info("Shutting down bot...");
        MessageUtil.reply(message, "command.shutdown.success");
        this.bot.shutdown();
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException ex) {
            // NOOP
        }
        System.exit(0);
    }

    private void fail(Message message, ParsedArguments parsed, List<String> unparsed) {
        MessageUtil.reply(message, "command.shutdown.error.restart_subcommand", "hard, soft");
    }

    private void hardRestart(Message message, ParsedArguments args) {
        MessageUtil.reply(message, "command.shutdown.success.restart_hard");

        try {
            String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
            File currentJar = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if (!currentJar.getName().endsWith(".jar")) {
                MessageUtil.reply(message, "command.shutdown.error.not_jar");
                return;
            }

            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            builder.start();
            Bot.logger.info("Hard restarting bot...");
            this.bot.shutdown();
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                // NOOP
            }
            System.exit(0);
        }
        catch (URISyntaxException e) {
            MessageUtil.reply(message, "command.shutdown.error.jar_uri_error");
        }
        catch (IOException e) {
            MessageUtil.reply(message, "command.shutdown.error.cannot_spawn_process");
        }
    }

    private void softRestart(Message message, ParsedArguments args) {
        MessageUtil.reply(message, "command.shutdown.success.restart_soft");
        Bot.logger.info("Soft restarting bot...");
        this.bot.softRestart();
    }
}
