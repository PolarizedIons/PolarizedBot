package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.api_handlers.WolframAlphaApi;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;

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

        StringBuilder response = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String podName = entry.getKey();
            List<String> values = entry.getValue();
            if (values.size() == 0) {
                continue;
            }

            response.append("**").append(podName).append("**");

            if (values.size() == 1) {
                response.append(": ").append(values.get(0).replaceAll("\\*", "\\\\*"));
            }
            else {
                for (String value : values) {
                    response.append("\n  -> ").append(value.replaceAll("\\*", "\\\\*"));
                }
            }
            response.append("\n");
        }

        command.getChannel().sendMessage(response.toString());
    }
}
