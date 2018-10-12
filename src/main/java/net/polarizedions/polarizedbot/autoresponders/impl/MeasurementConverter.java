package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.autoresponders.IResponder;
import net.polarizedions.polarizedbot.autoresponders.impl.measurement_units.UnitTypes;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MeasurementConverter implements IResponder {
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");

    @Override
    public String getID() {
        return "measurement";
    }

    @Override
    public void run(IMessage message) {
        List<Pair<Double, UnitTypes.IUnit<? extends UnitTypes.IUnit>>> foundUnits = new ArrayList<>();

        String contents = message.getContent();
        int cursor = 0;
        while (cursor < contents.length()) {
            Pair<Double, Integer> value = this.readDouble(contents, cursor);
            if (cursor == value.two) {
                cursor++;
                continue;
            }
            cursor = value.two;
            if (value.one == null) {
                cursor = this.skipWord(contents, cursor);
                continue;
            }

            Pair<String, Integer> unit = this.readWord(contents, cursor);
            if (cursor == unit.two) {
                cursor++;
                continue;
            }
            cursor = unit.two;
            if (unit.one == null) {
                cursor = this.skipWord(contents, cursor);
                continue;
            }

            UnitTypes.IUnit<? extends UnitTypes.IUnit> unitType = UnitTypes.identify(unit.one);
            if (unitType != null) {
                foundUnits.add(new Pair<>(value.one, unitType));
                continue;
            }

            cursor++;
        }

        StringBuilder response = new StringBuilder("```");
        for (Pair<Double, UnitTypes.IUnit<? extends UnitTypes.IUnit>> unit : foundUnits) {
            Double from = unit.one;
            if (unit.two instanceof UnitTypes.Metric) {
                UnitTypes.Metric fromUnit = (UnitTypes.Metric)unit.two;
                Pair<Double, UnitTypes.Imperial> converted = fromUnit.convert(from);
                response.append(NUMBER_FORMAT.format(from)).append(" ").append(fromUnit.getSuffix()).append(" = ").append(NUMBER_FORMAT.format(converted.one)).append(" ").append(converted.two.getSuffix()).append("\n");
            }
            else if (unit.two instanceof UnitTypes.Imperial) {
                UnitTypes.Imperial fromUnit = (UnitTypes.Imperial)unit.two;
                Pair<Double, UnitTypes.Metric> converted = fromUnit.convert(from);
                response.append(NUMBER_FORMAT.format(from)).append(" ").append(fromUnit.getSuffix()).append(" = ").append(NUMBER_FORMAT.format(converted.one)).append(" ").append(converted.two.getSuffix()).append("\n");
            }
        }

        if (response.length() > 3) {
            MessageUtil.sendAutosplit(message.getChannel(), response.append("```").toString(), "```", "```");
        }
    }

    private int skipSpaces(@NotNull String str, int cursor) {
        while (cursor < str.length() && Character.isSpaceChar(str.charAt(cursor))) {
            cursor++;
        }

        return cursor;
    }

    @NotNull
    @Contract("_, _ -> new")
    private Pair<Double, Integer> readDouble(@NotNull String str, int cursor) {
        cursor = this.skipSpaces(str, cursor);
        StringBuilder working = new StringBuilder();

        while (cursor < str.length() && this.isDigit(str.charAt(cursor))) {
            working.append(str.charAt(cursor));
            cursor++;
        }

        try {
            return new Pair<>(Double.parseDouble(working.toString()), cursor);
        }
        catch (NumberFormatException ex) {
            /* noop */
        }

        return new Pair<>(null, cursor);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c) || c == '.';
    }

    @NotNull
    @Contract("_, _ -> new")
    private Pair<String, Integer> readWord(String str, int cursor) {
        cursor = this.skipSpaces(str, cursor);
        StringBuilder working = new StringBuilder();

        while (cursor < str.length() && this.isWordChar(str.charAt(cursor))) {
            working.append(str.charAt(cursor));
            cursor++;
        }

        if (working.length() > 0 && (cursor == str.length() || Character.isSpaceChar(str.charAt(cursor)) || this.isPunctuation(str.charAt(cursor)))) {
            return new Pair<>(working.toString(), cursor);
        }

        return new Pair<>(null, cursor);
    }

    @Contract(pure = true)
    private boolean isWordChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    @Contract(pure = true)
    private boolean isPunctuation(char c) {
        return c == '.' || c == '?' || c == ',' || c == '!' || c == '(' || c == ')';
    }

    private int skipWord(@NotNull String str, int cursor) {
        while (cursor < str.length() && this.isWordChar(str.charAt(cursor))) {
            cursor++;
        }

        return cursor;
    }
}
