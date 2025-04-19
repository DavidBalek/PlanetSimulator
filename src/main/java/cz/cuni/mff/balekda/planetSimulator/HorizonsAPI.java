package cz.cuni.mff.balekda.planetSimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author David Balek
 */


/**
 * A utility class to retrieve planetary ephemeris and orbital element data
 * from NASA JPL's Horizons API using the 'ELEMENTS' ephemeris type.
 *
 * <p>This class constructs properly encoded API requests and retrieves
 * the raw response for a given solar system object.
 */
public class HorizonsAPI {
    
    /**
     * Constructs the full URI for a Horizons API request to fetch orbital elements
     * for a specified solar system object on a fixed date.
     *
     * <p>The request uses the following fixed parameters:
     * <ul>
     *   <li>Start time: 2025-01-01 12:00:00.000</li>
     *   <li>Stop time: 2025-01-02</li>
     *   <li>Step size: 1 day</li>
     *   <li>Center: Solar System Barycenter (500@0)</li>
     *   <li>Output units: kilometers and seconds</li>
     * </ul>
     *
     * @param object The Horizons object ID or name (e.g., "499" for Mars).
     * @return A URI object representing the fully encoded API request.
     */
    private URI getURI(String object){
        String command = "'" + object + "'";
        
        String baseUrl = "https://ssd.jpl.nasa.gov/api/horizons.api";
        String query = String.format(
            "format=json&COMMAND=%s&OBJ_DATA=%s&EPHEM_TYPE=%s&CENTER=%s&START_TIME=%s&STOP_TIME=%s&STEP_SIZE=%s&OUT_UNITS=%s",
            URLEncoder.encode(command, StandardCharsets.UTF_8),
            URLEncoder.encode("'NO'", StandardCharsets.UTF_8),
            URLEncoder.encode("'ELEMENTS'", StandardCharsets.UTF_8),
            URLEncoder.encode("'500@0'", StandardCharsets.UTF_8),
            URLEncoder.encode("'2025-01-01 12:00:00.000'", StandardCharsets.UTF_8),
            URLEncoder.encode("'2025-01-02'", StandardCharsets.UTF_8),
            URLEncoder.encode("'1 d'", StandardCharsets.UTF_8),
            URLEncoder.encode("'KM-S'", StandardCharsets.UTF_8)
        );

        String fullUrl = baseUrl + "?" + query;
        return URI.create(fullUrl);
    }
    
    /**
     * Sends an HTTP GET request to NASA JPL Horizons and retrieves the raw API response.
     *
     * @param object The Horizons object ID or name (e.g., "499" for Mars).
     * @return The full text content returned by the API as a single string.
     * @throws MalformedURLException If the constructed URI is not valid for URL conversion.
     * @throws IOException If there is an error in connecting to the API or reading the response.
     */
    public String getData(String object) throws MalformedURLException, IOException {
        URI uriAPI = getURI(object);
        InputStream inputStream = uriAPI.toURL().openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }
}
