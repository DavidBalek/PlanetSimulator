package cz.cuni.mff.balekda.planetSimulator;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 *
 * @author David Balek
 */
public class PlanetSimulator {
    /**
     * The fixed reference epoch used for time offset calculations.
     * Defined as 2025-01-01T13:00:00Z (TDB equivalent in UTC).
     */
    private static final Instant EPOCH = Instant.parse("2025-01-01T13:00:00.00Z");
    
    
    /**
     * Parses command-line arguments into an {@link ArgumentParser} object.
     *
     * @param args The command-line arguments.
     * @return A new {@link ArgumentParser} instance containing parsed arguments.
     */
    private static ArgumentParser getArguments(String[] args){
        return new ArgumentParser(args);
    }
    
    
    /**
     * Computes the number of seconds between the reference epoch and the given instant.
     *
     * @param instant The instant to compare with the reference epoch.
     * @return The number of seconds from the reference epoch to the given instant.
     */
    private static double getSecondsFromEpoch(Instant instant){
        return (double) Duration.between(EPOCH, instant).getSeconds();
    }
    
    
    /**
     * Retrieves Keplerian orbital elements for the specified object from the Horizons API
     * and constructs a {@link PlanetKeplerian} instance at a given time.
     *
     * @param time The desired instant of observation.
     * @param object The name or ID of the celestial object to query.
     * @return A {@link PlanetKeplerian} instance with orbital data.
     * @throws IOException If the network request fails.
     * @throws Exception If the response cannot be parsed into a planet object.
     */
    private static PlanetKeplerian getPlanet(Instant time, String object) throws IOException, Exception{
        HorizonsAPI api = new HorizonsAPI();
        String data = api.getData(object);
        return PlanetKeplerian.createPlanetFromString(data, getSecondsFromEpoch(time));
    }
    
    private static PlanetKeplerian getEarth(Instant time){
        double semiMajorAxis = 1.482723189000168E+08 * 1000.0;
        double e = 1.293398280839581E-02;
        double inclination = Math.toRadians(7.530442636380576E-03);
        double argPerihelion = Math.toRadians(6.804721922709237E+01);
        double longitudeNode = Math.toRadians(6.787791105814221E+00);
        double meanAnomaly = Math.toRadians(2.587134472915172E+01);
        return new PlanetKeplerian(semiMajorAxis, e, inclination,
                argPerihelion, longitudeNode, meanAnomaly, getSecondsFromEpoch(time));
    }

    public static void main(String[] args) {
        try {
            PlanetKeplerian earth = getEarth(EPOCH);
            PlanetKeplerian mars = getPlanet(EPOCH, "Apophis");

            PlanetHeliocentric marsHel = KeplerianCalculator.computePosition(mars);
            PlanetHeliocentric earthHel = KeplerianCalculator.computePosition(earth);

            ////////////////////////////////////
            System.out.println("Mars distance to Sun: " + marsHel.distanceFromSun());
            System.out.println("Earth distance to Sun: " + earthHel.distanceFromSun());
            System.out.println("Sun long: " + Math.atan2(-earthHel.y(), -earthHel.x()) / Math.PI * 180.0);

            ///////////////////////
            PlanetRADec marsRADec = earthHel.toRADec(marsHel);
            System.out.println(marsRADec);
            Instant time = TimeConverter.fromJulianDate(2460677 + 0);
            double [] azAlt = marsRADec.toAltitudeAzimuth(time, 49.5, 14.5);
            System.out.println("Azimuth = " + azAlt[1] + " altitude = " + azAlt[0]);
            double[] riseSet = marsRADec.estimateRiseSetTimes(time, 49.5);
            System.out.println("Sunrise = " + riseSet[0] + " sunset = " + riseSet[1] + " transit = " + riseSet[2]);
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
