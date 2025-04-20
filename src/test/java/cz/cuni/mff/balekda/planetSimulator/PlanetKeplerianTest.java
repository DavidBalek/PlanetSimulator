package cz.cuni.mff.balekda.planetSimulator;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for the PlanetKeplerian record.
 * This class contains unit tests for the methods related to creating 
 * PlanetKeplerian instances from strings and files, as well as extracting
 * orbital parameters from the Horizons API response.
 * 
 * @author David Balek
 */
public class PlanetKeplerianTest {

    private String horizonsData;

   
    /**
     * Test for creating a PlanetKeplerian instance from a Horizons API response string.
     * Verifies that the values are correctly extracted and the instance is created.
     *
     * @throws Exception If the data cannot be parsed properly.
     */
    @Test
    public void testCreatePlanetFromString1() throws Exception {    
        horizonsData = """
                 A = 1.496000000000E+05
                 EC = 0.016700000000
                 IN = 7.155000000000
                 W = 102.937300000000
                 OM = 348.739360000000
                 MA = 100.000000000000
                """; 
        
        double secondsFromEpoch = 1.0e7; 
        PlanetKeplerian planet = PlanetKeplerian.createPlanetFromString(horizonsData, secondsFromEpoch);

        // Assertions for expected values
        assertEquals(1.496e8, planet.semiMajorAxis(), 1e4);
        assertEquals(0.0167, planet.eccentricity(), 1e-4);
        assertEquals(Math.toRadians(7.155), planet.inclination(), 1e-6);
        assertEquals(Math.toRadians(102.9373), planet.argumentPerihelion(), 1e-6);
        assertEquals(Math.toRadians(348.73936), planet.longitudeNode(), 1e-6);
        assertEquals(Math.toRadians(100.0), planet.meanAnomaly(), 1e-6);
        assertEquals(1.0e7, planet.time());
    }
    
    /**
     * More challenging input
     *
     * @throws Exception If the data cannot be parsed properly.
     */
    @Test
    public void testCreatePlanetFromString2() throws Exception {    
        horizonsData = """
                 A =           1.496000000000E+05
                 EC      = 1.6700000000E-02
                 IN=   7.155000000000E+00
                 W   =1.02937300000000E+02
                 OM  =  3.48739360000000E+02
                 MA  =   1.00000000000000E+02
                """; 
        
        double secondsFromEpoch = 1.0e7; 
        PlanetKeplerian planet = PlanetKeplerian.createPlanetFromString(horizonsData, secondsFromEpoch);

        // Assertions for expected values
        assertEquals(1.496e8, planet.semiMajorAxis(), 1e4);
        assertEquals(0.0167, planet.eccentricity(), 1e-4);
        assertEquals(Math.toRadians(7.155), planet.inclination(), 1e-6);
        assertEquals(Math.toRadians(102.9373), planet.argumentPerihelion(), 1e-6);
        assertEquals(Math.toRadians(348.73936), planet.longitudeNode(), 1e-6);
        assertEquals(Math.toRadians(100.0), planet.meanAnomaly(), 1e-6);
        assertEquals(1.0e7, planet.time());
    }

    
}
