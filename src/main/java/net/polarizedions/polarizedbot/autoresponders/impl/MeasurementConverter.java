package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.autoresponders.IResponder;
import net.polarizedions.polarizedbot.autoresponders.impl.measurement_units.UnitTypes;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashSet;
import java.util.Set;

public class MeasurementConverter implements IResponder {

    @Override
    public String getID() {
        return "measurement";
    }

    @Override
    public void run(IMessage message) {
        Set<Pair<Double, UnitTypes.IUnit<? extends UnitTypes.IUnit>>> foundUnits = new HashSet<>();

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

            Pair<String, Integer> unit = this.readUnit(contents, cursor);
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

        Localizer localizer = new Localizer(message);
        StringBuilder response = new StringBuilder("```\n");
        for (Pair<Double, UnitTypes.IUnit<? extends UnitTypes.IUnit>> unit : foundUnits) {
            Double from = unit.one;
            if (from <= 0.00) {
                continue;
            }

            UnitTypes.IUnit<? extends UnitTypes.IUnit> fromUnit = unit.two;
            Pair<Double, ? extends UnitTypes.IUnit> converted = fromUnit.convert(from);
            response.append(fromUnit.format(localizer, from)).append(" = ").append(converted.two.format(localizer, converted.one)).append("\n");
        }

        if (response.length() > 4) {
            MessageUtil.sendAutosplit(message.getChannel(), response.append("```").toString(), "```", "```");
        }
    }

    int skipSpaces(@NotNull String str, int cursor) {
        while (cursor < str.length() && Character.isSpaceChar(str.charAt(cursor))) {
            cursor++;
        }

        return cursor;
    }

    @NotNull
    @Contract("_, _ -> new")
    Pair<Double, Integer> readDouble(@NotNull String str, int cursor) {
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

    boolean isDigit(char c) {
        return Character.isDigit(c) || c == '.';
    }

    @NotNull
    @Contract("_, _ -> new")
    Pair<String, Integer> readUnit(String str, int cursor) {
        cursor = this.skipSpaces(str, cursor);
        StringBuilder working = new StringBuilder();

        while (cursor < str.length() && (this.isWordChar(str.charAt(cursor)) || str.charAt(cursor) == '\'' || str.charAt(cursor) == '"')) {
            working.append(str.charAt(cursor));
            cursor++;
        }

        if (working.length() > 0 && (cursor == str.length() || Character.isSpaceChar(str.charAt(cursor)) || this.isPunctuation(str.charAt(cursor)))) {
            return new Pair<>(working.toString(), cursor);
        }

        return new Pair<>(null, cursor);
    }

    @Contract(pure = true)
    boolean isWordChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    @Contract(pure = true)
    boolean isPunctuation(char c) {
        return c == '.' || c == '?' || c == ',' || c == '!' || c == '(' || c == ')' || c == '"' || c == '\'';
    }

    int skipWord(@NotNull String str, int cursor) {
        while (cursor < str.length() && this.isWordChar(str.charAt(cursor))) {
            cursor++;
        }

        return cursor;
    }
}
