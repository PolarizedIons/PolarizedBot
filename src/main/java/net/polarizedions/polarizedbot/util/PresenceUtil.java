package net.polarizedions.polarizedbot.util;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import net.polarizedions.polarizedbot.Bot;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PresenceUtil {
    private static final Random random = new Random();

    private Bot bot;
    private int index;
    private String[] presences;
    private Formatter formatter;
    private Timer timer;
    private TimerTask task;
    private long delayMs;

    public PresenceUtil(Bot bot, String[] presences, long delayMs) {
        this.bot = bot;
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

    public void init(DiscordClient client) {
        client.getSelf().subscribe(botUser ->
                client.getApplicationInfo().subscribe(appInfo -> appInfo.getOwner().subscribe(ownerUser -> {
                    this.formatter.addArg("bot-name", botUser.getUsername())
                            .addArg("bot-name-full", botUser.getUsername() + "#" + botUser.getDiscriminator())
                            .addArg("owner-name", ownerUser.getUsername())
                            .addArg("owner-name-full", ownerUser.getUsername() + "#" + ownerUser.getDiscriminator())
                            .addArg("guild-count", () -> String.valueOf(client.getGuilds().count().block()))
                            .addArg("random-member", () -> this.randomMember(client))
                    ;

                    this.timer.scheduleAtFixedRate(this.task, 1000, delayMs);
                }))
        );
    }

    private void updatePresence() {
        this.index = random.nextInt(this.presences.length);
        this.bot.getClient().updatePresence(Presence.online(Activity.playing(this.formatter.format(this.presences[this.index]))));
    }

    public void stop() {
        this.timer.cancel();
    }

    @NotNull
    private String randomMember(@NotNull DiscordClient client) {
        List<Guild> guilds = client.getGuilds().collectList().block();
        Guild selectedGuild = guilds.get(random.nextInt(guilds.size()));

        int usersCount = selectedGuild.getMemberCount().getAsInt();
        Member user = selectedGuild.getMembers().elementAt(random.nextInt(usersCount)).block();
        return user.getUsername() + "#" + user.getDiscriminator();
    }
}
