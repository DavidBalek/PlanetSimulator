package cz.cuni.mff.balekda.planetSimulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

/**
 * Unit tests for the TimeConverter class
 */
public class TimeConverterTest {
    /**
     * Tests the toJulianDate() method, which converts a given Instant to Julian Date.
     */
    @Test
    public void testToJulianDate() {
        Instant instant = Instant.parse("2000-01-01T12:00:00.00Z");
        double julianDate = TimeConverter.toJulianDate(instant);
        assertEquals(TimeConverter.JD_2000_IN_JD, julianDate, 1e-6);
    }
    
    /**
     * Tests the fromJulianDate() method, which converts a given Julian Date back to an Instant.
     */
    @Test
    public void testFromJulianDate() {
        double julianDate = TimeConverter.JD_2000_IN_JD;
        Instant instant = TimeConverter.fromJulianDate(julianDate);
        assertEquals(Instant.parse("2000-01-01T12:00:00.00Z"), instant);
    }
    
    /**
     * Compares the computed ERA to the expected value based on the known constant.
     */
    @Test
    public void testComputeERA() {
        double julianDate = TimeConverter.JD_2000_IN_JD;
        double era = TimeConverter.computeERA(julianDate);
        double expectedValue =  2 * Math.PI * 0.7790572732640;
        assertEquals(expectedValue, era, 1e-6);
    }
}