package net.polarizedions.polarizedbot.commands;

import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.impl.*;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.exceptions.CommandExceptions;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
        this.registerCommand(new CommandUpdate());
        this.registerCommand(new CommandPing());
        this.registerCommand(new CommandShutdown());
        this.registerCommand(new CommandSay());
        this.registerCommand(new CommandWolframAlpha());
    }

    private void registerCommand(@NotNull ICommand command) {
        CommandTree tree = command.getCommand();
        for (String alias : tree.getCommands()) {
            this.commands.put(alias, tree);
        }
    }

    public void messageHandler(IMessage message) {
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();


        if (guild == null) {
            MessageUtil.reply(message, "error.pm_not_supported");
            return;
        }

        if (user.isBot()) {
            return;
        }

        GuildConfig guildConfig = GuildManager.getConfig(guild);
        if (guildConfig.ignoredUsers.contains(user.getLongID())) {
            logger.debug("From ignored user");
            return;
        }

        if (!message.getContent().startsWith(guildConfig.commandPrefix)) {
            return;
        }


        List<String> commandFragments = new ArrayList<>();
        Collections.addAll(commandFragments, message.getContent().split(" "));
        String command = commandFragments.get(0).substring(guildConfig.commandPrefix.length());
        commandFragments.set(0, command);

        if (command.isEmpty()) {
            return;
        }

        CommandTree commandTree = this.commands.get(command);
        if (commandTree == null) {
            MessageUtil.reply(message,"error.no_such_command", command);
            return;
        }

        if (guildConfig.disabledCommands.contains(command)) {
            MessageUtil.reply(message,"error.command_disabled", commandTree.getName());
            return;
        }

        CompletableFuture.runAsync(() -> {
            logger.debug("Running command {}, alias {}, fragments: {}", commandTree.getName(), command, commandFragments);

            try {
                commandTree.execute(commandFragments, message);
            }
            catch (Exception ex) {
                this.handleCommandException(message, ex);
            }
        });
    }

    private void handleCommandException(IMessage message, Exception ex) {
        if (ex instanceof CommandExceptions) {
            logger.warn("Failed to handle command: threw {}", ex.getClass().getSimpleName());
            MessageUtil.reply(message, ((CommandExceptions) ex).getError(), ((CommandExceptions) ex).getErrorContext());
        }
        else if (ex instanceof MissingPermissionsException) {
            String neededPerms = ((MissingPermissionsException) ex).getMissingPermissions().toString();
            logger.error("No permission", neededPerms);

            String localized = new Localizer(message).localize("error.no_permission", neededPerms);
            try {
                message.getChannel().sendMessage(localized);
            } catch(Exception e) {
                try {
                    message.getAuthor().getOrCreatePMChannel().sendMessage(localized);
                }
                catch(Exception exc) { /* NOOP */ }
            }
        }
        else if (ex instanceof DiscordException) {
            logger.error("Discord exception!", ex);
            try {
                MessageUtil.reply(message,"error.discord_error", ((DiscordException) ex).getErrorMessage());
            } catch(Exception e) { /* NOOP */ }
        }
        else {
            logger.error("I f'ed up", ex);
            try {
                MessageUtil.reply(message,"error.misc_error", ex.getClass().getCanonicalName() + ": " + ex.getMessage());
            } catch(Exception e) { /* NOOP */ }
        }

    }

    public Set<CommandTree> getCommands() {
        return new HashSet<>(this.commands.values());
    }

    public CommandTree get(String alias) {
        return this.commands.get(alias);
    }
}
