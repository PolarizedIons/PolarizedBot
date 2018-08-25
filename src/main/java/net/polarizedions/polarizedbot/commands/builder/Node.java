package net.polarizedions.polarizedbot.commands.builder;

import net.polarizedions.polarizedbot.exceptions.CommandException;
import net.polarizedions.polarizedbot.exceptions.NeedPermission;
import net.polarizedions.polarizedbot.exceptions.UnknownFail;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.util.TriConsumer;
import sx.blah.discord.handle.obj.IMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Node {
    private Map<CommandArg, Node> options = new LinkedHashMap<>();
    private BiConsumer<IMessage, List<Object>> successConsumer;
    private TriConsumer<IMessage, List<Object>, List<String>> failConsumer;

    private CommandBuilder builder;
    private boolean swallows;
    private boolean allowEmpty;

    private UserRank rank;

    public Node(CommandBuilder builder) {
        this.builder = builder;
        this.rank = this.builder.command.rank;
    }

    public Node stringArg(String option, Consumer<Node> optionNode) {
        return this.addArgument(CommandArg.String(option), optionNode);
    }

    public Node pingArg(Consumer<Node> optionNode) {
        return this.addArgument(CommandArg.Ping(), optionNode);
    }

    public Node channelArg(Consumer<Node> optionNode) {
        return this.addArgument(CommandArg.Channel(), optionNode);
    }

    public Node captureArg(Consumer<Node> optionNode) {
        return this.addArgument(CommandArg.Any(), optionNode);
    }

    public Node optionArg(String[] options, Consumer<Node> optionNode) {
        return this.addArgument(CommandArg.Option(options), optionNode);
    }

    public Node addArgument(CommandArg arg, Consumer<Node> optionNode) {
        Node child = new Node(this.builder);
        this.options.put(arg, child);
        optionNode.accept(child);
        return this;
    }

    public Node onExecute(BiConsumer<IMessage, List<Object>> consumer) {
        this.successConsumer = consumer;
        return this;
    }

    public Node onFail(TriConsumer<IMessage, List<Object>, List<String>> failConsumer) {
        this.failConsumer = failConsumer;
        return this;
    }

    public Node swallow(boolean allowEmpty) {
        this.swallows = true;
        this.allowEmpty = allowEmpty;
        return this;
    }

    void executeTree(List<String> treeOptions, IMessage command, List<Object> parsedArgs) throws CommandException {
        if (! GuildManager.userHasRank(command, this.rank)) {
            throw new NeedPermission(this.rank);
        }


        if (treeOptions.size() == 0) {
            if (this.isExecutable()) {
                if (this.swallows) {
                    if (this.allowEmpty) {
                        parsedArgs.add("");
                    }
                    else {
                        this.fail(command, parsedArgs, treeOptions);
                    }
                }
                else {
                    this.run(command, parsedArgs);
                }
            }
            else {
                this.fail(command, parsedArgs, treeOptions);
            }

            return;
        }

        String head = treeOptions.remove(0);

        for (Map.Entry<CommandArg, Node> optionEntry : this.options.entrySet()) {
            CommandArg arg = optionEntry.getKey();
            Node node = optionEntry.getValue();
            Object match = arg.match(head);

            if (match != null) {
                parsedArgs.add(match);
                node.executeTree(treeOptions, command, parsedArgs);
                return;
            }
        }

        if (this.swallows) {
            this.run(command, parsedArgs);
        }
        else{
            treeOptions.add(0, head);
            this.fail(command, parsedArgs, treeOptions);
        }
    }

    public boolean isExecutable() {
        return this.successConsumer != null;
    }

    public Node rank(UserRank rank) {
        this.rank = rank;
        return this;
    }

    private void run(IMessage command, List<Object> parsedArgs) {
        if (this.successConsumer != null) {
            this.successConsumer.accept(command, parsedArgs);
        }
    }

    private void fail(IMessage command, List<Object> parsedArgs, List<String> unparsedArgs) throws UnknownFail {
        if (this.failConsumer != null) {
            this.failConsumer.accept(command, parsedArgs, unparsedArgs);
        }
        else {
            throw new UnknownFail();
        }
    }
}
