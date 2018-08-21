//package io.github.polarizedions.polarizedbot.commands;
//
//import Bot;
//import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
//
//public class CommandHelp implements ICommand {
//    @Override
//    public String[] getCommand() {
//        return new String[] {"help"};
//    }
//
//    @Override
//    public String getHelp() {
//        return "get help";
//    }
//
//    @Override
//    public void run(CommandMessage msg) {
//        CommandManager manager = Bot.instance.getCommandManager();
//        StringBuilder responseBuilder = new StringBuilder();
//        for (ICommand command : manager.getCommands()) {
//            if (command.getRequiredRank().rank > msg.getUserRank().rank)
//            {
//                continue;
//            }
//
//            responseBuilder.append("- **").append(command.getCommand()[0]).append("**");
//            if (command.getCommand().length > 1) {
//                responseBuilder.append(" (");
//                for (int i = 1; i < command.getCommand().length; i++) {
//                    if (i != 1) {
//                        responseBuilder.append(", ");
//                    }
//                    responseBuilder.append("**").append(command.getCommand()[i]).append("**");
//                }
//                responseBuilder.append(")");
//            }
//
//            responseBuilder.append(": *").append(command.getHelp()).append("*\n");
//        }
//
//        msg.getChannel().sendMessage(responseBuilder.toString());
//    }
//
//}
