package cz.cuni.mff.balekda.planetSimulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A record representing a planet's Keplerian orbital parameters.
 * This includes the semi-major axis, eccentricity, inclination, 
 * argument of perihelion, longitude of ascending node, and mean anomaly.
 * These parameters are used for orbital calculations, such as determining the
 * planet's position in its orbit at a specific time.
 * 
 * @param semiMajorAxis        The semi-major axis of the planet's orbit in meters.
 *                             This represents the average distance between the planet and the Sun.
 * @param eccentricity         The eccentricity of the planet's orbit.
 * @param inclination          The inclination of the planet's orbit in radians.
 *                             This is the angle between the planet's orbital plane and the ecliptic plane.
 * @param argumentPerihelion   The argument of perihelion in radians.
 *                             This is the angle from the ascending node to the perihelion.
 * @param longitudeNode        The longitude of the ascending node in radians.
 *                             This is the angle from the vernal equinox to the ascending node.
 * @param meanAnomaly          The mean anomaly in radians.
 *                             This is the angular distance the planet has traveled since perihelion.
 * @param time                 The time offset from a fixed epoch in seconds.
 * 
 * @author David Balek
 */
public record PlanetKeplerian(
        
    double semiMajorAxis,
    double eccentricity,
    double inclination,
    double argumentPerihelion,
    double longitudeNode,
    double meanAnomaly,
    double time
)
{
    /**
     * Extracts Keplerian orbital elements from a Horizons API response string
     * and constructs a {@link PlanetKeplerian} record.
     *
     * @param source The full string response from the Horizons API.
     * @param secondsFromEpoch The number of seconds since a reference epoch (used as the simulation time).
     * @return A {@link PlanetKeplerian} instance representing the orbital elements.
     * @throws Exception If parsing fails or any required value is missing from the response.
     */
    public static PlanetKeplerian createPlanetFromString(String source, double secondsFromEpoch) throws Exception{
        try{
            double a_km = extractValue(source, "\\sA\\s*=\\s*([\\d.E+-]+)");
            double e = extractValue(source, "\\sEC\\s*=\\s*([\\d.E+-]+)");
            double i_deg = extractValue(source, "\\sIN\\s*=\\s*([\\d.E+-]+)");
            double w_deg = extractValue(source, "\\sW\\s*=\\s*([\\d.E+-]+)");
            double Omega_deg = extractValue(source, "\\sOM\\s*=\\s*([\\d.E+-]+)");
            double M_deg = extractValue(source, "\\sMA\\s*=\\s*([\\d.E+-]+)");

            double a_m = a_km * 1000;
            double i_rad = Math.toRadians(i_deg);
            double w_rad = Math.toRadians(w_deg);
            double Omega_rad = Math.toRadians(Omega_deg);
            double M_rad = Math.toRadians(M_deg);

            return new PlanetKeplerian(
                a_m, e, i_rad,
                w_rad, Omega_rad,
                M_rad, secondsFromEpoch
            );
        }
        catch (Exception ex){
            throw new Exception("""
                                Could not parse expected values from data Horizons provided here is the data we have got
                                """ + source);
        }
    }
    
    
    
    /**
     * Extracts a numeric value from a text string using a regular expression pattern.
     *
     * @param input The input text to search.
     * @param pattern The regular expression pattern with a single capturing group.
     * @return The extracted value as a double.
     * @throws IllegalArgumentException If the pattern is not found in the input.
     */
     private static double extractValue(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        } else {
            throw new IllegalArgumentException("Could not find value for pattern: " + pattern);
        }
    }

    /**
     * Reads a Horizons API response from a file and parses a {@link PlanetKeplerian} record from it.
     *
     * @param fileName The path to the file containing the API response.
     * @param secondsFromEpoch The number of seconds since the simulation epoch.
     * @return A {@link PlanetKeplerian} instance with parsed orbital elements.
     * @throws IOException If reading the file fails.
     * @throws Exception If the file content cannot be parsed properly.
     */
    public static PlanetKeplerian creatPlanetFromFile(String fileName , double secondsFromEpoch) throws IOException, Exception{
        String content = Files.readString(Paths.get(fileName));
        return createPlanetFromString(content, secondsFromEpoch);
    }
        
}
