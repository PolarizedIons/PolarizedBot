package net.polarizedions.polarizedbot.announcer.impl;

import discord4j.core.object.entity.TextChannel;
import net.polarizedions.polarizedbot.announcer.IAnnouncer;
import net.polarizedions.polarizedbot.api_handlers.MojangApi;
import net.polarizedions.polarizedbot.util.Localizer;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AnnouncerMcNotifier implements IAnnouncer {
    private MojangApi.MinecraftVersions prevVersions;
    private boolean newSnapshot, newRelease;

    @Override
    public String getID() {
        return "minecraft";
    }

    @Override
    public long updateFrequency() {
        return TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public boolean check() {
        MojangApi.MinecraftVersions newVersions = MojangApi.fetchLatestVersions();
        if (newVersions == null) {
            return false;
        }

        if (prevVersions == null) {
            prevVersions = newVersions;
            return false;
        }

        this.newSnapshot = !prevVersions.snapshot.equals(newVersions.snapshot);
        this.newRelease = !prevVersions.release.equals(newVersions.release);

        prevVersions = newVersions;
        return this.newSnapshot || this.newRelease;
    }

    @Override
    public void execute(List<TextChannel> channels) {
        String releaseType = this.newSnapshot ? "snapshot" : "full";
        String version = this.newSnapshot ? this.prevVersions.snapshot : this.prevVersions.release;
        String imageUrl = this.newSnapshot ? "https://i.imgur.com/Km2ugwt.png" : "https://i.imgur.com/gO5ar1C.png";
        Color color = this.newSnapshot ? new Color(168/255f, 23/255f, 62/255f) : new Color(41/255f, 188/255f, 51/255f) ;

        for (TextChannel channel : channels) {
            channel.getGuild().subscribe(guild ->
                channel.createMessage(spec ->
                    spec.setEmbed(embedSpec -> {
                        embedSpec.setThumbnail(imageUrl);
                        embedSpec.setColor(color);

                        Localizer loc = new Localizer(guild);
                        embedSpec.setTitle(loc.localize("announcer.minecraft.header.title"));
                        embedSpec.addField(loc.localize("announcer.minecraft.header." + releaseType), loc.localize("announcer.minecraft.version", version), false);

                    })
                )
            );
        }
    }
}
