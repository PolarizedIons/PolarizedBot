package net.polarizedions.polarizedbot.autoresponders.impl.measurement_units;

import org.junit.jupiter.api.Test;

import static net.polarizedions.polarizedbot.autoresponders.impl.measurement_units.UnitTypes.Imperial;
import static net.polarizedions.polarizedbot.autoresponders.impl.measurement_units.UnitTypes.Metric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UnitTypesTest {

    @Test
    void identifyMetic() {
        assertEquals(Metric.MILLIMETER, UnitTypes.identify("mm"));
        assertEquals(Metric.MILLIMETER, UnitTypes.identify("millimeter"));
        assertEquals(Metric.MILLIMETER, UnitTypes.identify("millimeters"));

        assertEquals(Metric.CENTIMETER, UnitTypes.identify("cm"));
        assertEquals(Metric.CENTIMETER, UnitTypes.identify("centimeter"));
        assertEquals(Metric.CENTIMETER, UnitTypes.identify("centimeters"));

        assertEquals(Metric.METER, UnitTypes.identify("m"));
        assertEquals(Metric.METER, UnitTypes.identify("meter"));
        assertEquals(Metric.METER, UnitTypes.identify("meters"));

        assertEquals(Metric.KILOMETER, UnitTypes.identify("km"));
        assertEquals(Metric.KILOMETER, UnitTypes.identify("kilometer"));
        assertEquals(Metric.KILOMETER, UnitTypes.identify("kilometers"));

    }


    @Test
    void identifyImperial() {
//        assertEquals(Imperial.INCH, UnitTypes.identify("in"));
        assertNull(UnitTypes.identify("in"));
        assertEquals(Imperial.INCH, UnitTypes.identify("inch"));
        assertEquals(Imperial.INCH, UnitTypes.identify("inches"));
        assertEquals(Imperial.INCH, UnitTypes.identify("'"));

        assertEquals(Imperial.FOOT, UnitTypes.identify("ft"));
        assertEquals(Imperial.FOOT, UnitTypes.identify("foot"));
        assertEquals(Imperial.FOOT, UnitTypes.identify("feet"));
        assertEquals(Imperial.FOOT, UnitTypes.identify("\""));

        assertEquals(Imperial.YARD, UnitTypes.identify("yd"));
        assertEquals(Imperial.YARD, UnitTypes.identify("yard"));
        assertEquals(Imperial.YARD, UnitTypes.identify("yards"));

        assertEquals(Imperial.MILE, UnitTypes.identify("mi"));
        assertEquals(Imperial.MILE, UnitTypes.identify("mile"));
        assertEquals(Imperial.MILE, UnitTypes.identify("miles"));
    }

    @Test
    void defaultNull() {
        assertNull(UnitTypes.identify("blabla"));
    }
}