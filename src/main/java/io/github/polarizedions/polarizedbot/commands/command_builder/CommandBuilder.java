package io.github.polarizedions.polarizedbot.commands.command_builder;

public class CommandBuilder {
    private CommandTree command;

    private CommandBuilder(String name) {
        this.command = new CommandTree();
        this.command.name = name;
    }

    public static CommandBuilder create(String commandName) {
        return new CommandBuilder(commandName);
    }

    public CommandBuilder setHelp(String helpText) {
        this.command.help = helpText;
        return this;
    }

    public Node start() {
        this.command.rootNode = new Node(this, null);
        return this.command.rootNode;
    }

    public CommandTree build() {
        return this.command;
    }

}
