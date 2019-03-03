package net.polarizedions.polarizedbot.announcer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.announcer.impl.AnnouncerGW2Update;
import net.polarizedions.polarizedbot.announcer.impl.AnnouncerMcNotifier;
import net.polarizedions.polarizedbot.util.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AnnouncerManager {
    private static final Logger logger = LogManager.getLogger("AnnouncerManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, IAnnouncer> announcers;
    private Map<IAnnouncer, List<TextChannel>> subData;

    public AnnouncerManager() {
        this.announcers = new HashMap<>();
        this.subData = new HashMap<>();

        this.registerAnnouncer(new AnnouncerMcNotifier());
        this.registerAnnouncer(new AnnouncerGW2Update());
    }

    private void registerAnnouncer(IAnnouncer announcer) {
        this.announcers.put(announcer.getID(), announcer);
    }

    public void initAnnouncers() {
        for (IAnnouncer announcer : this.announcers.values()) {
            Runnable task = () -> {
                List<TextChannel> subscriptionData = this.getSubData(announcer);
                if (subscriptionData.size() == 0) {
                    return;
                }

                try {
                    boolean result = announcer.check();
                    logger.debug("Checking announcer '{}': {}", announcer.getID(), result);
                    if (result) {
                        announcer.execute(subscriptionData);
                    }
                }
                catch (Exception ex) {
                    logger.debug("Exception while executing announcer " + announcer.getID(), ex);
                }
            };

            Bot.instance.threadPool.scheduleWithFixedDelay(task, 5, announcer.updateFrequency(), TimeUnit.SECONDS);
        }
    }

    public IAnnouncer getAnnouncer(String name) {
        return announcers.get(name);
    }

    public String[] getIDs() {
        return this.announcers.keySet().toArray(new String[0]);
    }

    public Map<IAnnouncer, List<TextChannel>> getAnnouncersForGuild(Guild guild) {
        Map<IAnnouncer, List<TextChannel>> announcers = new HashMap<>();

        long guildId = guild.getId().asLong();

        for (Map.Entry<IAnnouncer, List<TextChannel>> entry : this.subData.entrySet()) {
            List<TextChannel> channels = entry.getValue().parallelStream().filter(c -> c.getGuild().block().getId().asLong() == guildId).collect(Collectors.toList());

            if (channels.size() > 0) {
                announcers.put(entry.getKey(), channels);
            }
        }

        return announcers;
    }

    public void addSub(IAnnouncer announcer, TextChannel channel) {
        this.subData.computeIfAbsent(announcer, a -> new ArrayList<>());
        this.subData.get(announcer).add(channel);

        this.save();
    }

    public void forgetSub(IAnnouncer announcer, TextChannel channel) {
        this.subData.get(announcer).remove(channel);
        if (this.subData.get(announcer).size() == 0) {
            this.subData.remove(announcer);
        }

        this.save();
    }

    public List<TextChannel> getSubData(IAnnouncer announcer) {
        return subData.getOrDefault(announcer, Collections.emptyList());
    }

    public void load() {
        File saveFile = Paths.get(ConfigManager.configDir.getAbsolutePath(), "announcements.json").toFile();
        boolean dirty = false;

        if (!saveFile.exists()) {
            return;
        }

        logger.info("Loading announcement sub data from: {}", saveFile);
        JsonObject json;
        try {
            json = new JsonParser().parse(new FileReader(saveFile)).getAsJsonObject();
        }
        catch (FileNotFoundException ex) {
            logger.error("Save file not found", ex);
            return;
        }

        DiscordClient client = Bot.instance.getClient();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            IAnnouncer announcer = getAnnouncer(entry.getKey());
            if (announcer == null) {
                logger.error("Error loading data: unknown announcer '{}'", entry.getKey());
                dirty = true;
                continue;
            }

            List<TextChannel> channels = new ArrayList<>();
            for (JsonElement jsonLong : entry.getValue().getAsJsonArray()) {
                long channelID = jsonLong.getAsLong();
                TextChannel channel = (TextChannel) client.getChannelById(Snowflake.of(channelID)).block();
                if (channel == null) {
                    logger.error("Unable to get channel from id ({}) while loading announcement data for {}, dropping.", channelID, announcer.getID());
                    dirty = true;
                    continue;
                }
                channels.add(channel);
            }

            subData.put(announcer, channels);
        }

        if (dirty) {
            this.save();
        }
    }

    public void save() {
        File saveFile = Paths.get(ConfigManager.configDir.getAbsolutePath(), "announcements.json").toFile();
        logger.info("Saving announcements sub data to: {}", saveFile);

        Map<String, List<Long>> data = new HashMap<>();
        for (Map.Entry<IAnnouncer, List<TextChannel>> entry : subData.entrySet()) {
            data.put(entry.getKey().getID(), entry.getValue().parallelStream().map(channel -> channel.getId().asLong()).collect(Collectors.toList()));
        }

        try {
            Writer writer = new FileWriter(saveFile);
            GSON.toJson(data, writer);
            writer.close();
        }
        catch (IOException ex) {
            logger.error("Error saving announcement data", ex);
        }
    }

    public void stop() {

    }
}
