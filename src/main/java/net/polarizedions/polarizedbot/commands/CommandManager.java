package net.polarizedions.polarizedbot.commands;

import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.impl.CommandAbout;
import net.polarizedions.polarizedbot.commands.impl.CommandAnnoucer;
import net.polarizedions.polarizedbot.commands.impl.CommandGuild;
import net.polarizedions.polarizedbot.commands.impl.CommandHelp;
import net.polarizedions.polarizedbot.commands.impl.CommandIgnore;
import net.polarizedions.polarizedbot.commands.impl.CommandInvite;
import net.polarizedions.polarizedbot.commands.impl.CommandPing;
import net.polarizedions.polarizedbot.commands.impl.CommandSay;
import net.polarizedions.polarizedbot.commands.impl.CommandShutdown;
import net.polarizedions.polarizedbot.commands.impl.CommandWolframAlpha;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.exceptions.CommandException;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandManager {
    private Logger logger = LogManager.getLogger("CommandManager");
    private Map<String, CommandTree> commands = new HashMap<>();

    public CommandManager() {
        this.registerCommand(new CommandAbout());
        this.registerCommand(new CommandAnnoucer());
        this.registerCommand(new CommandGuild());
        this.registerCommand(new CommandHelp());
        this.registerCommand(new CommandIgnore());
        this.registerCommand(new CommandInvite());
        this.registerCommand(new CommandPing());
        this.registerCommand(new CommandShutdown());
        this.registerCommand(new CommandSay());
        this.registerCommand(new CommandWolframAlpha());
    }

    public void registerCommand(ICommand command) {
        CommandTree tree = command.getCommand();
        for (String alias : tree.getCommands()) {
            this.commands.put(alias, tree);
        }
    }

    public void registerListeners(IDiscordClient discordClient) {
        discordClient.getDispatcher().registerListener((IListener<MessageReceivedEvent>) this::messageHandler);
    }

    public void messageHandler(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();

        if (guild == null) {
            message.getChannel().sendMessage(Localizer.localize("error.pm_not_supported"));
            return;
        }

        logger.debug("[UserID: {}, GuildID: {}, MessageID: {}, User: {}]: {}", message.getAuthor().getStringID(), message.getGuild().getStringID(), message.getStringID(), message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator(), message.getContent());

        GuildConfig guildConfig = GuildManager.getConfig(guild);

        if (guildConfig.ignoredUsers.contains(user.getLongID())) {
            logger.debug("From ignored user");
            return;
        }

        if (!message.getContent().startsWith(guildConfig.commandPrefix)) {
            logger.debug("not command");
            return;
        }


        List<String> commandFragments = new ArrayList<>();
        Collections.addAll(commandFragments, message.getContent().split(" "));
        String command = commandFragments.get(0).substring(guildConfig.commandPrefix.length());
        commandFragments.set(0, command);

        CommandTree commandTree = this.commands.get(command);
        if (commandTree == null) {
            logger.debug("no command found");
            message.getChannel().sendMessage("no command found");
            return;
        }

        if (guildConfig.disabledCommands.contains(command)) {
            message.getChannel().sendMessage(Localizer.localize("error.command_disabled", commandTree.getName()));
            return;
        }

        Localizer.setCurrentLang(guildConfig.lang);
        logger.debug("Running command {}, alias {}, fragments: {}", commandTree.getName(), command, commandFragments);
        message.getChannel().setTypingStatus(true);
        try {
            commandTree.execute(commandFragments, message);
        }
        catch (CommandException ex) {
            logger.warn("Failed to handle command: threw {}", ex.getClass().getSimpleName());
            message.getChannel().sendMessage(ex.getError());
        }
        catch (MissingPermissionsException ex) {
            logger.error("No permission", ex.getMissingPermissions());
            try {
                message.getChannel().sendMessage(Localizer.localize("error.no_permission", ex.getMissingPermissions()));
            } catch(Exception e) {
                try {
                message.getAuthor().getOrCreatePMChannel().sendMessage(Localizer.localize("error.no_permission", ex.getErrorMessage()));}catch(Exception exc) {}
            }
        }
        catch (DiscordException ex) {
            logger.error("Discord exception!", ex);
            try {
                message.getChannel().sendMessage(Localizer.localize("error.discord_error", ex.getErrorMessage()));
            } catch(Exception e) {}
        }
        catch (Exception ex) {
            logger.error("I f'ed up", ex);
            try {
                message.getChannel().sendMessage(Localizer.localize("error.misc_error", ex.getClass().getCanonicalName() + ": " + ex.getMessage()));
            } catch(Exception e) {}
        }

        message.getChannel().setTypingStatus(false);
    }

    public Set<CommandTree> getCommands() {
        return new HashSet<>(this.commands.values());
    }

    public CommandTree get(String alias) {
        return this.commands.get(alias);
    }
}
