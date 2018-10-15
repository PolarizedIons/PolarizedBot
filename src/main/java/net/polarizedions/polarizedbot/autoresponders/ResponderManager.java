package net.polarizedions.polarizedbot.autoresponders;

import net.polarizedions.polarizedbot.autoresponders.impl.GoodBot;
import net.polarizedions.polarizedbot.autoresponders.impl.MeasurementConverter;
import net.polarizedions.polarizedbot.autoresponders.impl.TempConverter;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.GuildManager;
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

        this.responders.add(new TempConverter());
        this.responders.add(new MeasurementConverter());
        this.responders.add(new GoodBot());
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
        boolean commandPrefix = content.startsWith(guildConfig.commandPrefix);

        for (IResponder responder : this.responders) {
            if (guildConfig.disabledResponders.contains(responder.getID())) {
                continue;
            }

            if (commandPrefix) {
                boolean allow = false;
                for (String prefix : responder.getPrefixWhitelist()) {
                    if (content.startsWith(prefix)) {
                        allow = true;
                        break;
                    }
                }

                if (! allow) {
                    continue;
                }
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
