package cz.cuni.mff.balekda.planetSimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;

/**
 *
 * @author David Balek
 */


public class HorizonsAPI {
    
    private URI getURI(){
        String command = "'499'";
        String MAKE_EPHEM = "'YES'";
        String OBJ_DATA = "'NO'";
        String EPHEM_TYPE ="'ELEMENTS'";
        String START_TIME = "'2000-01-01 12:00:00.000'";
        String STOP_TIME = "'2000-01-01'";
        String CENTER = "'500@0'";
        /** URL to the Horizons API composed of all parameters
         * 
         */
        String uri = "https://ssd.jpl.nasa.gov/api/horizons.api?" + 
                "COMMAND=" + command + 
                "&MAKE_EPHEM=" + MAKE_EPHEM +
                "&OBJ_DATA=" + OBJ_DATA +
                "&EPHEM_TYPE=" + EPHEM_TYPE +
                "&CENTER=" + CENTER +
                "&START_TIME=" + START_TIME + 
                "&STOP_TIME=" + STOP_TIME +
                "&STEP_SIZE=" + "'2 d'";
        return URI.create(uri);
    }
    public String getData() throws MalformedURLException, IOException {
        URI uriAPI = getURI();
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
