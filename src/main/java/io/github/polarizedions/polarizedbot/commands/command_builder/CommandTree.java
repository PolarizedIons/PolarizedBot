package io.github.polarizedions.polarizedbot.commands.command_builder;

import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;

import java.util.List;

public class CommandTree {
    String name;
    String help;
    Node rootNode;

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }


    public boolean canExecute(List<String> options) {
        return this.rootNode.canTreeExecute(options);
    }

    public boolean execute(List<String> option, CommandMessage command) {
        System.out.println("Executing options: " + String.join(" ", option));
        return this.rootNode.treeExecute(option, command);
    }
}
