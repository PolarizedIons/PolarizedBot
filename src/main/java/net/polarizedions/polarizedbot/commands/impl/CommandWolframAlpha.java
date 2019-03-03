package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Message;
import net.polarizedions.polarizedbot.api_handlers.WolframAlphaApi;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.exceptions.ApiException;
import net.polarizedions.polarizedbot.util.MessageUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

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

    private void fail(Message message, @NotNull ParsedArguments parsedArgs, List<String> unparsedArgs) {
        if (parsedArgs.size() == 1) {
            MessageUtil.reply(message, "command.wolfram.error.no_arg");
        }
        else {
            MessageUtil.reply(message, "command.wolfram.error.unknown");
        }
    }

    @Nullable
    private WolframAlphaApi.WolframAlphaReply get(Message message, @NotNull ParsedArguments args) {
        WolframAlphaApi.WolframAlphaReply data;
        try {
            data = WolframAlphaApi.fetch(args.getAsString(1));
        }
        catch (ApiException ex) {
            MessageUtil.reply(message, "command.wolfram.error." + ex.getError(), ex.getErrorContext());
            return null;
        }

        if (! data.error.isEmpty()) {
            if (data.error.equals("no_data") && data.didYouMeans.size() > 0) {
                StringBuilder resp = new StringBuilder("\n");
                DecimalFormat format = new DecimalFormat("#.##");
                for (WolframAlphaApi.DidYouMean dym : data.didYouMeans) {
                    resp.append(" - ").append(dym.value).append(" (**").append(format.format(dym.chance)).append("%**)\n");
                }

                MessageUtil.reply(message, "command.wolfram.didyoumean", resp.toString());
                return null;
            }

            MessageUtil.reply(message, "command.wolfram.error." + data.error);
            return null;
        }

        return data;
    }

    private void replyFull(Message message, ParsedArguments args) {
        WolframAlphaApi.@Nullable WolframAlphaReply reply = this.get(message, args);
        if (reply == null) {
            return;
        }

        // < 2, because one of those is the input interpretation
        if (reply.pods.size() < 2) {
            MessageUtil.reply(message, "command.wolfram.no_data");
            return;
        }

        this.reply(message, reply, reply.pods.size());
    }


    private void replyShort(Message message, ParsedArguments args) {
        WolframAlphaApi.@Nullable WolframAlphaReply reply = this.get(message, args);
        if (reply == null) {
            return;
        }

        // < 2, because one of those is the input interpretation
        if (reply.pods.size() < 2) {
            MessageUtil.reply(message, "command.wolfram.error.no_data");
            return;
        }

        this.reply(message, reply, 2);
    }

    private void reply(Message message, WolframAlphaApi.WolframAlphaReply wolfData, int count) {
        StringBuilder responseBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            WolframAlphaApi.Pod pod = wolfData.pods.get(i);

            responseBuilder.append("**").append(pod.name).append("**: ");

            if (pod.data.size() == 1 && !pod.data.get(0).contains("\n")) {
                responseBuilder.append(this.escapeData(pod.data.get(0)));
            }
            else {
                String prefix = "    ";

                for (String data : pod.data) {
                    String[] splitLines = data.split("\n");
                    responseBuilder.append("\n").append(prefix).append("- ").append(this.escapeData(splitLines[0]));
                    for (int j = 1; j < splitLines.length; j++) {
                        responseBuilder.append("\n").append(prefix).append("  ").append(this.escapeData(splitLines[j]));
                    }
                }
            }

            responseBuilder.append("\n");
        }

        MessageUtil.sendAutosplit(message, responseBuilder.toString());
    }

    @NotNull
    @Contract(pure = true)
    private String escapeData(@NotNull String text) {
        return text.replaceAll("([*_])", "\\\\$1");
    }
}
