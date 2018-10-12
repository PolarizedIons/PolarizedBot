package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.Pair;
import net.polarizedions.polarizedbot.util.TriFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.function.Function;

public class UnitTypes {
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");

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

    public static String defaultFormatter(@NotNull Localizer localizer, @NotNull IUnit<? extends IUnit> unit, Double value) {
        return localizer.localizeNumber("autoresponder.measurement." + unit.getSuffix(), (int) (double)value, NUMBER_FORMAT.format(value));
    }

    public interface IUnit<Other extends IUnit> {
        String getSuffix();
        Pair<Double, Other> convert(double from);
        String format(Localizer localizer, double value);
    }

    public enum Imperial implements IUnit<Metric> {
        INCH("inch", ImperialUnits::fromInch, UnitTypes::defaultFormatter),
        FOOT("foot", ImperialUnits::fromFoot, UnitTypes::defaultFormatter),
        YARD("yard", ImperialUnits::fromYard, UnitTypes::defaultFormatter),
        MILE("mile", ImperialUnits::fromMile, UnitTypes::defaultFormatter);

        String suffix;
        Function<Double, Pair<Double, Metric>> converter;
        TriFunction<Localizer, IUnit<? extends IUnit>, Double, String> formatter;
        Imperial(String suffix, Function<Double, Pair<Double, Metric>> converter, TriFunction<Localizer, IUnit<? extends IUnit>, Double, String> formatter) {
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
        public String format(Localizer localizer, double value) {
            return this.formatter.apply(localizer, this, value);
        }
    }

    public enum Metric implements IUnit<Imperial> {
        MILLIMETER("mm", MetricUnits::fromMillimeter, UnitTypes::defaultFormatter),
        CENTIMETER("cm", MetricUnits::fromCentimeter, UnitTypes::defaultFormatter),
        METER("meter", MetricUnits::fromMeter, UnitTypes::defaultFormatter),
        KILOMETER("km", MetricUnits::fromKilometer, UnitTypes::defaultFormatter);

        String suffix;
        Function<Double, Pair<Double, Imperial>> converter;
        TriFunction<Localizer, IUnit<? extends IUnit>, Double, String> formatter;
        Metric(String suffix, Function<Double, Pair<Double, Imperial>> converter, TriFunction<Localizer, IUnit<? extends IUnit>, Double, String> formatter) {
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
        public String format(Localizer localizer, double value) {
            return this.formatter.apply(localizer, this, value);
        }
    }
}
