package io.github.polarizedions.polarizedbot.commands.command_builder;

import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Node {
    Map<Option, Node> options = new LinkedHashMap<>();
    private Consumer<CommandMessage> consumer;

    private CommandBuilder builder;
    private Node parentNode;

    public Node(CommandBuilder builder, Node parent) {
        this.builder = builder;
        this.parentNode = parent;
    }

    public Node addOptionString(String option) {
        return addOption(Option.String(option));
    }

    public Node addOptionPing() {
        return addOption(Option.Ping());
    }

    public Node addOption(Option option) {
        Node child = new Node(this.builder, this);
        this.options.put(option, child);
        return child;
    }

    public Node execute(Consumer<CommandMessage> consumer) {
        this.consumer = consumer;
        return this;
    }



    public boolean canTreeExecute(List<String> treeOptions) {
        if (treeOptions.size() == 0) {
            return this.isExecutable();
        }

        String head = treeOptions.remove(0);

        for (Map.Entry<Option, Node> entry : this.options.entrySet()) {
            if (entry.getKey().match(head)) {
                return entry.getValue().canTreeExecute(treeOptions);
            }
        }

        return false;
    }

    public boolean treeExecute(List<String> treeOptions, CommandMessage command) {
        String head = treeOptions.remove(0);

        for (Map.Entry<Option, Node> entry : this.options.entrySet()) {
            Option option = entry.getKey();
            Node optionNode = entry.getValue();
            if (option.match(head)) {
                if (treeOptions.size() == 0) {
                    optionNode.exec(command);
                    return true;
                }

                return optionNode.treeExecute(treeOptions, command);
            }
        }

        return false;


    }

    public boolean isExecutable() {
        return this.consumer != null;
    }

    public void exec(CommandMessage command) {
        this.consumer.accept(command);
    }



    public Node done() {
        return parentNode;
    }

    public CommandTree buildCommand() {
        return this.builder.build();
    }


}
