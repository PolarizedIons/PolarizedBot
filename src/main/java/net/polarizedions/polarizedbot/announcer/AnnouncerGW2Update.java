package net.polarizedions.polarizedbot.announcer;

import net.polarizedions.polarizedbot.api_handlers.GW2ForumApi;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AnnouncerGW2Update implements IAnnouncer {
    List<GW2ForumApi.GW2UpdateLising> toAnnounce = new ArrayList<>();
    private int lastUpdateID;

    @Override
    public String getName() {
        return "gw2-updates";
    }

    @Override
    public long updateFrequency() {
        return TimeUnit.MINUTES.toMillis(10);
    }

    @Override
    public boolean check() {
        List<GW2ForumApi.GW2UpdateLising> newUpdates = GW2ForumApi.fetchUpdates();
        if (newUpdates == null) {
            return false;
        }

        // Bot just started
        if (lastUpdateID == 0) {
            toAnnounce = newUpdates;
            lastUpdateID = newUpdates.get(0).id;
            return false;
        }

        this.toAnnounce.clear();
        for (GW2ForumApi.GW2UpdateLising listing : newUpdates) {
            if (listing.id == lastUpdateID) {
                break;
            }

            this.toAnnounce.add(listing);
        }

        // TODO: update comments too
        lastUpdateID = newUpdates.get(0).id;

        return toAnnounce.size() > 0;
    }

    @Override
    public void execute(List<IChannel> channels) {
        for (IChannel channel : channels) {
            for (GW2ForumApi.GW2UpdateLising listing : this.toAnnounce) {
                // TODO: localize
                channel.sendMessage("GW2 update found: " +  listing.title);
            }
        }

        this.toAnnounce.clear();
    }
}
