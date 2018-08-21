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

        System.out.println("Parsing tree: " + treeOptions + " " + parsedArgs);
        if (treeOptions.size() == 0) {
            System.out.println("  options size == 0");
            if (this.isExecutable()) {
                System.out.println("    is executable");
                if (this.swallows) {
                    System.out.println("      swallows");
                    if (this.allowEmpty) {
                        System.out.println("        allows empty");
                        System.out.println("# adding \"\" to parsed Args");
                        parsedArgs.add("");
                    }
                    else {
                        System.out.println("        doesn't allows empty");
                        System.out.println("# Failing command");
                        this.fail(command, parsedArgs, treeOptions);
                    }
                }
                else {
                    System.out.println("      doesnt swallow");
                    System.out.println("# Running excecutable");
                    this.run(command, parsedArgs);
                }
            }
            else {
                System.out.println("    isn't executable");
                System.out.println("# Failing command");
                this.fail(command, parsedArgs, treeOptions);
            }
            System.out.println("# Returning");
            return;
        }

        String head = treeOptions.remove(0);
        System.out.println("Head is: " + head);

        for (Map.Entry<CommandArg, Node> optionEntry : this.options.entrySet()) {
            CommandArg arg = optionEntry.getKey();
            Node node = optionEntry.getValue();
            Object match = arg.match(head);

            if (match != null) {
                System.out.println("  Found arg match! " + match);
                System.out.println("# Adding match to parsed args");
                parsedArgs.add(match);
                System.out.println("# executing tree further");
                node.executeTree(treeOptions, command, parsedArgs);
                System.out.println("# returning");
                return;
            }
        }

        System.out.println("Swallows?");
        if (this.swallows) {
            System.out.println("  YES!");
//            if (treeOptions.size() == 0 && !this.allowEmpty) {
//                System.out.println("    Tree size is now 0 && not allow empty");
//                System.out.println("# failing command");
//                this.fail(command);
//            }
//            else {
//                System.out.println("    tree size is > 0  OR this.allows empty");
                System.out.println("# adding head & joined options to parsed args: " + head + " " + String.join(" ", treeOptions));
                parsedArgs.add(head + " " + String.join(" ", treeOptions));
                System.out.println("# Running command");
                this.run(command, parsedArgs);
//            }
        }
        else{
            System.out.println("  NO!");
            System.out.println("# failing command");
            treeOptions.add(0, head);
            this.fail(command, parsedArgs, treeOptions);
        }

        System.out.println("# Reached end of method");
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
