package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ImperialUnits {
    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Metric> fromInch(Double from) {
        if (from < 40) {
            return new Pair<>(from *  2.54, UnitTypes.Metric.CENTIMETER);
        }
        else {
            return new Pair<>(from / 39.37, UnitTypes.Metric.METER);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Metric> fromFoot(Double from) {
        if (from < 3) {
            return new Pair<>(from * 30.48, UnitTypes.Metric.CENTIMETER);
        }
        else {
            return new Pair<>(from / 3.281, UnitTypes.Metric.METER);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Metric> fromYard(Double from) {
        if (from < 2) {
            return new Pair<>(from * 91.44, UnitTypes.Metric.CENTIMETER);
        }
        else {
            return new Pair<>(from / 1.094, UnitTypes.Metric.METER);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Metric> fromMile(Double from) {
        return new Pair<>(from * 1.609, UnitTypes.Metric.KILOMETER);
    }
}
