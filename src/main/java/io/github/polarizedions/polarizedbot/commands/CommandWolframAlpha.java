package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.api_handlers.WolframAlphaApi;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandWolframAlpha implements ICommand {
    @Override
    public String[] getCommand() {
        return new String[] {"wolf", "calc"};
    }

    @Override
    public String getHelp() {
        return "wolfram alpha stuff";
    }

    @Override
    public void exec(CommandMessage command) {
        if (!WolframAlphaApi.hasApiKey()) {
            command.replyLocalized("command.wolfram.error.no_api_key");
            return;
        }

        Map<String, List<String>> data = WolframAlphaApi.fetch(String.join(" ", command.getArgs()));
        if (data == null) {
            command.replyLocalized("command.wolfram.error.connection");
            return;
        }

        if (data.size() == 0) {
            command.replyLocalized("command.wolfram.error.no_reply");
            return;
        }

        if (command.getCommand().equals("wolf")) {
            replyFull(command, data);
        }
        else if (command.getCommand().equals("calc")) {
            replyShort(command, data);
        }
    }

    private void replyFull(CommandMessage command, Map<String, List<String>> data) {
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
            String currentMsg = "";
            for (String part : responseParts) {
                if ((currentMsg.length() + part.length()) <= 2000) {
                    currentMsg += part;
                }
                else {
                    messages.add(currentMsg);
                    currentMsg = part;
                }
            }

            if (currentMsg.length() > 0) {
                messages.add(currentMsg);
            }
        }

        for (String msg : messages) {
            command.getChannel().sendMessage(msg);
        }
    }


    private void replyShort(CommandMessage command, Map<String, List<String>> data) {
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

        command.getChannel().sendMessage(responseBuilder.toString());
    }
}
