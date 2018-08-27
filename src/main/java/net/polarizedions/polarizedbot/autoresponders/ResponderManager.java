package net.polarizedions.polarizedbot.autoresponders;

import net.polarizedions.polarizedbot.autoresponders.impl.*;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResponderManager {
    private List<IResponder> responders;

    public ResponderManager() {
        this.responders = new ArrayList<>();

        this.init();
    }

    private void init() {
        this.responders.add(new AutoUnitConverter());
    }

    public void registerListeners(IDiscordClient discordClient) {
        discordClient.getDispatcher().registerListener((IListener<MessageReceivedEvent>) this::messageHandler);
    }

    private void messageHandler(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();

        if (guild == null) {
            return;
        }

        GuildConfig guildConfig = GuildManager.getConfig(guild);

        if (guildConfig.ignoredUsers.contains(user.getLongID())) {
            return;
        }

        if (message.getContent().startsWith(guildConfig.commandPrefix)) {
            return;
        }

        for (IResponder responder : this.responders) {
            if (guildConfig.disabledResponders.contains(responder.getID())) {
                continue;
            }
            responder.run(message);
        }
    }

    public List<IResponder> getResponders() {
        return this.responders;
    }

    public List<String> getIDs() {
        return this.responders.parallelStream().map(IResponder::getID).map(String::toLowerCase).collect(Collectors.toList());
    }

}
