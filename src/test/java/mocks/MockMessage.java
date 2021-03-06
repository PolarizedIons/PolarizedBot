package mocks;

import com.vdurmont.emoji.Emoji;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageTokenizer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class MockMessage implements IMessage {
    public MockChannel channel;
    public long messageID;
    public String content;
    public MockUser user;
    public Instant time;

    public MockMessage() {
        this("");
    }

    public MockMessage(String content) {
        this((long)( Math.random() * 5000 ), new MockChannel(), content);
    }

    public MockMessage(long messageID, MockChannel channel, String content) {
        this(messageID, channel, content, new MockUser((long)( Math.random() * 5000 )));
    }

    public MockMessage(long messageID, MockChannel channel, String content, MockUser user) {
        this.messageID = messageID;
        this.channel = channel;
        this.content = content;
        this.user = user;

        this.channel.sentMessages.add(this);
        this.time = Instant.now();
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public IChannel getChannel() {
        return this.channel;
    }

    @Override
    public IUser getAuthor() {
        return this.user;
    }

    @Override
    public Instant getTimestamp() {
        return time;
    }

    @Override
    public List<IUser> getMentions() {
        return null;
    }

    @Override
    public List<IRole> getRoleMentions() {
        return null;
    }

    @Override
    public List<IChannel> getChannelMentions() {
        return null;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }

    @Override
    public List<IEmbed> getEmbeds() {
        return null;
    }

    @Override
    public IMessage reply(String content) {
        return null;
    }

    @Override
    public IMessage reply(String content, EmbedObject embed) {
        return null;
    }

    @Override
    public IMessage edit(String content) {
        return null;
    }

    @Override
    public IMessage edit(String content, EmbedObject embed) {
        return null;
    }

    @Override
    public IMessage edit(EmbedObject embed) {
        return null;
    }

    @Override
    public boolean mentionsEveryone() {
        return false;
    }

    @Override
    public boolean mentionsHere() {
        return false;
    }

    @Override
    public void delete() {

    }

    @Override
    public Optional<Instant> getEditedTimestamp() {
        return Optional.empty();
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @Override
    public IGuild getGuild() {
        return null;
    }

    @Override
    public String getFormattedContent() {
        return null;
    }

    @Override
    public List<IReaction> getReactions() {
        return null;
    }

    @Override
    public IReaction getReactionByEmoji(IEmoji emoji) {
        return null;
    }

    @Override
    public IReaction getReactionByID(long id) {
        return null;
    }

    @Override
    public IReaction getReactionByUnicode(Emoji unicode) {
        return null;
    }

    @Override
    public IReaction getReactionByUnicode(String unicode) {
        return null;
    }

    @Override
    public IReaction getReactionByEmoji(ReactionEmoji emoji) {
        return null;
    }

    @Override
    public void addReaction(IReaction reaction) {

    }

    @Override
    public void addReaction(IEmoji emoji) {

    }

    @Override
    public void addReaction(Emoji emoji) {

    }

    @Override
    public void addReaction(ReactionEmoji emoji) {

    }

    @Override
    public void removeReaction(IUser user, IReaction reaction) {

    }

    @Override
    public void removeReaction(IUser user, ReactionEmoji emoji) {

    }

    @Override
    public void removeReaction(IUser user, IEmoji emoji) {

    }

    @Override
    public void removeReaction(IUser user, Emoji emoji) {

    }

    @Override
    public void removeReaction(IUser user, String emoji) {

    }

    @Override
    public void removeAllReactions() {

    }

    @Override
    public MessageTokenizer tokenize() {
        return null;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public long getWebhookLongID() {
        return 0;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isSystemMessage() {
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
    public IMessage copy() {
        return new MockMessage(this.messageID, this.channel, this.content, this.user);
    }

    @Override
    public long getLongID() {
        return this.messageID;
    }
}
