package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.autoresponders.IResponder;
import net.polarizedions.polarizedbot.util.MessageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.obj.IMessage;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoUnitConverter implements IResponder {
    private static final Logger logger = LogManager.getLogger("AutoUnitConverter");
    private Map<String, List<Conversion>> conversions;
    private Pattern matchPattern;
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    @Override
    public String getID() {
        return "units";
    }

    public AutoUnitConverter() {
        conversions = new HashMap<>();

        // INIT UNITS

        // LENGTH
        new Conversion("cm", "in", cm -> cm / 2.54)
                .alias("cm", "\"")
                .alias("centimeter", "inch")
                .alias("centimeters", "inches").register()
                .reverse(in -> in * 2.54).register();

        new Conversion("mi", "km", mi -> mi * 1.609344)
                .alias("mile", "kilometer")
                .alias("miles", "kilometers").register()
                .reverse(km -> km / 1.609344).register();

        new Conversion("ft", "m", ft -> ft * 0.3048)
                .alias("'", "m")
                .alias("foot", "meter")
                .alias("feet", "meters").register()
                .reverse(m -> m / 0.3048).register();

        // TEMPERATURE

        new Conversion("°F", "°C", f -> (f - 32) / 1.8)
                .alias("F", "C")
                .alias("fahrenheit", "celsius").register()
                .reverse(c -> c * 1.8 + 32).register();

        new Conversion("K", "°C", k -> k + 273.15)
                .alias("K", "C").register()
                .alias("kelvin", "celsius").register()
                .reverse(c -> c - 273.15).register();

        new Conversion("K", "°F", k -> k * 9.0/5.0 - 459.67)
                .alias("K", "F").register()
                .alias("kelvin", "fahrenheit").register()
                .reverse(f -> (f + 459.67) * 5.0 / 9.0).register();

        // DONE WITH UNIT INIT

        String matchRegexString = "(?<![a-zA-Z])((?:[0-9]+)(?:\\.(?:[0-9]+))?) ?(%s)(?![a-zA-Z])";
        this.matchPattern = Pattern.compile(String.format(matchRegexString, String.join("|", this.conversions.keySet())), Pattern.MULTILINE);
    }

    @Override
    public void run(IMessage message) {
        Matcher matcher = matchPattern.matcher(message.getContent().toLowerCase());
        StringBuilder response = new StringBuilder("```\n");
        boolean matched = false;

        while (matcher.find()) {
            matched = true;
            String matchingUnit = matcher.group(2);
            logger.debug("Found unit '{}' to convert", matchingUnit);
            double in = Double.parseDouble(matcher.group(1));
            for (Conversion converter : this.conversions.get(matchingUnit)) {
                response.append(in).append(" ").append(converter.from.get(0)).append(" -> ")
                        .append(FORMAT.format(converter.run(in))).append(" ").append(converter.to.get(0)).append("\n");
            }
        }

        if (matched) {
            MessageUtil.sendAutosplit(message.getChannel(), response.append("```").toString(), "```", "```");
        }
    }

    private class Conversion {
        private List<String> from;
        private List<String> to;
        private Function<Double, Double> convert;

        Conversion(List<String> from, List<String> to, Function<Double, Double> convert) {
            this.from = from;
            this.to = to;
            this.convert = convert;
        }

        Conversion(String from, String to, Function<Double, Double> convert) {
            this(new LinkedList<>(), new LinkedList<>(), convert);
            this.from.add(from);
            this.to.add(to);

        }

        Conversion reverse(Function<Double, Double> convert) {
            return new Conversion(this.to, this.from, convert);
        }

        Conversion alias(String from, String to) {
            if (!this.from.contains(from) && !from.isEmpty()) {
                this.from.add(from);
            }
            if (!this.to.contains(to) && !to.isEmpty()) {
                this.to.add(to);
            }

            return this;
        }

        Conversion register() {
            AutoUnitConverter that = AutoUnitConverter.this;

            for (String fromUnit : this.from) {
                fromUnit = fromUnit.toLowerCase();
                if (!that.conversions.containsKey(fromUnit)) {
                    that.conversions.put(fromUnit, new ArrayList<>());
                    that.conversions.get(fromUnit).add(this);
                }
            }

            return this;
        }

        double run(double in) {
            return this.convert.apply(in);
        }
    }
}
