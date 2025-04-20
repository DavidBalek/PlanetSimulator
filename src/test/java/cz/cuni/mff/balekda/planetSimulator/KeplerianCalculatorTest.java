package cz.cuni.mff.balekda.planetSimulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the KeplerianCalculator class. 
 * This class contains unit tests for the methods solving Kepler's equation 
 * and computing the 3D position of a celestial body based on Keplerian elements.
 * 
 * @author David Balek
 */
public class KeplerianCalculatorTest {

    private PlanetKeplerian planet;

    /**
     * Set up method that runs before each test.
     * Initializes the KeplerianCalculator instance.
     */
    @BeforeEach
    public void setUp() {
        planet = new PlanetKeplerian(1.496e11, 0.0167, Math.toRadians(7.155), Math.toRadians(102.9373), 
                Math.toRadians(348.73936), Math.toRadians(100), 365.25 * 24 * 3600);
    }


    /**
     * Test for computing the 3D position of a celestial body in orbit.
     * This test ensures that the position is finite.
     * 
     */
    @Test
    public void testComputePosition1() {
        
        PlanetHeliocentric position = KeplerianCalculator.computePosition(planet);
        assertNotNull(position, "Position should not be null");

        assertTrue(Double.isFinite(position.x()));
        assertTrue(Double.isFinite(position.y()));
        assertTrue(Double.isFinite(position.z()));
    }
    
    /**
     * Test for computing the 3D position of a celestial body in orbit.
     * This test ensures that the distance is no more than a*(1+e) and not less than a*(1-e) (within 2%)
     * 
     */
    @Test
    public void testComputePosition2() {
        
        PlanetHeliocentric position = KeplerianCalculator.computePosition(planet);
        assertNotNull(position);
        
        double r = position.distanceFromSun();
        double aphelium = planet.semiMajorAxis() * (1 + planet.eccentricity()) * 1.02;
        double perihelium = planet.semiMajorAxis() * (1 - planet.eccentricity()) * 1.02;
        assertTrue(r <= aphelium);
        assertTrue(r >= perihelium);
    }
}
