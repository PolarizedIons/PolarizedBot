package net.polarizedions.polarizedbot.util;

import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReactionListener {
    private Map<Long, Function<ReactionAddEvent, Boolean>> addListeners;
    private Map<Long, Function<ReactionRemoveEvent, Boolean>> removeListeners;

    public ReactionListener(@NotNull IDiscordClient client) {
        client.getDispatcher().registerListener(this);

        this.addListeners = new HashMap<>();
        this.removeListeners = new HashMap<>();
    }

    public void reactionAddedListener(IMessage message, Function<ReactionAddEvent, Boolean> callback) {
        this.addListeners.put(message.getLongID(), callback);
    }

    public void reactionRemovedListener(IMessage message, Function<ReactionRemoveEvent, Boolean> callback) {
        this.removeListeners.put(message.getLongID(), callback);
    }

    public void stopAddListener(IMessage message) {
        this.addListeners.remove(message.getLongID());
    }

    public void stopRemoveListener(IMessage message) {
        this.removeListeners.remove(message.getLongID());
    }

    @EventSubscriber
    public void onReactionAdded(@NotNull ReactionAddEvent event) {
        Function<ReactionAddEvent, Boolean> callback = this.addListeners.get(event.getMessageID());
        if (callback != null) {
            if (callback.apply(event)) {
                this.stopAddListener(event.getMessage());
            }
        }
    }

    @EventSubscriber
    public void onReactionRemoved(@NotNull ReactionRemoveEvent event) {
        Function<ReactionRemoveEvent, Boolean> callback = this.removeListeners.get(event.getMessageID());
        if (callback != null) {
            if (callback.apply(event)) {
                this.stopRemoveListener(event.getMessage());
            }
        }
    }
}
