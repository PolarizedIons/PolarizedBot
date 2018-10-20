package mocks;

import net.polarizedions.polarizedbot.Bot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IExtendedInvite;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IInvite;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IWebhook;
import sx.blah.discord.handle.obj.PermissionOverride;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.AttachmentPartEntry;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.cache.LongMap;

import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MockChannel implements IChannel {
    private final long channelID;
    public List<IMessage> sentMessages;

    public MockChannel() {
        this((long)( Math.random() * 7823 ));
    }

    public MockChannel(long channelID) {
        this.channelID = channelID;
        this.sentMessages = new LinkedList<>();
        Bot.logger.info("[Mock] IChannel {} created", channelID);
    }

    public List<String> getSentContent() {
        return this.sentMessages.stream().map(IMessage::getContent).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public MessageHistory getMessageHistory() {
        return new MessageHistory(this.sentMessages);
    }

    @Override
    public MessageHistory getMessageHistory(int messageCount) {
        int start = Math.max(0, this.sentMessages.size() - messageCount - 1);
        int end = Math.min(messageCount -1, this.sentMessages.size());
        return new MessageHistory(this.sentMessages.subList(start, end));
    }

    @Override
    public MessageHistory getMessageHistoryFrom(Instant startDate) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryFrom(Instant startDate, int maxMessageCount) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryFrom(long id) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryFrom(long id, int maxMessageCount) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryTo(Instant endDate) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryTo(Instant endDate, int maxMessageCount) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryTo(long id) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryTo(long id, int maxMessageCount) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryIn(Instant startDate, Instant endDate) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryIn(Instant startDate, Instant endDate, int maxMessageCount) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryIn(long beginID, long endID) {
        return null;
    }

    @Override
    public MessageHistory getMessageHistoryIn(long beginID, long endID, int maxMessageCount) {
        return null;
    }

    @Override
    public MessageHistory getFullMessageHistory() {
        return null;
    }

    @Override
    public List<IMessage> bulkDelete() {
        return null;
    }

    @Override
    public List<IMessage> bulkDelete(List<IMessage> messages) {
        return null;
    }

    @Override
    public int getMaxInternalCacheCount() {
        return 0;
    }

    @Override
    public int getInternalCacheCount() {
        return 0;
    }

    @Override
    public IMessage getMessageByID(long messageID) {
        return null;
    }

    @Override
    public IMessage fetchMessage(long messageID) {
        return null;
    }

    @Override
    public IGuild getGuild() {
        return null;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }

    @Override
    public String getTopic() {
        return null;
    }

    @Override
    public String mention() {
        return null;
    }

    @Override
    public IMessage sendMessage(String content) {
        IMessage msg = new MockMessage(content);
        this.sentMessages.add(msg);
        return msg;
    }

    @Override
    public IMessage sendMessage(EmbedObject embed) {
        return null;
    }

    @Override
    public IMessage sendMessage(String content, boolean tts) {
        return null;
    }

    @Override
    public IMessage sendMessage(String content, EmbedObject embed) {
        return null;
    }

    @Override
    public IMessage sendMessage(String content, EmbedObject embed, boolean tts) {
        return null;
    }

    @Override
    public IMessage sendFile(File file) {
        return null;
    }

    @Override
    public IMessage sendFiles(File... files) {
        return null;
    }

    @Override
    public IMessage sendFile(String content, File file) {
        return null;
    }

    @Override
    public IMessage sendFiles(String content, File... files) {
        return null;
    }

    @Override
    public IMessage sendFile(EmbedObject embed, File file) {
        return null;
    }

    @Override
    public IMessage sendFiles(EmbedObject embed, File... files) {
        return null;
    }

    @Override
    public IMessage sendFile(String content, InputStream file, String fileName) {
        return null;
    }

    @Override
    public IMessage sendFiles(String content, AttachmentPartEntry... entries) {
        return null;
    }

    @Override
    public IMessage sendFile(EmbedObject embed, InputStream file, String fileName) {
        return null;
    }

    @Override
    public IMessage sendFiles(EmbedObject embed, AttachmentPartEntry... entries) {
        return null;
    }

    @Override
    public IMessage sendFile(String content, boolean tts, InputStream file, String fileName) {
        return null;
    }

    @Override
    public IMessage sendFiles(String content, boolean tts, AttachmentPartEntry... entries) {
        return null;
    }

    @Override
    public IMessage sendFile(String content, boolean tts, InputStream file, String fileName, EmbedObject embed) {
        return null;
    }

    @Override
    public IMessage sendFiles(String content, boolean tts, EmbedObject embed, AttachmentPartEntry... entries) {
        return null;
    }

    @Override
    public IMessage sendFile(MessageBuilder builder, InputStream file, String fileName) {
        return null;
    }

    @Override
    public IInvite createInvite(int maxAge, int maxUses, boolean temporary, boolean unique) {
        return null;
    }

    @Override
    public void toggleTypingStatus() {

    }

    @Override
    public boolean getTypingStatus() {
        return false;
    }

    @Override
    public void setTypingStatus(boolean typing) {

    }

    @Override
    public void edit(String name, int position, String topic) {

    }

    @Override
    public void changeName(String name) {

    }

    @Override
    public void changePosition(int position) {

    }

    @Override
    public void changeTopic(String topic) {

    }

    @Override
    public void changeNSFW(boolean isNSFW) {

    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public void delete() {

    }

    @Override
    public LongMap<PermissionOverride> getUserOverrides() {
        return null;
    }

    @Override
    public LongMap<PermissionOverride> getRoleOverrides() {
        return null;
    }

    @Override
    public EnumSet<Permissions> getModifiedPermissions(IUser user) {
        return null;
    }

    @Override
    public EnumSet<Permissions> getModifiedPermissions(IRole role) {
        return null;
    }

    @Override
    public void removePermissionsOverride(IUser user) {

    }

    @Override
    public void removePermissionsOverride(IRole role) {

    }

    @Override
    public void overrideRolePermissions(IRole role, EnumSet<Permissions> toAdd, EnumSet<Permissions> toRemove) {

    }

    @Override
    public void overrideUserPermissions(IUser user, EnumSet<Permissions> toAdd, EnumSet<Permissions> toRemove) {

    }

    @Override
    public List<IExtendedInvite> getExtendedInvites() {
        return null;
    }

    @Override
    public List<IUser> getUsersHere() {
        return null;
    }

    @Override
    public List<IMessage> getPinnedMessages() {
        return null;
    }

    @Override
    public void pin(IMessage message) {

    }

    @Override
    public void unpin(IMessage message) {

    }

    @Override
    public List<IWebhook> getWebhooks() {
        return null;
    }

    @Override
    public IWebhook getWebhookByID(long id) {
        return null;
    }

    @Override
    public List<IWebhook> getWebhooksByName(String name) {
        return null;
    }

    @Override
    public IWebhook createWebhook(String name) {
        return null;
    }

    @Override
    public IWebhook createWebhook(String name, Image avatar) {
        return null;
    }

    @Override
    public IWebhook createWebhook(String name, String avatar) {
        return null;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public void changeCategory(ICategory category) {

    }

    @Override
    public ICategory getCategory() {
        return null;
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
    public IChannel copy() {
        return null;
    }

    @Override
    public long getLongID() {
        return this.channelID;
    }
}
