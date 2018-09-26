package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.api_handlers.WolframAlphaApi;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.MessageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;
import java.util.Map;

public class CommandWolframAlpha implements ICommand {

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("WolframAlpha")
                .command("wolf", wolf -> wolf
                        .swallow(false)
                        .onExecute(this::replyFull)
                        .onFail(this::fail)
                        .setHelp("command.wolfram.help.wolf")
                )
                .command("calc", calc -> calc
                        .swallow(false)
                        .onExecute(this::replyShort)
                        .onFail(this::fail)
                        .setHelp("command.wolfram.help.calc")
                )
                .setHelp("command.wolfram.help")
                .buildCommand();
    }

    private void fail(IMessage message, @NotNull ParsedArguments parsedArgs, List<String> unparsedArgs) {
        if (parsedArgs.size() == 1) {
            MessageUtil.reply(message, "command.wolfram.error.no_arg");
        }
        else {
            MessageUtil.reply(message, "command.wolfram.error.unknown");
        }
    }

    @Nullable
    private Map<String, List<String>> get(IMessage message, ParsedArguments args) {
        if (!WolframAlphaApi.hasApiKey()) {
            MessageUtil.reply(message, "command.wolfram.error.no_api_key");
            return null;
        }

        Map<String, List<String>> data = WolframAlphaApi.fetch(args.getAsString(1));
        if (data == null) {
            MessageUtil.reply(message, "command.wolfram.error.connection");
            return null;
        }

        if (data.size() == 0) {
            MessageUtil.reply(message, "command.wolfram.error.no_reply");
            return null;
        }

        return data;
    }

    private void replyFull(IMessage message, ParsedArguments args) {
        Map<String, List<String>> data = this.get(message, args);
        if (data == null) {
            return;
        }

        StringBuilder responseBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String podName = entry.getKey();
            List<String> values = entry.getValue();
            if (values.size() == 0) {
                continue;
            }

            responseBuilder.append("**").append(podName).append("**");

            if (values.size() == 1) {
                responseBuilder.append(": ").append(values.get(0).replaceAll("\\*", "\\\\*"));
            }
            else {
                for (String value : values) {
                    responseBuilder.append("\n  -> ").append(value.replaceAll("\\*", "\\\\*"));
                }
            }
            responseBuilder.append("\n");
        }

        MessageUtil.sendAutosplit(message.getChannel(), responseBuilder.toString());
    }


    private void replyShort(IMessage message, ParsedArguments args) {
        Map<String, List<String>> data = this.get(message, args);
        if (data == null) {
            return;
        }
        int i = 0;

        StringBuilder responseBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String podName = entry.getKey();
            List<String> values = entry.getValue();
            if (values.size() == 0) {
                continue;
            }

            if (i >= 2) {
                break;
            }

            responseBuilder.append("**").append(podName).append("**");
            if (values.size() == 1) {
                responseBuilder.append(": ").append(values.get(0).replaceAll("\\*", "\\\\*"));
            }
            else {
                for (String value : values) {
                    responseBuilder.append("\n  -> ").append(value.replaceAll("\\*", "\\\\*"));
                }
            }
            responseBuilder.append("\n");
            i++;
        }

        MessageUtil.sendAutosplit(message.getChannel(), responseBuilder.toString());
    }
}
