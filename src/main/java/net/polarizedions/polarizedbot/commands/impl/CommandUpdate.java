package net.polarizedions.polarizedbot.commands.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.UserRank;
import net.polarizedions.polarizedbot.util.WebHelper;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommandUpdate implements ICommand {
    private static final String REPO = "PolarizedIons/polarizedbot";
    private static final String LATEST_RELEASE_URL = String.format("https://api.github.com/repos/%s/releases/latest", REPO);
    private static boolean inProgess = false;

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Update")
                .setRank(UserRank.GUILD_ADMIN)
                .command("update", update -> update.onExecute(this::update))
                .setHelp("command.update.help")
                .buildCommand();
    }

    private void update(IMessage message, List<Object> args) {
        if (inProgess) {
            message.getChannel().sendMessage(Localizer.localize("command.update.error.in_progress"));
            return;
        }
        inProgess = true;

        logger.debug("Checking for updates...");
        JsonObject json = WebHelper.fetchJson(LATEST_RELEASE_URL);

        if (json == null) {
            message.getChannel().sendMessage(Localizer.localize("command.update.error.no_response"));
            inProgess = false;
            return;
        }

        File currentJar;
        try {
            currentJar = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if(!currentJar.getName().endsWith(".jar")) {
                message.getChannel().sendMessage(Localizer.localize("command.update.error.no_jar"));
                inProgess = false;
                return;
            }
        }
        catch (URISyntaxException ex) {
                message.getChannel().sendMessage(Localizer.localize("command.error.no_jar"));
                inProgess = false;
                return;
        }

        if (json.get("message") != null) {
            String errMessage = json.get("message").getAsString();
            if (errMessage.equals("not found")) {
                message.getChannel().sendMessage(Localizer.localize("command.update.error.not_found", REPO));
            }
            else {
                message.getChannel().sendMessage(Localizer.localize("command.update.error.github", errMessage));
            }

            inProgess = false;
            return;
        }

        String tag_name = json.get("tag_name").getAsString();

        if (tag_name.equals(Bot.version)) {
            message.getChannel().sendMessage(Localizer.localize("command.update.error.up_to_date", tag_name));
            inProgess = false;
            return;
        }

        String downloadName = null;
        String downloadUrl = null;
        for (JsonElement element : json.getAsJsonArray("assets")) {
            JsonObject asset = element.getAsJsonObject();
            if (asset.get("name").getAsString().contains("with-dependencies")) {
                downloadName = asset.get("name").getAsString();
                downloadUrl = asset.get("browser_download_url").getAsString();
                break;
            }
        }

        if (downloadUrl == null) {
            message.getChannel().sendMessage(Localizer.localize("command.update.error.url_not_found"));
            inProgess = false;
            return;
        }

        message.getChannel().sendMessage(Localizer.localize("command.update.success.downloading", tag_name));

        String currentLang = Localizer.getCurrentLang();
        String finalDownloadUrl = downloadUrl; // vars in lambdas need to be final.
        String finalDownloadName = downloadName;
        new Thread(() -> {
            try {
                logger.info("Downloading update {} from {}", tag_name, finalDownloadUrl);
                // Remote file
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(finalDownloadUrl).openStream());

                // Local file
                FileOutputStream fileOutputStream = new FileOutputStream(finalDownloadName);

                // Download / transfer
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                logger.info("Done downloading update {}", finalDownloadName);
                Localizer.setCurrentLang(currentLang);
                message.getChannel().sendMessage(Localizer.localize("command.update.success.starting", tag_name));

                // Starting new process
                String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
                ArrayList<String> command = new ArrayList<>();
                command.add(javaBin);
                command.add("-jar");
                command.add(Paths.get(finalDownloadName).toString());
                command.add("--update");
                command.add(currentJar.getName());

                ProcessBuilder builder = new ProcessBuilder(command);
                builder.inheritIO();
                builder.start();

                Bot.instance.shutdown();
                // Don't System.exit because that breaks inheritIO
            }
            catch (Exception ex) {
                logger.error("Exception while downloading update {}: {}", tag_name, ex);
                message.getChannel().sendMessage(Localizer.localize("command.update.error.error_downloading"));
            }
        }, "update thread").start();
    }
}
