package net.polarizedions.polarizedbot.autoresponders;

import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.GuildManager;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResponderManager {
    private List<IResponder> responders;
    private Set<String> prefixWhitelist;

    public ResponderManager() {
        this.responders = new ArrayList<>();
        this.prefixWhitelist = new HashSet<>();
    }

    private void addResponder(IResponder responder) {
        this.responders.add(responder);
        this.prefixWhitelist.addAll(responder.getPrefixWhitelist());
    }

    public void messageHandler(IMessage message) {
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();

        if (guild == null) {
            return;
        }

        if (user.isBot()) {
            return;
        }

        GuildConfig guildConfig = GuildManager.getConfig(guild);

        if (guildConfig.ignoredUsers.contains(user.getLongID())) {
            return;
        }

        String content = message.getContent();
        if (content.startsWith(guildConfig.commandPrefix)) {
            boolean allow = false;

            for (String prefix : this.prefixWhitelist) {
                if (content.startsWith(prefix)) {
                    allow = true;
                }
            }

            if (! allow) {
                return;
            }
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
