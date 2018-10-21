package net.polarizedions.polarizedbot.announcer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.announcer.impl.AnnouncerGW2Update;
import net.polarizedions.polarizedbot.announcer.impl.AnnouncerMcNotifier;
import net.polarizedions.polarizedbot.util.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AnnouncerManager {
    private static final Logger logger = LogManager.getLogger("AnnouncerManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, IAnnouncer> announcers;
    private Map<IAnnouncer, List<IChannel>> subData;
    private ScheduledExecutorService scheduler;

    public AnnouncerManager() {
        this.announcers = new HashMap<>();
        this.subData = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));

        this.registerAnnouncer(new AnnouncerMcNotifier());
        this.registerAnnouncer(new AnnouncerGW2Update());
    }

    private void registerAnnouncer(IAnnouncer announcer) {
        this.announcers.put(announcer.getID(), announcer);
    }

    public void initAnnouncers() {
        for (IAnnouncer announcer : this.announcers.values()) {
            Runnable task = () -> {
                List<IChannel> subscriptionData = this.getSubData(announcer);
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

            this.scheduler.scheduleWithFixedDelay(task, 5, announcer.updateFrequency(), TimeUnit.SECONDS);
        }
    }

    public IAnnouncer getAnnouncer(String name) {
        return announcers.get(name);
    }

    public String[] getIDs() {
        return this.announcers.keySet().toArray(new String[0]);
    }

    public Map<IAnnouncer, List<IChannel>> getAnnouncersForGuild(IGuild guild) {
        Map<IAnnouncer, List<IChannel>> announcers = new HashMap<>();

        for (Map.Entry<IAnnouncer, List<IChannel>> entry : this.subData.entrySet()) {
            List<IChannel> channels = entry.getValue().parallelStream().filter(c -> c.getGuild().getLongID() == guild.getLongID()).collect(Collectors.toList());

            if (channels.size() > 0) {
                announcers.put(entry.getKey(), channels);
            }
        }

        return announcers;
    }

    public void addSub(IAnnouncer announcer, IChannel channel) {
        this.subData.computeIfAbsent(announcer, a -> new ArrayList<>());
        this.subData.get(announcer).add(channel);

        this.save();
    }

    public void forgetSub(IAnnouncer announcer, IChannel channel) {
        this.subData.get(announcer).remove(channel);
        if (this.subData.get(announcer).size() == 0) {
            this.subData.remove(announcer);
        }

        this.save();
    }

    public List<IChannel> getSubData(IAnnouncer announcer) {
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

        IDiscordClient client = Bot.instance.getClient();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            IAnnouncer announcer = getAnnouncer(entry.getKey());
            if (announcer == null) {
                logger.error("Error loading data: unknown announcer '{}'", entry.getKey());
                dirty = true;
                continue;
            }

            List<IChannel> channels = new ArrayList<>();
            for (JsonElement jsonLong : entry.getValue().getAsJsonArray()) {
                long channelID = jsonLong.getAsLong();
                IChannel channel = client.getChannelByID(channelID);
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
        for (Map.Entry<IAnnouncer, List<IChannel>> entry : subData.entrySet()) {
            data.put(entry.getKey().getID(), entry.getValue().parallelStream().map(channel -> channel.getLongID()).collect(Collectors.toList()));
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
        this.scheduler.shutdown();
    }
}
