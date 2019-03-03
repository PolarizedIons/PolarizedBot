package net.polarizedions.polarizedbot.util;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReactionListener {
    private Map<Long, Function<ReactionAddEvent, Boolean>> addListeners;
    private Map<Long, Function<ReactionRemoveEvent, Boolean>> removeListeners;

    public ReactionListener(@NotNull DiscordClient client) {


        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(this::onReactionAdded);
        client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(this::onReactionRemoved);

        this.addListeners = new HashMap<>();
        this.removeListeners = new HashMap<>();
    }

    private void reactionAddedListener(Message message, Function<ReactionAddEvent, Boolean> callback) {
        this.addListeners.put(message.getId().asLong(), callback);
    }

    public void reactionRemovedListener(Message message, Function<ReactionRemoveEvent, Boolean> callback) {
        this.removeListeners.put(message.getId().asLong(), callback);
    }

    public void stopAddListener(Message message) {
        this.addListeners.remove(message.getId().asLong());
    }

    public void stopRemoveListener(Message message) {
        this.removeListeners.remove(message.getId().asLong());
    }

    private void onReactionAdded(@NotNull ReactionAddEvent event) {
        Function<ReactionAddEvent, Boolean> callback = this.addListeners.get(event.getMessageId().asLong());
        if (callback != null) {
            if (callback.apply(event)) {
                this.stopAddListener(event.getMessage().block());
            }
        }
    }

    private void onReactionRemoved(@NotNull ReactionRemoveEvent event) {
        Function<ReactionRemoveEvent, Boolean> callback = this.removeListeners.get(event.getMessageId().asLong());
        if (callback != null) {
            if (callback.apply(event)) {
                this.stopRemoveListener(event.getMessage().block());
            }
        }
    }
}
