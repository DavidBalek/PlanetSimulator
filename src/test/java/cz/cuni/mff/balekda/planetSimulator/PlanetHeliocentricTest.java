package cz.cuni.mff.balekda.planetSimulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the PlanetHeliocentric class, which represents a planet's position in heliocentric coordinates.
 */
public class PlanetHeliocentricTest {
    
    /**
     * Tests that the distanceFromSun() method correctly returns the distance to the Sun.
     */
    @Test
    public void testDistanceFromSun() {
        PlanetHeliocentric planet = new PlanetHeliocentric(1.496e11, 0, 0);
        double distance = planet.distanceFromSun();
        assertEquals(1.496e11, distance, 1e6);
    }
    
    /**
     * Tests that the toRADec() method correctly computes the Right Ascension and Declination for a planet.
     */
    @Test
    public void testToRADec() {
        PlanetHeliocentric planetA = new PlanetHeliocentric(1.496e11, -1.550e10, 0);
        PlanetHeliocentric planetB = new PlanetHeliocentric(1.496e11, 0, 1e10);

        PlanetRADec radDec = planetA.toRADec(planetB);
        assertNotNull(radDec);
        assertTrue(radDec.RA() >= 0 && radDec.RA() < 24);
        assertTrue(radDec.declination() >= -90 && radDec.declination() <= 90);
    }
    
    /**
     * Tests that the toArray() method correctly returns the planet's position in a 3D coordinate array.
     */
    @Test
    public void testToArray() {
        PlanetHeliocentric planet = new PlanetHeliocentric(1.496e11, 0, 0);
        double[] coords = planet.toArray();
        assertArrayEquals(new double[] { 1.496e11, 0.0, 0.0 }, coords, 1e6);
    }
}