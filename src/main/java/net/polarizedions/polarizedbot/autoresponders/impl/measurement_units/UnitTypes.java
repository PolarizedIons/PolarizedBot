package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class UnitTypes {

    @Nullable
    @Contract(pure = true)
    public static IUnit<? extends IUnit> identify(@NotNull String suffix) {
        switch (suffix.toLowerCase()) {
            case "mm":
            case "millimeter":
            case "millimeters":
                return Metric.MILLIMETER;

            case "cm":
            case "centimeter":
            case "centimeters":
                return Metric.CENTIMETER;

            case "m":
            case "meter":
            case "meters":
                return Metric.METER;

            case "km":
            case "kilometer":
            case "kilometers":
                return Metric.KILOMETER;

            // case "in":  // Too common
            case "inch":
            case "inches":
            case "'":
                return Imperial.INCH;

            case "ft":
            case "foot":
            case "feet":
            case "\"":
                return Imperial.FOOT;

            case "yd":
            case "yard":
            case "yards":
                return Imperial.YARD;

            case "mi":
            case "mile":
            case "miles":
                return Imperial.MILE;

            default:
                return null;
        }
    }

    public interface IUnit<Other extends IUnit> {
        String getSuffix();
        Pair<Double, Other> convert(double from);
        String format(double value);
    }

    public enum Imperial implements IUnit<Metric> {
        INCH("inch", ImperialUnits::fromInch, null),
        FOOT("foot", ImperialUnits::fromFoot, null),
        YARD("yard", ImperialUnits::fromYard, null),
        MILE("mile", ImperialUnits::fromMile, null);

        String suffix;
        Function<Double, Pair<Double, Metric>> converter;
        Function<Double, String> formatter;
        Imperial(String suffix, Function<Double, Pair<Double, Metric>> converter, Function<Double, String> formatter) {
            this.suffix = suffix;
            this.converter = converter;
            this.formatter = formatter;
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

        @Override
        public String format(double value) {
            return this.formatter == null ? String.format("%s %s", value, this.suffix) : this.formatter.apply(value);
        }
    }

    public enum Metric implements IUnit<Imperial> {
        MILLIMETER("mm", MetricUnits::fromMillimeter, null),
        CENTIMETER("cm", MetricUnits::fromCentimeter, null),
        METER("meter", MetricUnits::fromMeter, null),
        KILOMETER("km", MetricUnits::fromKilometer, null);

        String suffix;
        Function<Double, Pair<Double, Imperial>> converter;
        Function<Double, String> formatter;
        Metric(String suffix, Function<Double, Pair<Double, Imperial>> converter, Function<Double, String> formatter) {
            this.suffix = suffix;
            this.converter = converter;
            this.formatter = formatter;
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

        @Override
        public String format(double value) {
            return this.formatter == null ? String.format("%s %s", value, this.suffix) : this.formatter.apply(value);
        }
    }
}
