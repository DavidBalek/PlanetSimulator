package cz.cuni.mff.balekda.planetSimulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author David Balek
 */

/**
 * A record representing a planet's Keplerian orbital parameters.
 * This includes the semi-major axis, eccentricity, inclination, 
 * longitude of perihelion, longitude of ascending node, and mean anomaly.
 * These parameters are used for orbital calculations, such as determining the
 * orbital period of the planet.
 */
public record PlanetKeplerian(
    /**
     * The semi-major axis of the planet's orbit in m.
     * This represents the average distance between the planet and the Sun.
     */
    double semiMajorAxis,

    /**
     * The eccentricity of the planet's orbit.
     */
    double eccentricity,

    /**
     * The inclination of the planet's orbit in radians.
     * This is the angle between the planet's orbital plane and the ecliptic plane.
     */
    double inclination,

    /**
     * The longitude of the planet's perihelion in radians.
     * This is the angle from the ascending node to the perihelion.
     */
    double argumentPerihelion,

    /**
     * The longitude of the ascending node in radians.
     * This is the angle from the first point of Aries to the ascending node of the planet's orbit.
     */
    double longitudeNode,

    /**
     * The mean anomaly of the planet in radians.
     * This is a fraction of a elliptical orbit's period that has elapsed since the
     * planet perihelion
     */
    double meanAnomaly,
       
    /**
     * The time after epoch in seconds.
     */
    double time
)
{
    public static PlanetKeplerian createPlanetFromString(String source){
        double semiMajorAxis = 0;
        double eccentricity = 0;
        double inclination = 0;
        double argumentPerihelion = 0;
        double longitudeNode = 0;
        double meanAnomaly = 0;
        double time = 0;
        return new PlanetKeplerian(
                    semiMajorAxis,
                    eccentricity,
                    inclination,
                    argumentPerihelion,
                    longitudeNode,
                    meanAnomaly,
                    time);
    }
    
    public static PlanetKeplerian creatPlanetFromFile(String fileName) throws IOException{
        String content = Files.readString(Paths.get(fileName));
        return createPlanetFromString(content);
    }
        
}
