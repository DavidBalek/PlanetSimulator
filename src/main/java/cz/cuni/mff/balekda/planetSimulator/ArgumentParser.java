/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cuni.mff.balekda.planetSimulator;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author David Balek
 */


/**
 * Parses command-line flags for a celestial body tracking or simulation program.
 * <p>
 * Required flags:
 * <ul>
 *     <li>{@code --time} or {@code -t} — ISO 8601 formatted date/time (UTC) (e.g. 2025-04-18T10:00:00Z)</li>
 *     <li>{@code --body} or {@code -b} — Integer ID of the celestial body</li>
 * </ul>
 * Optional flags:
 * <ul>
 *     <li>{@code --latitude} or {@code -p} — Latitude in degrees [-90, 90]</li>
 *     <li>{@code --longitude} or {@code -l} — Longitude in degrees [-180, 180]</li>
 *     <li>{@code --file} or {@code -f} — Output file name</li>
 *     <li>{@code --help} — Displays usage info</li>
 * </ul>
 */
public class ArgumentParser {


    /** UTC date/time as ISO 8601 Instant. */
    private Instant time;
    
    /** Integer ID of the celestial body. */
    private int body;
    
    /** Optional latitude in degrees. */
    private double latitude;
    
    /** Optional longitude in degrees. */
    private double longitude;
    
    /** Optional output file name. */
    private String file;
    
    private boolean isTimeAvailable = false;
    private boolean isBodyAvailable = false;
    private boolean isLatitudeAvailable = false;
    private boolean isLongitudeAvailable = false;
    private boolean isFileAvailable = false;

    /**
     * Gets the UTC date/time as an {@link Instant}.
     * 
     * @return the UTC date/time as an {@link Instant}.
     * @throws IllegalStateException if the time has not been initialized
     */
    public Instant getTime() {
        if (!isTimeAvailable) {
            throw new IllegalStateException("Time has not been initialized.");
        }
        return time;
    }

    /**
     * Gets the integer ID of the celestial body.
     * 
     * @return the integer body ID.
     * @throws IllegalStateException if the body has not been initialized
     */
    public int getBody() {
        if (!isBodyAvailable){
            throw new IllegalStateException("ID of cellestial body has not been initialized.");
        }
        return body;
    }

    /**
     * Gets the latitude in degrees.
     * 
     * @return the latitude in degrees.
     * @throws IllegalStateException if the latitude has not been initialized
     */
    public double getLatitude() {
        if (!isLatitudeAvailable){
            throw new IllegalStateException("Latitude has not been initialized.");
        }
        return latitude;
    }

    /**
     * Gets the longitude in degrees.
     * 
     * @return the longitude in degrees.
     * @throws IllegalStateException if the longitude has not been initialized
     */
    public double getLongitude() {
        if (!isLongitudeAvailable){
            throw new IllegalStateException("Longitude has not been initialized.");
        }
        return longitude;
    }

    /**
     * Gets the optional output file name.
     * 
     * @return the output file name or {@code null} if not set.
     * @throws IllegalStateException if the file name has not been initialized
     */
    public String getFile() {
        if (!isFileAvailable){
            throw new IllegalStateException("Output file name has not initialized.");
        }
        return file;
    }
    
    /**
     * Parses the provided command-line arguments into a {@link ParsedArgs} object.
     *
     * @param args the command-line arguments
     * @throws IllegalArgumentException if required arguments are missing or malformed
     */
    public ArgumentParser(String[] args) {
        Map<String, String> argMap = new HashMap<>();

        // Parse key-value arguments
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--help".equals(arg)) {
                printHelp();
                System.exit(0);
            }

            if (arg.startsWith("-")) {
                if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                    throw new IllegalArgumentException("Missing value for " + arg);
                }
                argMap.put(arg, args[++i]);
            } else {
                throw new IllegalArgumentException("Unknown argument format: " + arg);
            }
        }

        // Parse --time or -t
        String timeStr = getArg(argMap, "--time", "-t", true);
        try {
            time = Instant.parse(timeStr);
            isTimeAvailable = true;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid ISO time format: " + timeStr);
        }

        // Parse --body or -b
        String bodyStr = getArg(argMap, "--body", "-b", true);
        try {
            body = Integer.parseInt(bodyStr);
            isBodyAvailable = true;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer for body: " + bodyStr);
        }

        // Parse optional --latitude or -p
        String latStr = getArg(argMap, "--latitude", "-p", false);
        if (latStr != null) {
            double lat = Double.parseDouble(latStr);
            if (lat < -90 || lat > 90)
                throw new IllegalArgumentException("Latitude must be between -90 and 90");
            latitude = lat;
            isLatitudeAvailable = true;
        }

        // Parse optional --longitude or -l
        String lonStr = getArg(argMap, "--longitude", "-l", false);
        if (lonStr != null) {
            double lon = Double.parseDouble(lonStr);
            if (lon < -180 || lon > 180)
                throw new IllegalArgumentException("Longitude must be between -180 and 180");
            longitude = lon;
            isLongitudeAvailable = true;
        }

        // Parse optional --file or -f
        file = getArg(argMap, "--file", "-f", false);
        isFileAvailable = true;

    }

    /**
     * Retrieves an argument's value from the map using either the long or short flag.
     *
     * @param map map of flags to values
     * @param longFlag the long form (e.g. {@code --time})
     * @param shortFlag the short form (e.g. {@code -t})
     * @param required whether the argument is required
     * @return the value as a string, or {@code null} if optional and not provided
     * @throws IllegalArgumentException if required and missing
     */
    private static String getArg(Map<String, String> map, String longFlag, String shortFlag, boolean required) {
        String value = map.getOrDefault(longFlag, map.get(shortFlag));
        if (value == null && required) {
            throw new IllegalArgumentException("Missing required argument: " + longFlag);
        }
        return value;
    }

    /**
     * Prints usage/help information for the command-line tool.
     */
    private static void printHelp() {
        System.out.println("Usage: java Program [options]");
        System.out.println("Required:");
        System.out.println("  -t, --time <ISO 8601 time>        UTC date/time (e.g. 2025-04-18T10:00:00Z)");
        System.out.println("  -b, --body <integer>              Celestial body ID");
        System.out.println("Optional:");
        System.out.println("  -p, --latitude <double>           Latitude in degrees (-90 to 90)");
        System.out.println("  -l, --longitude <double>          Longitude in degrees (-180 to 180)");
        System.out.println("  -f, --file <filename>             Output file name");
        System.out.println("  --help                            Show this help message");
    }
}
