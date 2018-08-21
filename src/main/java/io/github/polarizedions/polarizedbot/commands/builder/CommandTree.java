package io.github.polarizedions.polarizedbot.commands.builder;

import io.github.polarizedions.polarizedbot.exceptions.CommandException;
import io.github.polarizedions.polarizedbot.exceptions.NoSuchCommand;
import io.github.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IMessage;

import java.util.*;

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

    public void execute(List<String> fragments, IMessage msg) throws CommandException {
        String head = fragments.remove(0);
        Node alias = this.commands.get(head);
        if (alias == null) {
            throw new NoSuchCommand();
        }

        LinkedList<Object> parsed = new LinkedList<>();
        parsed.add(head);
        alias.executeTree(fragments, msg, parsed);
    }

    public Set<String> getCommands() {
        return this.commands.keySet();
    }
}
