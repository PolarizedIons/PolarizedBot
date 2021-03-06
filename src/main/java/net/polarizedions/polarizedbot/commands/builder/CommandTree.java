package net.polarizedions.polarizedbot.commands.builder;

import net.polarizedions.polarizedbot.exceptions.CommandExceptions;
import net.polarizedions.polarizedbot.exceptions.NoSuchCommand;
import net.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandTree {
    String name;
    String help;
    UserRank rank = UserRank.DEFAULT;
    Map<String, Node> commands = new HashMap<>();

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public UserRank getRank() {
        return rank;
    }

    public void execute(List<String> fragments, IMessage msg) throws CommandExceptions {
        String head = fragments.remove(0);
        Node alias = this.commands.get(head);
        if (alias == null) {
            throw new NoSuchCommand(head);
        }

        ParsedArguments parsed = new ParsedArguments();
        parsed.add(head);
        alias.executeTree(fragments, msg, parsed);
    }

    public Set<String> getCommands() {
        return this.commands.keySet();
    }

    public String getHelpFor(String alias) {
        Node aliasNode = this.commands.get(alias);
        return aliasNode == null ? "" : aliasNode.getHelp();
    }
}
