package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import net.polarizedions.polarizedbot.autoresponders.impl.measurement_units.UnitTypes.Metric;
import net.polarizedions.polarizedbot.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImperialUnitsTest {

    @SuppressWarnings("unchecked")
    <T extends Double, U> Pair<T, U> floorToTwoDigits(Pair<T, U> pair) {
        T num = (T) ((Double) this.floorToTwoDigits(pair.one));
        return new Pair<>(num, pair.two);
    }

    double floorToTwoDigits(double in) {
        return Math.floor(in * 100) / 100;
    }

    @Test
    void fromInch() {
        assertEquals(new Pair<>(2.54, Metric.CENTIMETER), this.floorToTwoDigits(ImperialUnits.fromInch(1.0)));
        assertEquals(new Pair<>(1.27, Metric.METER), this.floorToTwoDigits(ImperialUnits.fromInch(50.0)));
    }

    @Test
    void fromFoot() {
        assertEquals(new Pair<>(30.48, Metric.CENTIMETER), this.floorToTwoDigits(ImperialUnits.fromFoot(1.0)));
        assertEquals(new Pair<>(15.23, Metric.METER), this.floorToTwoDigits(ImperialUnits.fromFoot(50.0)));
    }

    @Test
    void fromYard() {
        assertEquals(new Pair<>(91.44, Metric.CENTIMETER), this.floorToTwoDigits(ImperialUnits.fromYard(1.0)));
        assertEquals(new Pair<>(45.7, Metric.METER), this.floorToTwoDigits(ImperialUnits.fromYard(50.0)));
    }

    @Test
    void fromMile() {
        assertEquals(new Pair<>(1.60, Metric.KILOMETER), this.floorToTwoDigits(ImperialUnits.fromMile(1.0)));
    }
}