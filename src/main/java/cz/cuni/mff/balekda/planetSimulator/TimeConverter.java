package cz.cuni.mff.balekda.planetSimulator;

import java.time.Instant;

/**
 *
 * @author David Balek
 */

/**
 * Utility class for converting between UTC time, Julian Date and Earth's Rotation Angle (ERA).
 * 
 * Julian Date (JD) is the continuous count of days since noon UTC on January 1, 4713 BCE.
 * This class uses the standard of Julian Date 2000, which counts the number of dates since noon UTC Janaury 1, 2000 AD.
 * ERA is the angle from Celestial Intermediate Origin changing over time due to Earth's rotation
 * 
 */
public class TimeConverter {
    
    // start of MJD_2000 epoch
    private static final Instant JD_2000 = Instant.parse("2000-01-01T12:00:00.00Z");
    // JD at MJD_2000 time 0
    public static final double JD_2000_IN_JD = 2451545.0; 
    private static final double SECONDS_PER_DAY = 86400.0;

    /**
     * Converts an Instant (UTC) to Julian Date.
     * @param instant the time in UTC
     * @return same time in JD
     */
    public static double toJulianDate(Instant instant) {
        long epochSecondsInstant = instant.getEpochSecond();
        long epochSecondsJD2000 = JD_2000.getEpochSecond();
        long diffSeconds = epochSecondsInstant - epochSecondsJD2000;
        
        int nanoInstant = instant.getNano();
        int nanoMJD2000 = JD_2000.getNano();
        int diffNanos = nanoInstant - nanoMJD2000;
        
        double totalDiff = diffSeconds + diffNanos / 1e9;
        return JD_2000_IN_JD + totalDiff / SECONDS_PER_DAY;
    }
    
    public static double getJ2000Epoch(){
        return JD_2000_IN_JD;
    }    

    /**
     * Converts a Julian Date to Instant (UTC).
     * @param julianDate the Julian Date to convert
     * @return the corresponding UTC instant
     */
    public static Instant fromJulianDate(double julianDate) {
        double daysSinceMJD2000Epoch = julianDate - JD_2000_IN_JD;
        
        long epochSecondsMJD2000 = JD_2000.getEpochSecond();
        long seconds = (long) ( daysSinceMJD2000Epoch * SECONDS_PER_DAY + epochSecondsMJD2000 );
        
        int nanoMJD2000 = JD_2000.getNano();
        long nanos = (long) ((daysSinceMJD2000Epoch % 1.0) * 1e9 + nanoMJD2000);
        return Instant.ofEpochSecond(seconds, nanos);
    }
    /**
     * Computes the Earth Rotation Angle (ERA) in radians for a given Julian Date.
     * Formula based on IAU 2000 standards. 
     *
     * @param julianDate the Julian Date
     * @return ERA in radians, normalized to [0, 2 PI)
     * @see <a href="https://en.wikipedia.org/wiki/Sidereal_time#Earth_rotation_angle">Earth's rotation angle</a>
     */
    public static double computeERA(double julianDate) {
        double delta = julianDate - JD_2000_IN_JD;
        double theta = 2 * Math.PI * (0.7790572732640 + 1.00273781191135448 * delta);
        return normalizeRadians(theta);
    }

    /**
     * Normalizes an angle in radians to the range [0, 2 PI).
     *
     * @param angle the angle in radians
     * @return the normalized angle
     */
    private static double normalizeRadians(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }
}