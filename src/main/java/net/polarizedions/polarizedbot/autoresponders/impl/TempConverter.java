package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.autoresponders.IResponder;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import sx.blah.discord.handle.obj.IMessage;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TempConverter implements IResponder {
    private static final Pattern TEMPERATURE_REGEX = Pattern.compile("(?<=^|\\s)(-?[0-9]+(?:.[0-9]+)?)\\s?°?([CF])(?![a-z])", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private static final DecimalFormat FORMATER = new DecimalFormat("#.##");

    private static final double MIN_C = -273.15;
    private static final double MIN_F = -459.67;
    private static final Function<Double, Double> C_TO_F = c -> c * 1.8  + 32;
    private static final Function<Double, Double> F_TO_C = f -> ( f - 32 ) / 1.8;

    @Override
    public String getID() {
        return "temp";
    }

    @Override
    public List<String> getPrefixWhitelist() {
        return Collections.singletonList("-");
    }

    @Override
    public void run(IMessage message) {
        Localizer loc = new Localizer(message);
        Matcher m = TEMPERATURE_REGEX.matcher(message.getContent());
        boolean match = false;
        StringBuilder builder = new StringBuilder("```\n");

        while (m.find()) {
            match = true;
            double temp = Double.parseDouble(m.group(1));
            String unit = m.group(2).toUpperCase();
            String otherUnit = unit.equals("C") ? "F" : "C";

            Function<Double, Double> converter = unit.equals("C") ? C_TO_F : F_TO_C;
            double min = unit.equals("C") ? MIN_C : MIN_F;
            double result = converter.apply(temp);

            builder.append(FORMATER.format(temp)).append(" °").append(loc.localize("autoresponder.temperature." + unit)).append(" -> ");
            if (result < min) {
                builder.append(loc.localize("autoresponder.temperature.impossible")).append("\n");
            }
            else {
                builder.append(FORMATER.format(result)).append(" °").append(loc.localize("autoresponder.temperature." + otherUnit)).append("\n");
            }
        }

        if (match) {
            MessageUtil.sendAutosplit(message.getChannel(), builder.append("```").toString(), "```", "```");
        }
    }
}
