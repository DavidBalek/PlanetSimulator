package cz.cuni.mff.balekda.planetSimulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Represents the core logic for computing and formatting planetary ephemeris data
 * based on user input, using the JPL Horizons API and Keplerian orbital mechanics.
 *
 * <p>This class supports outputting RA, Dec, distance to Sun, azimuth, altitude,
 * rise/set/transit times based on provided coordinates and time.</p>
 *
 * @author David Balek
 */
public class ResultData {
    private ArgumentParser arguments;
    
    /**
     * The fixed reference epoch used for time offset calculations.
     * Defined as 2025-01-01T13:00:00Z (TDB equivalent in UTC).
     */
    private static final Instant EPOCH = Instant.parse("2025-01-01T13:00:00.00Z");
    
    
    /**
     * Computes the number of seconds between the reference epoch and the given instant.
     *
     * @param instant The instant to compare with the reference epoch.
     * @return The number of seconds from the reference epoch to the given instant.
     */
    private double getSecondsFromEpoch(Instant instant){
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
    private PlanetKeplerian getPlanet(Instant time, String object) throws IOException, Exception{
        HorizonsAPI api = new HorizonsAPI();
        String data = api.getData(object);
        return PlanetKeplerian.createPlanetFromString(data, getSecondsFromEpoch(time));
    }
    
    /**
     * Returns a {@link PlanetKeplerian} representation of Earth at a given time,
     * using fixed orbital elements.
     *
     * @param time The observation time.
     * @return The {@link PlanetKeplerian} object for Earth.
     */
    private PlanetKeplerian getEarth(Instant time){
        double semiMajorAxis = 1.482723189000168E+08 * 1000.0;
        double e = 1.293398280839581E-02;
        double inclination = Math.toRadians(7.530442636380576E-03);
        double argPerihelion = Math.toRadians(6.804721922709237E+01);
        double longitudeNode = Math.toRadians(6.787791105814221E+00);
        double meanAnomaly = Math.toRadians(2.587134472915172E+01);
        return new PlanetKeplerian(semiMajorAxis, e, inclination,
                argPerihelion, longitudeNode, meanAnomaly, getSecondsFromEpoch(time));
    }
    /**
     * Parses command-line arguments into an {@link ArgumentParser} object.
     *
     * @param args The command-line arguments.
     * @return A new {@link ArgumentParser} instance containing parsed arguments.
     */
    private void getArguments(String[] args){
        arguments = new ArgumentParser(args);
    }
    
    /**
     * Constructs a new ResultData object and parses its arguments.
     *
     * @param args The command-line arguments.
     */
    public ResultData(String [] args){
        getArguments(args);
    }
    
    private boolean dataReady = false;
    
    private double earthDistanceToSun;
    private double planetDistanceToSun;
    private double planetRA;
    private double planetDec;
    private double planetAzimuth;
    private double planetAltitude;
    private double planetRise;
    private double planetSet;
    private double planetTransit;
    
    /**
     * Executes the full computation pipeline: fetches planet data, calculates
     * heliocentric positions, transforms to RA/Dec and optionally to Alt/Az.
     *
     * @throws Exception if time or body is not provided, or the planet data cannot be fetched.
     */
    public void calculate() throws Exception{
        if (!arguments.isBodyAvailable() || !arguments.isTimeAvailable()){
            throw new Exception("The name of the body or time must be specified");
        }
        PlanetKeplerian earth = getEarth(arguments.getTime());
        PlanetKeplerian planet = getPlanet(arguments.getTime(), arguments.getBody());
        PlanetHeliocentric planetHel = KeplerianCalculator.computePosition(planet);
        PlanetHeliocentric earthHel = KeplerianCalculator.computePosition(earth);
        
        // get the values from heliocentric coordinates
        planetDistanceToSun = planetHel.distanceFromSun();
        earthDistanceToSun = earthHel.distanceFromSun();
        PlanetRADec planetRADec = earthHel.toRADec(planetHel);
        planetRA = planetRADec.RA();
        planetDec = planetRADec.declination();
        
        if (arguments.isLatitudeAvailable() && arguments.isLongitudeAvailable()){
            double [] altAz = planetRADec.toAltitudeAzimuth(arguments.getTime(), arguments.getLatitude(), arguments.getLongitude());
            planetAltitude = altAz[0];
            planetAzimuth = altAz[1];
            double[] riseSetTransit = planetRADec.estimateRiseSetTimes(arguments.getTime(), arguments.getLatitude());
            planetRise = riseSetTransit[0];
            planetSet = riseSetTransit[1];
            planetTransit = riseSetTransit[2];
        }
        dataReady = true;
    }
    
    
    /**
     * Formats a double representing hours into HH:mm format.
     *
     * @param hours The value in hours.
     * @return Formatted string in HH:mm.
     */
    private String toHourMinutes(double hours) {
        int h = (int) hours;
        int m = (int) ((hours - h) * 60);
        return String.format("%02d:%02d", h, m);
    }
    
    /**
     * Formats Right Ascension in a compact 00h00min format.
     *
     * @param RA The RA in hours.
     * @return Formatted RA string.
     */
    private String toRAFormat(double RA){
        int h = (int) RA;
        int m = (int) ((RA - h) * 60);
        return String.format("%02dh%02dmin", h, m);
    }
    
    /**
     * Formats all collected data into a human-readable string, depending on available arguments.
     *
     * @return The string representation of the planetary data, or null if data is not ready.
     */
    @Override
    public String toString(){
        if (!dataReady){
            System.err.println("You must first call calculate() before printing data");
            return null;
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append("##### INFO #####").append(System.lineSeparator());
        builder.append("Object ID: ").append(arguments.getBody()).append(System.lineSeparator());
        builder.append("Time: ").append(arguments.getTime()).append(System.lineSeparator());
        if (arguments.isLatitudeAvailable() && arguments.isLongitudeAvailable()){
            String position = String.format(
                        "Latitude: %.2f°" + System.lineSeparator() +
                        "Longitude: %.2f°" + System.lineSeparator(),
                        arguments.getLatitude(),
                        arguments.getLongitude()
                        );
            builder.append(position);
            
        }
        
        builder.append("##### DATA #####").append(System.lineSeparator());
        
        // Date creation
        String basicData = String.format(
                        "Earth–Sun Distance: %.5E m" + System.lineSeparator() +
                        "Planet–Sun Distance: %.5E m" + System.lineSeparator() +
                        "RA: %s" + System.lineSeparator() +
                        "Dec: %.2f°" + System.lineSeparator(),
                        earthDistanceToSun,
                        planetDistanceToSun,
                        toRAFormat(planetRA),
                        planetDec
                    );
        builder.append(basicData);
        
        if (arguments.isLatitudeAvailable() && arguments.isLongitudeAvailable()){
            String advancedData = String.format(
                        "Azimuth: %.2f°" + System.lineSeparator() +
                        "Altitude: %.2f°" + System.lineSeparator() +
                        "Rise Time: %s h" + System.lineSeparator() +
                        "Set Time: %s h" + System.lineSeparator() +
                        "Transit Time: %s h",
                        planetAzimuth,
                        planetAltitude,
                        toHourMinutes(planetRise),
                        toHourMinutes(planetSet),
                        toHourMinutes(planetTransit)
                        );
            builder.append(advancedData);
        }
        
        return builder.toString();
    }
    
    /**
     * Prints or writes the formatted output to a file, depending on arguments.
     *
     * @throws Exception if writing to file fails.
     */
    public void print() throws Exception{
        String data = this.toString();
        if (arguments.isFileAvailable()){
            Path filePath = Path.of(arguments.getFile());
            Files.writeString(filePath, data);
            return;
        }
        System.out.println(data);
        
    }
}
