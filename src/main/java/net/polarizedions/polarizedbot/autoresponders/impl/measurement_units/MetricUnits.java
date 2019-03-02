package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MetricUnits {
    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Imperial> fromMillimeter(Double from) {
        if (from < 305) { // < 12 in
            return new Pair<>(from / 25.4, UnitTypes.Imperial.INCH);
        }
        else { // > 12 in
            return new Pair<>(from / 304.8, UnitTypes.Imperial.FOOT);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Imperial> fromCentimeter(Double from) {
        if (from < 31) { // < 1 ft
            return new Pair<>(from / 2.54, UnitTypes.Imperial.INCH);
        }
        else if (from < 92) { // < 1 yd
            return new Pair<>(from / 30.48, UnitTypes.Imperial.FOOT);
        }
        else { // > 1 yd
            return new Pair<>(from / 91.44, UnitTypes.Imperial.YARD);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Imperial> fromMeter(Double from) {
        return new Pair<>(from * 1.094, UnitTypes.Imperial.YARD);
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, UnitTypes.Imperial> fromKilometer(Double from) {
        return new Pair<>(from / 1.609 , UnitTypes.Imperial.MILE);
    }
}
