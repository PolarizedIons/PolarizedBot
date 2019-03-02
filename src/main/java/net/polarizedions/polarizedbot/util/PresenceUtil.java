package net.polarizedions.polarizedbot.util;

import net.polarizedions.polarizedbot.Bot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;

import java.util.Timer;
import java.util.TimerTask;

public class PresenceUtil {
    private int index;
    private String[] presences;
    private Formatter formatter;
    private Timer timer;
    private TimerTask task;
    private long delayMs;

    public PresenceUtil(String[] presences, long delayMs) {
        this.index = -1;
        this.presences = presences;
        this.formatter = new Formatter();
        this.timer = new Timer();
        this.task = new TimerTask() {
            @Override
            public void run() {
                PresenceUtil.this.updatePresence();
            }
        };
        this.delayMs = delayMs;
    }

    public void init() {
        IDiscordClient client = Bot.instance.getClient();
        IUser botUser = client.getOurUser();
        IUser ownerUser = client.getApplicationOwner();

        this.formatter.addArg("bot-name", botUser.getName())
                      .addArg("bot-name-full", botUser.getName() + "#" + botUser.getDiscriminator())
                      .addArg("owner-name", ownerUser.getName())
                      .addArg("owner-name-full", ownerUser.getName() + "#" + ownerUser.getDiscriminator())
                      .addArg("guilds-num", () -> String.valueOf(Bot.instance.getClient().getGuilds().size()))
        ;

        this.timer.scheduleAtFixedRate(this.task, 1000, delayMs);
    }

    private void updatePresence() {
        this.index = ++this.index % this.presences.length;
        try {
            Bot.instance.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, this.formatter.format(this.presences[this.index]));
        }
        catch (DiscordException ex) {
            Bot.logger.error("Error updating presence..", ex);
        }
    }

    public void stop() {
        this.timer.cancel();
    }
}
