package net.polarizedions.polarizedbot.commands.builder;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.util.UserRank;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandBuilder {
    CommandTree command;
    Bot bot;

    private CommandBuilder(Bot bot, String name) {
        this.bot = bot;
        this.command = new CommandTree();
        this.command.name = name;
    }

    @NotNull
    @Contract("_ -> new")
    public static CommandBuilder create(Bot bot, String commandName) {
        return new CommandBuilder(bot, commandName);
    }

    public CommandBuilder setHelp(String helpKey) {
        this.command.help = helpKey;
        return this;
    }

    public CommandBuilder setRank(UserRank rank) {
        this.command.rank = rank;
        return this;
    }

    public CommandBuilder command(String command, Consumer<Node> consumer) {
        return command(new String[] { command }, consumer);
    }

    public CommandBuilder command(String command, String alias, Consumer<Node> consumer) {
        return command(new String[] { command, alias }, consumer);
    }

    public CommandBuilder command(String[] aliases, Consumer<Node> consumer) {
        Node node = new Node(this, null);
        for (String alias : aliases) {
            this.command.commands.put(alias, node);
        }

        consumer.accept(node);
        return this;
    }

    public CommandTree buildCommand() {
        return this.command;
    }
}
