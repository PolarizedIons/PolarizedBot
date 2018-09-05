package mocks;

import net.polarizedions.polarizedbot.Bot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IInvite;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IRegion;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.modules.ModuleLoader;
import sx.blah.discord.util.Image;

import java.util.List;

public class MockDiscordClient implements IDiscordClient {
    public MockDiscordClient() {
        Bot.logger.info("[Mock] DiscordClient created");
    }

    @Override
    public EventDispatcher getDispatcher() {
        return null;
    }

    @Override
    public ModuleLoader getModuleLoader() {
        return null;
    }

    @Override
    public List<IShard> getShards() {
        return null;
    }

    @Override
    public int getShardCount() {
        return 0;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {

    }

    @Override
    public void changeUsername(String username) {

    }

    @Override
    public void changeAvatar(Image avatar) {

    }

    @Override
    public void changePresence(StatusType status, ActivityType activity, String text) {

    }

    @Override
    public void changePresence(StatusType type) {

    }

    @Override
    public void changeStreamingPresence(StatusType status, String text, String streamUrl) {

    }

    @Override
    public void mute(IGuild guild, boolean isSelfMuted) {

    }

    @Override
    public void deafen(IGuild guild, boolean isSelfDeafened) {

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public IUser getOurUser() {
        return null;
    }

    @Override
    public List<IChannel> getChannels(boolean includePrivate) {
        return null;
    }

    @Override
    public List<IChannel> getChannels() {
        return null;
    }

    @Override
    public IChannel getChannelByID(long channelID) {
        return new MockChannel(channelID);
    }

    @Override
    public List<IVoiceChannel> getVoiceChannels() {
        return null;
    }

    @Override
    public IVoiceChannel getVoiceChannelByID(long id) {
        return null;
    }

    @Override
    public List<IGuild> getGuilds() {
        return null;
    }

    @Override
    public IGuild getGuildByID(long guildID) {
        return null;
    }

    @Override
    public List<IUser> getUsers() {
        return null;
    }

    @Override
    public IUser getUserByID(long userID) {
        return new MockUser(userID);
    }

    @Override
    public IUser fetchUser(long id) {
        return null;
    }

    @Override
    public List<IUser> getUsersByName(String name) {
        return null;
    }

    @Override
    public List<IUser> getUsersByName(String name, boolean ignoreCase) {
        return null;
    }

    @Override
    public List<IRole> getRoles() {
        return null;
    }

    @Override
    public IRole getRoleByID(long roleID) {
        return null;
    }

    @Override
    public List<IMessage> getMessages(boolean includePrivate) {
        return null;
    }

    @Override
    public List<IMessage> getMessages() {
        return null;
    }

    @Override
    public IMessage getMessageByID(long messageID) {
        return null;
    }

    @Override
    public IPrivateChannel getOrCreatePMChannel(IUser user) {
        return null;
    }

    @Override
    public IInvite getInviteForCode(String code) {
        return null;
    }

    @Override
    public List<IRegion> getRegions() {
        return null;
    }

    @Override
    public IRegion getRegionByID(String regionID) {
        return null;
    }

    @Override
    public List<IVoiceChannel> getConnectedVoiceChannels() {
        return null;
    }

    @Override
    public String getApplicationDescription() {
        return null;
    }

    @Override
    public String getApplicationIconURL() {
        return null;
    }

    @Override
    public String getApplicationClientID() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public IUser getApplicationOwner() {
        return null;
    }

    @Override
    public List<ICategory> getCategories() {
        return null;
    }

    @Override
    public ICategory getCategoryByID(long categoryID) {
        return null;
    }

    @Override
    public List<ICategory> getCategoriesByName(String name) {
        return null;
    }
}
