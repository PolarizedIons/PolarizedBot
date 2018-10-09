package net.polarizedions.polarizedbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.polarizedions.polarizedbot.commands.impl.CommandGuild;
import net.polarizedions.polarizedbot.commands.impl.CommandPing;
import net.polarizedions.polarizedbot.commands.impl.CommandSay;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.MessageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import net.polarizedions.polarizedbot.commands.impl.CommandAbout;
//import net.polarizedions.polarizedbot.commands.impl.CommandAnnoucer;
//import net.polarizedions.polarizedbot.commands.impl.CommandHelp;
//import net.polarizedions.polarizedbot.commands.impl.CommandIgnore;
//import net.polarizedions.polarizedbot.commands.impl.CommandInvite;
//import net.polarizedions.polarizedbot.commands.impl.CommandShutdown;
//import net.polarizedions.polarizedbot.commands.impl.CommandUpdate;
//import net.polarizedions.polarizedbot.commands.impl.CommandWolframAlpha;
//import net.polarizedions.polarizedbot.config.GuildConfig;

public class CommandManager {
    private Logger logger = LogManager.getLogger("CommandManager");
    private List<ICommand> commands;
    private CommandDispatcher<CommandSource> commandDispatcher;

    public CommandManager() {
        this.commands = new ArrayList<>();
        this.commandDispatcher = new CommandDispatcher<>();

//        this.registerCommand(new CommandAbout());
//        this.registerCommand(new CommandAnnoucer());
        this.registerCommand(new CommandGuild());
//        this.registerCommand(new CommandHelp());
//        this.registerCommand(new CommandIgnore());
//        this.registerCommand(new CommandInvite());
//        this.registerCommand(new CommandUpdate());
        this.registerCommand(new CommandPing());
//        this.registerCommand(new CommandShutdown());
        this.registerCommand(new CommandSay());
//        this.registerCommand(new CommandWolframAlpha());
    }

    private void registerCommand(@NotNull ICommand command) {
        this.commands.add(command);
        command.registerCommand(this.commandDispatcher);
    }

    public void messageHandler(IMessage message) {
        CommandSource commandSource = new CommandSource(message);


        if (commandSource.guild == null) {
            MessageUtil.reply(message, "error.pm_not_supported");
            return;
        }

        if (commandSource.user.isBot()) {
            return;
        }

        GuildConfig guildConfig = commandSource.getGuildConfig();
        if (guildConfig.ignoredUsers.contains(commandSource.user.getLongID())) {
            logger.debug("From ignored user");
            return;
        }

        if (!message.getContent().startsWith(guildConfig.commandPrefix)) {
            return;
        }

        String commandString = message.getContent().substring(guildConfig.commandPrefix.length());

        ParseResults<CommandSource> parseResults = this.commandDispatcher.parse(commandString, commandSource);
        System.out.println(parseResults.getContext().getNodes());
        if (parseResults.getExceptions().size() > 0) {
            System.out.println("exceptions");
            for (Map.Entry<CommandNode<CommandSource>, CommandSyntaxException> thing : parseResults.getExceptions().entrySet()) {
                System.out.println(thing.getKey() + " - " + thing.getValue());
            }
        }
        try {
//            commands.get(1).help(commandDispatcher, commandSource);
            commandDispatcher.execute(parseResults);
        }
        catch (CommandSyntaxException e) {
            System.out.println("syntax ex ");
            System.out.println(e.toString());
//            e.printStackTrace();

            if (e.getCursor() == 0) {
                message.getChannel().sendMessage("no command error");
            }
            else {
                for (Map.Entry<CommandNode<CommandSource>, StringRange> thing : parseResults.getContext().getLastChild().getNodes().entrySet()) {
                    message.getChannel().sendMessage("usage: "  + thing.getValue() + " " + commandDispatcher.getSmartUsage(thing.getKey(), commandSource) + " -- " + Arrays.toString(commandDispatcher.getAllUsage(thing.getKey(), commandSource, true)));
                }
            }
        }

//
//        List<String> commandFragments = new ArrayList<>();
//        Collections.addAll(commandFragments, message.getContent().split(" "));
//        String command = commandFragments.get(0).substring(guildConfig.commandPrefix.length());
//        commandFragments.set(0, command);
//
//        if (command.isEmpty()) {
//            return;
//        }
//
//        CommandTree commandTree = this.commands.get(command);
//        if (commandTree == null) {
//            return;
//        }
//
//        if (guildConfig.disabledCommands.contains(command)) {
//            MessageUtil.reply(message, "error.command_disabled", commandTree.getName());
//            return;
//        }
//
//        CompletableFuture.runAsync(() -> {
//            logger.debug("Running command {}, alias {}, fragments: {}", commandTree.getName(), command, commandFragments);
//
//            try {
//                commandTree.execute(commandFragments, message);
//            }
//            catch (Exception ex) {
//                this.handleCommandException(message, ex);
//            }
//        });
    }

//    private void handleCommandException(IMessage message, Exception ex) {
//        if (ex instanceof CommandExceptions) {
//            logger.warn("Failed to handle command: threw {}", ex.getClass().getSimpleName());
//            MessageUtil.reply(message, ( (CommandExceptions)ex ).getError(), ( (CommandExceptions)ex ).getErrorContext());
//        }
//        else if (ex instanceof MissingPermissionsException) {
//            String neededPerms = ( (MissingPermissionsException)ex ).getMissingPermissions().toString();
//            logger.error("No permission", neededPerms);
//
//            String localized = new Localizer(message).localize("error.no_permission", neededPerms);
//            try {
//                message.getChannel().sendMessage(localized);
//            }
//            catch (Exception e) {
//                try {
//                    message.getAuthor().getOrCreatePMChannel().sendMessage(localized);
//                }
//                catch (Exception exc) { /* NOOP */ }
//            }
//        }
//        else if (ex instanceof DiscordException) {
//            logger.error("Discord exception!", ex);
//            try {
//                MessageUtil.reply(message, "error.discord_error", ( (DiscordException)ex ).getErrorMessage());
//            }
//            catch (Exception e) { /* NOOP */ }
//        }
//        else {
//            logger.error("I f'ed up", ex);
//            try {
//                MessageUtil.reply(message, "error.misc_error", ex.getClass().getCanonicalName() + ": " + ex.getMessage());
//            }
//            catch (Exception e) { /* NOOP */ }
//        }
//
//    }

//    public Set<CommandTree> getCommands() {
//        return new HashSet<>(this.commands.values());
//    }
//
//    public CommandTree get(String alias) {
//        return this.commands.get(alias);
//    }
}
