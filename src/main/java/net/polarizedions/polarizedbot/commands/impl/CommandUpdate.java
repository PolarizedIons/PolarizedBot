package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.api_handlers.GithubApi;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.exceptions.ApiException;
import net.polarizedions.polarizedbot.util.Args;
import net.polarizedions.polarizedbot.util.BuildInfo;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandUpdate implements ICommand {
    private static final Logger logger = LogManager.getLogger("CommandUpdate");
    private static final String REPO = BuildInfo.githubRepo.replaceAll("https?://github.com/", "");
    private static boolean inProgress = false;

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Update")
                .setRank(UserRank.GUILD_ADMIN)
                .command("update", update -> update.onExecute(this::update))
                .setHelp("command.update.help")
                .buildCommand();
    }

    private void update(IMessage message, ParsedArguments args) {
        if (inProgress) {
            MessageUtil.reply(message, "command.update.error.in_progress");
            return;
        }
        inProgress = true;

        logger.debug("Checking for updates...");

        File currentJar;
        try {
            currentJar = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if (!currentJar.getName().endsWith(".jar")) {
                MessageUtil.reply(message, "command.update.error.no_jar");
                inProgress = false;
                return;
            }
        }
        catch (URISyntaxException ex) {
            MessageUtil.reply(message, "command.error.no_jar");
            inProgress = false;
            return;
        }

        GithubApi.Release release;
        try {
            release = GithubApi.getLatestRelease(REPO, asset -> asset.get("name").getAsString().contains("with-dependencies"));
        }
        catch (ApiException ex) {
            MessageUtil.reply(message, "command.update.error." + ex.getError(), ex.getErrorContext());
            inProgress = false;
            return;
        }

        if (release.tag.equals(BuildInfo.version)) {
            MessageUtil.reply(message, "command.update.error.up_to_date", release.tag);
            inProgress = false;
            return;
        }

        MessageUtil.reply(message, "command.update.success.downloading", release.tag);

        new Thread(() -> {
            try {
                logger.info("Downloading update {} from {}", release.tag, release.url);
                // Remote file
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(release.url).openStream());

                // Local file
                FileOutputStream fileOutputStream = new FileOutputStream(release.name);

                // Download / transfer
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                
                fileOutputStream.close();

                logger.info("Done downloading update {}", release.name);
                MessageUtil.reply(message, "command.update.success.starting", release.tag);

                // Starting new process
                String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
                ArrayList<String> command = new ArrayList<>();
                command.add(javaBin);
                command.add("-jar");
                command.add(Paths.get(release.name).toString());
                command.add("--update");
                command.add(currentJar.getName());

                // Restore args that was passed in
                if (Args.instance.configDir != null) {
                    command.add("--config");
                    command.add(Args.instance.configDir.toString());
                }
                command.add("--log");
                command.add(Args.instance.logLevel);


                ProcessBuilder builder = new ProcessBuilder(command);
                builder.inheritIO();
                builder.start();

                Bot.instance.shutdown();
                // Don't System.exit because that breaks inheritIO
            }
            catch (Exception ex) {
                logger.error("Exception while downloading update {}: {}", release.tag, ex);
                MessageUtil.reply(message, "command.update.error.error_downloading");
            }
        }, "update thread").start();
    }
}
