package net.polarizedions.polarizedbot.util;

import discord4j.core.DiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import net.polarizedions.polarizedbot.Bot;

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
        DiscordClient client = Bot.instance.getClient();
        client.getSelf().subscribe(botUser ->
                client.getApplicationInfo().subscribe(appInfo -> appInfo.getOwner().subscribe(ownerUser -> {
                    this.formatter.addArg("bot-name", botUser.getUsername())
                            .addArg("bot-name-full", botUser.getUsername() + "#" + botUser.getDiscriminator())
                            .addArg("owner-name", ownerUser.getUsername())
                            .addArg("owner-name-full", ownerUser.getUsername() + "#" + ownerUser.getDiscriminator())
                    ;

                    this.timer.scheduleAtFixedRate(this.task, 1000, delayMs);
                }))
        );
    }

    private void updatePresence() {
        this.index = ++this.index % this.presences.length;
        Bot.instance.getClient().updatePresence(Presence.online(Activity.playing(this.formatter.format(this.presences[this.index]))));
    }

    public void stop() {
        this.timer.cancel();
    }
}
