package net.polarizedions.polarizedbot.commands.builder;

import net.polarizedions.polarizedbot.util.UserRank;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommandBuilderTest {

    @Test
    void create() {
        String name = "Tester";

        CommandBuilder builder = CommandBuilder.create(name);
        assertNotNull(builder);
        CommandTree tree = builder.command;
        assertEquals(tree.name, name);
    }

    @Test
    void setHelp() {
        String helpText = "This is sample help text";
        CommandTree tree = CommandBuilder.create("test").setHelp(helpText).command;
        assertEquals(tree.getHelp(), helpText);
    }

    @Test
    void setRank() {
        UserRank rank = UserRank.GUILD_ADMIN;
        CommandTree tree = CommandBuilder.create("test").setRank(rank).command;
        assertEquals(tree.getRank(), rank);
    }

    @Test
    void command() {
        String command = "help";

        CommandTree tree = CommandBuilder.create("test")
                .command(command, (n) -> {
                })
                .buildCommand();

        assertIterableEquals(Arrays.asList(command), tree.getCommands());
    }

    @Test
    void command1() {
        String[] commands = new String[] { "help", "whatdo" };

        CommandTree tree = CommandBuilder.create("test")
                .command(commands[0], commands[1], (n) -> {
                })
                .buildCommand();

        assertIterableEquals(Arrays.asList(commands), tree.getCommands());

        Set<Node> nodes = new HashSet<>(tree.commands.values());
        assertEquals(1, nodes.size());
    }

    @Test
    void command2() {
        String[] commands = new String[] { "help", "whatdo", "wat", "morealiases" };

        CommandTree tree = CommandBuilder.create("test")
                .command(commands, (n) -> {
                })
                .command("test", (n) -> {
                })
                .buildCommand();

        List<String> expectedCommands = new ArrayList<>();
        Collections.addAll(expectedCommands, commands);
        expectedCommands.add("test");
        List<String> treeCommands = new ArrayList<>(tree.getCommands());
        Collections.sort(expectedCommands);
        Collections.sort(treeCommands);
        assertIterableEquals(expectedCommands, treeCommands);

        Set<Node> nodes = new HashSet<>(tree.commands.values());
        assertEquals(2, nodes.size());
    }

    @Test
    void buildCommand() {
        CommandTree tree = CommandBuilder.create("test").command("1", (n) -> {
        }).buildCommand();
        assertNotNull(tree);
    }
}