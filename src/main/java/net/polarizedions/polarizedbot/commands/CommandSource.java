package net.polarizedions.polarizedbot.commands;

import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.UserRank;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandSource {
    public final IMessage message;
    public final IChannel channel;
    public final IGuild guild;
    public final IUser user;
    private Localizer localizer;

    public CommandSource(@NotNull IMessage message) {
        this.message = message;
        this.channel = message.getChannel();
        this.guild = message.getGuild();
        this.user = message.getAuthor();
    }

    public UserRank getRank() {
        return GuildManager.getUserRank(this.guild, this.user);
    }

    public GuildConfig getGuildConfig() {
        return GuildManager.getConfig(this.guild);
    }

    public void saveGuildConfig() {
        GuildManager.saveConfig(this.guild);
    }

    public Localizer getLocalizer() {
        if (this.localizer == null) {
            this.localizer = new Localizer(this.getGuildConfig().lang);
        }

        return this.localizer;
    }

    public boolean hasPermission(UserRank requiredRank) {
        return GuildManager.userHasRank(this.guild, this.user, requiredRank);
    }
}
