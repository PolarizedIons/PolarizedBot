package net.polarizedions.polarizedbot.announcer;

import net.polarizedions.polarizedbot.api_handlers.MojangApi;
import sx.blah.discord.handle.obj.IChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AnnouncerMcNotifier implements IAnnouncer {
    private MojangApi.MinecraftVersions prevVersions;
    private boolean newSnapshot, newRelease;

    @Override
    public String getName() {
        return "mc-releases";
    }

    @Override
    public long updateFrequency() {
        return TimeUnit.MINUTES.toMillis(2);
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
    public void execute(List<IChannel> channels) {
        for (IChannel channel : channels) {
            if (this.newSnapshot) {
                // TODO: localize
                channel.sendMessage("New snapshot " + prevVersions.snapshot);
            }

            if (this.newRelease) {
                // TODO: localize
                channel.sendMessage("New release " + prevVersions.release);
            }
        }
    }
}
