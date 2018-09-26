package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.autoresponders.IResponder;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MeasurementConverter implements IResponder {
    private static final Map<String, Pair<String, Function<Double, Double>>> UNITS = new HashMap<>();
    private static final DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat("#.##");
    private static final DecimalFormat WHOLE_NUMBER = new DecimalFormat("#");


    private static Pattern REGEX_WORDS;
    private static Pattern REGEX_JOINED_FOOT_INCH = Pattern.compile("([0-9]+)'\\s?([0-9]+)\"");

    public MeasurementConverter() {
        this.addUnit("foot,feet,ft", "meters", ft-> ft * 0.3048);
        this.addUnit("meter,meters,m", "feet", m -> m / 0.3048);
        this.addUnit("cm,centimeter,centimeters", "inches", cm -> cm / 2.54);
        this.addUnit("inch,inches", "cm", in -> in * 2.54);  // no "in" because it's too common of a word
        this.addUnit("mi,mile,miles", "km", mi -> mi * 1.609344);
        this.addUnit("km,kilometer,kilometers", "miles", km -> km / 1.609344);

        REGEX_WORDS =  Pattern.compile(String.format("(?<=^|\\s)([0-9]+(?:\\.[0-9]+)?)\\s?(%s)", String.join("|", UNITS.keySet())), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    }

    private void addUnit(@NotNull String fromUnit, String toUnit, Function<Double, Double> converter) {
        this.addUnit(fromUnit.split(","), toUnit, converter);
    }

    private void addUnit(@NotNull String[] fromUnits, String toUnit, Function<Double, Double> converter) {
        Pair<String, Function<Double, Double>> pair = new Pair<>(toUnit, converter);
        for (String fromUnit : fromUnits) {
            UNITS.put(fromUnit, pair);
        }
    }

    @Override
    public String getID() {
        return "measurement";
    }

    @Override
    public void run(IMessage message) {
        Set<Pair<Integer, String>> matches = new TreeSet<>(Comparator.comparing(pair -> pair.one));

        Matcher m = REGEX_WORDS.matcher(message.getContent());
        while (m.find()) {
            double value = Double.parseDouble(m.group(1));
            String fromUnit = m.group(2);
            Pair<String, Function<Double, Double>> pair = UNITS.get(fromUnit);
            String toUnit = pair.one;
            Function<Double, Double> converter = pair.two;

            if (value == 0) {
                continue;
            }

            matches.add(new Pair<>(m.start(), TWO_DECIMAL_PLACES.format(value) + " " + fromUnit + " -> " + TWO_DECIMAL_PLACES.format(converter.apply(value)) + " " + toUnit));
        }

        m = REGEX_JOINED_FOOT_INCH.matcher(message.getContent());
        while (m.find()) {
            double feet = Double.parseDouble(m.group(1));
            double inches = Double.parseDouble(m.group(2));

            if (feet == 0 && inches == 0) {
                continue;
            }

            double combinedInches = (feet * 12) + inches;

            Pair<String, Function<Double, Double>> pair = UNITS.get(feet == 0 ? "inch" : "feet");
            String toUnit = pair.one;
            Function<Double, Double> converter = pair.two;

            matches.add(new Pair<>(m.start(), WHOLE_NUMBER.format(feet) + "'" + WHOLE_NUMBER.format(inches) + "\"" + " -> " + TWO_DECIMAL_PLACES.format(converter.apply(combinedInches)) + " " + toUnit));
        }

        if (matches.size() > 0) {
            String msg = "```" + String.join("\n", matches.stream().map(pair -> pair.two).collect(Collectors.toList())) + "```";

            MessageUtil.sendAutosplit(message.getChannel(), msg, "```", "```");
        }
    }
}
