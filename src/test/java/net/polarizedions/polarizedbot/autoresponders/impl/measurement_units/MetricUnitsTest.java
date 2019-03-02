package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import net.polarizedions.polarizedbot.autoresponders.impl.measurement_units.UnitTypes.Imperial;
import net.polarizedions.polarizedbot.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricUnitsTest {

    @SuppressWarnings("unchecked")
    <T extends Double, U> Pair<T, U> floorToTwoDigits(Pair<T, U> pair) {
        T num = (T) ((Double) this.floorToTwoDigits(pair.one));
        return new Pair<>(num, pair.two);
    }

    double floorToTwoDigits(double in) {
        return Math.floor(in * 100) / 100;
    }

    @Test
    void fromMillimeter() {
        assertEquals(new Pair<>(0.03, Imperial.INCH), this.floorToTwoDigits(MetricUnits.fromMillimeter(1.0)));
        assertEquals(new Pair<>(1.57, Imperial.INCH), this.floorToTwoDigits(MetricUnits.fromMillimeter(40.0)));
        assertEquals(new Pair<>(1.64, Imperial.FOOT), this.floorToTwoDigits(MetricUnits.fromMillimeter(500.0)));
    }

    @Test
    void fromCentimeter() {
        assertEquals(new Pair<>(0.39, Imperial.INCH), this.floorToTwoDigits(MetricUnits.fromCentimeter(1.0)));
        assertEquals(new Pair<>(1.64, Imperial.FOOT), this.floorToTwoDigits(MetricUnits.fromCentimeter(50.0)));
        assertEquals(new Pair<>(1.09, Imperial.YARD), this.floorToTwoDigits(MetricUnits.fromCentimeter(100.0)));
    }

    @Test
    void fromMeter() {
        assertEquals(new Pair<>(1.09, Imperial.YARD), this.floorToTwoDigits(MetricUnits.fromMeter(1.0)));
    }

    @Test
    void fromKilometer() {
        assertEquals(new Pair<>(0.62, Imperial.MILE), this.floorToTwoDigits(MetricUnits.fromKilometer(1.0)));
    }
}