package mocks;

import net.polarizedions.polarizedbot.Bot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.cache.LongMap;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;

public class MockUser implements IUser {
    private final long userID;

    public MockUser(long userID) {
        this.userID = userID;
        Bot.logger.info("[Mock] IUser {} created", userID);

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    @Override
    public String getAvatarURL() {
        return null;
    }

    @Override
    public IPresence getPresence() {
        return null;
    }

    @Override
    public String getDisplayName(IGuild guild) {
        return null;
    }

    @Override
    public String mention() {
        return null;
    }

    @Override
    public String mention(boolean mentionWithNickname) {
        return null;
    }

    @Override
    public String getDiscriminator() {
        return null;
    }

    @Override
    public List<IRole> getRolesForGuild(IGuild guild) {
        return null;
    }

    @Override
    public Color getColorForGuild(IGuild guild) {
        return null;
    }

    @Override
    public EnumSet<Permissions> getPermissionsForGuild(IGuild guild) {
        return null;
    }

    @Override
    public String getNicknameForGuild(IGuild guild) {
        return null;
    }

    @Override
    public IVoiceState getVoiceStateForGuild(IGuild guild) {
        return null;
    }

    @Override
    public LongMap<IVoiceState> getVoiceStates() {
        return null;
    }

    @Override
    public void moveToVoiceChannel(IVoiceChannel channel) {

    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public IPrivateChannel getOrCreatePMChannel() {
        return null;
    }

    @Override
    public void addRole(IRole role) {

    }

    @Override
    public void removeRole(IRole role) {

    }

    @Override
    public boolean hasRole(IRole role) {
        return false;
    }

    @Override
    public IDiscordClient getClient() {
        return null;
    }

    @Override
    public IShard getShard() {
        return null;
    }

    @Override
    public IUser copy() {
        return null;
    }

    @Override
    public long getLongID() {
        return this.userID;
    }
}
