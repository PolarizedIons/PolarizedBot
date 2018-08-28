package net.polarizedions.polarizedbot.announcer;

import com.google.gson.*;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.util.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AnnouncerManager {
    private static final Logger logger = LogManager.getLogger("AnnouncerManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, IAnnouncer> announcers;
    private Map<IAnnouncer, List<IChannel>> subData;
    private List<Timer> timers;

    public AnnouncerManager() {
        this.announcers = new HashMap<>();
        this.subData = new HashMap<>();
        this.timers = new ArrayList<>();

        registerAnnouncer(new AnnouncerMcNotifier());
        registerAnnouncer(new AnnouncerGW2Update());
    }

    private void registerAnnouncer(IAnnouncer announcer) {
        this.announcers.put(announcer.getName(), announcer);
    }

    public void initAnnouncers() {
        for (IAnnouncer announcer : this.announcers.values()) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (subData.getOrDefault(announcer, Collections.emptyList()).size() == 0) {
                        return;
                    }

                    try {
                        boolean result = announcer.check();
                        logger.debug("Checking announcer '{}': {}", announcer.getName(), result);
                        if (result) {
                            announcer.execute(getSubData(announcer));
                        }
                    }
                    catch (Exception e) {
                        logger.debug("Exception while executing announcer " + announcer.getName(), e);
                    }
                }
            };

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(task, 500, announcer.updateFrequency());
            this.timers.add(timer);
        }
    }

    public IAnnouncer getAnnouncer(String name) {
        return announcers.get(name);
    }

    public String[] getNames() {
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

        save();
    }

    public void forgetSub(IAnnouncer announcer, IChannel channel) {
        this.subData.get(announcer).remove(channel);
        if (this.subData.get(announcer).size() == 0) {
            this.subData.remove(announcer);
        }

        save();
    }

    public List<IChannel> getSubData(IAnnouncer announcer) {
        return subData.getOrDefault(announcer, Collections.emptyList());
    }

    public void load() {
        File saveFile = Paths.get(ConfigManager.configDir.getAbsolutePath(), "announcements.json").toFile();

        if (!saveFile.exists()) {
            return;
        }

        logger.info("Loading announcement sub data from: {}", saveFile);
        JsonObject json;
        try {
            json = new JsonParser().parse(new FileReader(saveFile)).getAsJsonObject();
        } catch (FileNotFoundException ex) {
            logger.error("Save file not found", ex);
            return;
        }

        IDiscordClient client = Bot.instance.getClient();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            IAnnouncer announcer = getAnnouncer(entry.getKey());
            if (announcer == null) {
                logger.error("Error loading data: unknown announcer '{}'", entry.getKey());
                continue;
            }

            List<IChannel> channels = new ArrayList<>();
            for (JsonElement jsonLong : entry.getValue().getAsJsonArray()) {
                long channelID = jsonLong.getAsLong();
                IChannel channel = client.getChannelByID(channelID);
                if (channel == null) {
                    logger.error("Unable to get channel from id ({}) while loading announcement data for {}, dropping.", channelID, announcer.getName());
                    continue;
                }
                channels.add(channel);
            }

            subData.put(announcer, channels);
        }
    }

    public void save() {
        File saveFile = Paths.get(ConfigManager.configDir.getAbsolutePath(), "announcements.json").toFile();
        logger.info("Saving announcements sub data to: {}", saveFile);

        Map<String, List<Long>> data = new HashMap<>();
        for (Map.Entry<IAnnouncer, List<IChannel>> entry : subData.entrySet()) {
            data.put(entry.getKey().getName(), entry.getValue().parallelStream().map(channel -> channel.getLongID()).collect(Collectors.toList()));
        }

        try {
            Writer writer = new FileWriter(saveFile);
            GSON.toJson(data, writer);
            writer.close();
        } catch (IOException ex) {
            logger.error("Error saving announcement data", ex);
        }
    }

    public void stop() {
        for (Timer timer : this.timers) {
            timer.cancel();
        }
    }
}
