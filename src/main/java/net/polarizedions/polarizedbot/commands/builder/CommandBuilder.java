package net.polarizedions.polarizedbot.commands.builder;

import net.polarizedions.polarizedbot.util.UserRank;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandBuilder {
    CommandTree command;

    private CommandBuilder(String name) {
        this.command = new CommandTree();
        this.command.name = name;
    }

    @NotNull
    @Contract("_ -> new")
    public static CommandBuilder create(String commandName) {
        return new CommandBuilder(commandName);
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
        return command(new String[] {command}, consumer);
    }

    public CommandBuilder command(String command, String alias, Consumer<Node> consumer) {
        return command(new String[] {command, alias}, consumer);
    }

    public CommandBuilder command(String[] aliases, Consumer<Node> consumer) {
        Node node = new Node(this);
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
