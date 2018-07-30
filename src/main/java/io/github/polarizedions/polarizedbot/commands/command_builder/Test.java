package io.github.polarizedions.polarizedbot.commands.command_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        CommandTree tree = CommandBuilder
                .create("ping command")
                .setHelp("Ping pong")
                .start()
                    .addOptionString("ping")
                        .execute(command -> System.out.println("ping!!"))
                        .done()
                    .addOptionString("pong")
                        .addOptionPing()
                            .execute(command -> System.out.println("pong mewo!!!!"))
                            .done()
                        .execute(command -> System.out.println("pong no mewo"))
                        .done()
                .buildCommand();

        printTree(tree);
        System.out.println("===========");
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("ping", "yadda", "wtg")), null));
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("pong", "yadda", "wtg")), null));
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("pong", "sg")), null));
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("ping")), null));
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("pong")), null));
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("pong", "meow")), null));
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("pong", "<@548541564854>")), null));
        System.out.println(" = " + tree.execute(new ArrayList<>(Arrays.asList("pong", "meow", "abra cadabra")), null));
    }

    private static void printTree(CommandTree tree) {
        System.out.println("Command Tree:");
        System.out.println(" Name: " + tree.name);
        System.out.println(" Help: " + tree.help);
        System.out.println(" Tree: ");

        printTree(tree.rootNode, "  ");
    }

    private static void printTree(Node node, String prefix) {
        for (Map.Entry<Option, Node> option : node.options.entrySet()) {
            printTree(option.getValue(), prefix + " -> " + option.getKey());
        }

        System.out.println(prefix + " :: " + node.isExecutable());
    }
}
