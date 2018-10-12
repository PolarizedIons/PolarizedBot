package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class UnitTypes {

    @Nullable
    public static Pair<Metric, Imperial> identify(@NotNull String suffix) {
        switch (suffix.toLowerCase()) {
            case "mm":
            case "millimeter":
            case "millimeters":
                return new Pair<>(Metric.MILLIMETER, null);

            case "cm":
            case "centimeter":
            case "centimeters":
                return new Pair<>(Metric.CENTIMETER, null);

            case "m":
            case "meter":
            case "meters":
                return new Pair<>(Metric.METER, null);

            case "km":
            case "kilometer":
            case "kilometers":
                return new Pair<>(Metric.KILOMETER, null);

            // case "in":  // Too common
            case "inch":
            case "inches":
            case "'":
                return new Pair<>(null, Imperial.INCH);

            case "ft":
            case "foot":
            case "feet":
            case "\"":
                return new Pair<>(null, Imperial.FOOT);

            case "yd":
            case "yard":
            case "yards":
                return new Pair<>(null, Imperial.YARD);

            case "mi":
            case "mile":
            case "miles":
                return new Pair<>(null, Imperial.MILE);

            default:
                return null;
        }
    }

    public interface IUnit<Other extends IUnit> {
        String getSuffix();
        Pair<Double, Other> convert(double from);
    }

    public enum Imperial implements IUnit<Metric> {
        INCH("inch", ImperialUnits::fromInch),
        FOOT("foot", ImperialUnits::fromFoot),
        YARD("yard", ImperialUnits::fromYard),
        MILE("mile", ImperialUnits::fromMile);

        String suffix;
        Function<Double, Pair<Double, Metric>> converter;
        Imperial(String suffix, Function<Double, Pair<Double, Metric>> converter) {
            this.suffix = suffix;
            this.converter = converter;
        }

        @Override
        @Contract(pure = true)
        public String getSuffix() {
            return this.suffix;
        }

        @Override
        public Pair<Double, Metric> convert(double from) {
            return this.converter.apply(from);
        }
    }

    public enum Metric implements IUnit<Imperial> {
        MILLIMETER("mm", MetricUnits::fromMillimeter),
        CENTIMETER("cm", MetricUnits::fromCentimeter),
        METER("m", MetricUnits::fromMeter),
        KILOMETER("km", MetricUnits::fromKilometer);

        String suffix;
        Function<Double, Pair<Double, Imperial>> converter;
        Metric(String suffix, Function<Double, Pair<Double, Imperial>> converter) {
            this.suffix = suffix;
            this.converter = converter;
        }

        @Override
        @Contract(pure = true)
        public String getSuffix() {
            return this.suffix;
        }

        @Override
        public Pair<Double, Imperial> convert(double from) {
            return this.converter.apply(from);
        }
    }
}
