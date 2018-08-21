package io.github.polarizedions.polarizedbot.announcer;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.util.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AnnouncerManager {
    private static final Logger logger = LogManager.getLogger("AnnouncerManager");
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

    @SuppressWarnings("unchecked")
    public void load() {
        File saveFile = Paths.get(ConfigManager.configDir.getAbsolutePath(), "announcements.toml").toFile();

        if (!saveFile.exists()) {
            return;
        }

        logger.info("Loaded announcement sub data from: {}", saveFile);
        Toml toml = new Toml().read(saveFile);
        IDiscordClient client = Bot.instance.getClient();

        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            IAnnouncer announcer = getAnnouncer(entry.getKey());
            if (announcer == null) {
                logger.error("Error loading data: unknown announcer '{}'", entry.getKey());
                continue;
            }

            if (!(entry.getValue() instanceof List)) {
                logger.error("Error loading data: value not list");
                continue;
            }

            List<Long> data;
            try {
                data = (List<Long>) entry.getValue();
            }
            catch (ClassCastException e) {
                logger.error("Error loading data: failed to cast list");
                continue;
            }

            List<IChannel> channels = new ArrayList<>();
            for (Long channelID : data) {
                IChannel channel = client.getChannelByID(channelID);
                if (channel == null) {
                    logger.error("Unable to get channel from id ({}) while loading announcement data for {}, dropping.", channelID, announcer.getName());
                }
            }

            subData.put(announcer, channels);
        }
    }

    public void save() {
        File saveFile = Paths.get(ConfigManager.configDir.getAbsolutePath(), "announcements.toml").toFile();
        logger.info("Saving announcements sub data to: {}", saveFile);

        Map<String, List<Long>> data = new HashMap<>();
        for (Map.Entry<IAnnouncer, List<IChannel>> entry : subData.entrySet()) {
            data.put(entry.getKey().getName(), entry.getValue().parallelStream().map(channel -> channel.getLongID()).collect(Collectors.toList()));
        }

        TomlWriter tomlWriter = new TomlWriter();
        try {
            tomlWriter.write(data, saveFile);
        } catch (IOException e) {
            logger.error("Error saving announcement sub data: ", e);
        }
    }

    public void stop() {
        for (Timer timer : this.timers) {
            timer.cancel();
        }
    }
}
