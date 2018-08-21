package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.api_handlers.WolframAlphaApi;
import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import io.github.polarizedions.polarizedbot.util.Localizer;
import sx.blah.discord.handle.obj.IMessage;

import java.util.LinkedList;
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
                )
                .command("calc", calc -> calc
                    .swallow(false)
                    .onExecute(this::replyShort)
                    .onFail(this::fail)
                )
                .buildCommand();
    }

    private void fail(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        if (parsedArgs.size() == 1) {
            message.getChannel().sendMessage(Localizer.localize("command.wolfram.error.no_arg"));
        }
        else {
            message.getChannel().sendMessage(Localizer.localize("command.wolfram.error.unknown"));
        }
    }

    private Map<String, List<String>> get(IMessage message, List<Object> args) {
        if (!WolframAlphaApi.hasApiKey()) {
            message.getChannel().sendMessage(Localizer.localize("command.wolfram.error.no_api_key"));
            return null;
        }

        Map<String, List<String>> data = WolframAlphaApi.fetch((String) args.get(1));
        if (data == null) {
            message.getChannel().sendMessage(Localizer.localize("command.wolfram.error.connection"));
            return null;
        }

        if (data.size() == 0) {
            message.getChannel().sendMessage(Localizer.localize("command.wolfram.error.no_reply"));
            return null;
        }

        return data;
    }

    private void replyFull(IMessage message, List<Object> args) {
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

        String response = responseBuilder.toString();

        List<String> messages = new LinkedList<>();
        if (response.length() <= 2000) { // Discord message char limit
            messages.add(response);
        }
        else {
            String[] responseParts = response.split("(?=\n\\*\\*)");
            StringBuilder currentMsg = new StringBuilder();
            for (String part : responseParts) {
                if ((currentMsg.length() + part.length()) <= 2000) {
                    currentMsg.append(part);
                }
                else {
                    messages.add(currentMsg.toString());
                    currentMsg = new StringBuilder(part);
                }
            }

            if (currentMsg.length() > 0) {
                messages.add(currentMsg.toString());
            }
        }

        for (String msg : messages) {
            message.getChannel().sendMessage(msg);
        }
    }


    private void replyShort(IMessage message, List<Object> args) {
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
            } else {
                for (String value : values) {
                    responseBuilder.append("\n  -> ").append(value.replaceAll("\\*", "\\\\*"));
                }
            }
            responseBuilder.append("\n");
            i++;
        }

        message.getChannel().sendMessage(responseBuilder.toString());
    }
}
