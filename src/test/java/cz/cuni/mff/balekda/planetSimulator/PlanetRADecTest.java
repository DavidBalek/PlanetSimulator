package cz.cuni.mff.balekda.planetSimulator;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PlanetRADec class 
 */
public class PlanetRADecTest {
    /**
     * Tests that the isValid() method correctly validates Right Ascension and Declination.
     */
    @Test
    public void testIsValid() {
        PlanetRADec planet = new PlanetRADec(10.0, 30.0);   
        assertTrue(planet.isValid());
        
        PlanetRADec invalidRA = new PlanetRADec(25.0, 30.0);
        assertFalse(invalidRA.isValid());
        
        PlanetRADec invalidDec = new PlanetRADec(10.0, 95.0);
        assertFalse(invalidDec.isValid());
    }
    
    /**
     * Tests the toAltitudeAzimuth() method, which converts Right Ascension and Declination 
     */
    @Test
    public void testToAltitudeAzimuth() {
        PlanetRADec planet = new PlanetRADec(10.0, 20.0);
        Instant time = Instant.parse("2025-04-20T12:00:00.00Z");
        double latitude = 40.0; 
        double longitude = -75.0;
        double[] altAz = planet.toAltitudeAzimuth(time, latitude, longitude);
        assertTrue(altAz[0] >= -90 && altAz[0] <= 90);
        assertTrue(altAz[1] >= 0 && altAz[1] < 360);
    }
    
    /**
     * The test ensures that rise, transit and set are within the valid range of [0, 24) hours.
     */
    @Test
    public void testEstimateRiseSetTimes() {
        PlanetRADec planet = new PlanetRADec(10.0, 20.0);
        Instant date = Instant.parse("2025-04-20T00:00:00.00Z");
        double latitude = 40.0;
        
        double[] riseSetTimes = planet.estimateRiseSetTimes(date, latitude);
        assertTrue(riseSetTimes[0] >= 0 && riseSetTimes[0] < 24);
        assertTrue(riseSetTimes[1] >= 0 && riseSetTimes[1] < 24);
        assertTrue(riseSetTimes[2] >= 0 && riseSetTimes[2] < 24);
    }
}