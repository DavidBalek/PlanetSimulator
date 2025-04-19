package cz.cuni.mff.balekda.planetSimulator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author David Balek
 */


/**
 * Represents the geocentric equatorial coordinates of a celestial object.
 *
 * These values are commonly used in observational astronomy to locate
 * planets in the sky, beacause 
 * they are independent of position of observer on Earth.
 *
 * @param RA Right Ascension (hours), range: [0, 24). This is the celestial equivalent of longitude.
 * @param declination Declination (degrees), range: [-90, +90]. This is the celestial equivalent of latitude.
 *
 */
public record PlanetRADec(
    double RA,
    double declination
) {

    /**
     * Returns a formatted string with Right Ascension and Declination.
     *
     * @return what it says it returns :)
     */
    @Override
    public String toString() {
        return String.format("RA = %.3f h, Dec = %.3f°", RA, declination);
    }

    /**
     * Checks if the RA and Dec values are within typical astronomical bounds.
     *
     * @return true if RA is in [0, 24) and Dec is in [-90, 90], false otherwise.
     */
    public boolean isValid() {
        return RA >= 0 && RA < 24 && declination >= -90 && declination <= 90;
    }
    
    /**
     * Computes the Altitude and Azimuth of the object for a given observer location and time.
     *
     * @param time UTC date and time
     * @param latitude Observer's latitude in degrees.
     * @param longitude Observer's longitude in degrees (East positive, West negative).
     * @return Array [altitude, azimuth] in degrees.
     * @see <a href="https://aa.usno.navy.mil/faq/alt_az">Computing altitude and azimuth</a>
     */
    public double[] toAltitudeAzimuth(Instant time, double latitude, double longitude) {
        // in degrees
        double LST = localSiderealTime(time, longitude);
        
        // Hour Angle (angle of a body from local meridian) in degrees 
        double HA = LST - RA * 15.0; 
        if (HA < -180) HA += 360;
        if (HA > 180) HA -= 360;

        double haRad = Math.toRadians(HA);
        double decRad = Math.toRadians(declination);
        double latRad = Math.toRadians(latitude);

        double sinAlt = Math.sin(decRad) * Math.sin(latRad) + Math.cos(decRad) * Math.cos(latRad) * Math.cos(haRad);
        double altitude = Math.toDegrees(Math.asin(sinAlt));
        double numAz = - Math.sin(haRad);
        double denomAz = Math.tan(decRad) * Math.cos(latRad) - Math.sin(latRad) * Math.cos(haRad);
        double azimuth =  Math.toDegrees(Math.atan2(numAz, denomAz)); 

        return new double[] { altitude, normalizeDegrees(azimuth) };
    }
    
    /**
     * Computes the Local Sidereal Time (LST) in degrees for a given UTC time and longitude.
     * Based on approximate formula valid for modern dates.
     *
     * @param utcTime UTC time
     * @param longitude Observer's longitude in degrees (East positive) in range [-180, 180]
     * @return Local Sidereal Time in degrees [0, 360)
     */
    private double localSiderealTime(Instant time, double longitude) {
        double julianDate = TimeConverter.toJulianDate(time);
        double ERA = TimeConverter.computeERA(julianDate);
        // ERA in degrees
        ERA = Math.toDegrees(ERA);
        
        double LST = ERA + longitude;

        return normalizeDegrees(LST);
    }
    
    /**
     * Estimates UTC rise and set times for the current declination and given observer position.
     *
     * @param date Date of observation (UTC).
     * @param latitude Observer's latitude in degrees.
     * @return Array of two Instant values: [rise, set, transit] in local solar time, NOT UTC, 
     * [null, null, null] in case the object is always above / below horizon throughout the day.
     */
    public double[] estimateRiseSetTimes(Instant date, double latitude) {
        double decRad = Math.toRadians(declination);
        double latRad = Math.toRadians(latitude);

        // Planet elevation at rise/set is -0.833 degrees (standard atmospheric refraction)
        double h0 = Math.toRadians(-0.833);
        double cosH = (Math.sin(h0) - Math.sin(latRad) * Math.sin(decRad)) /
                      (Math.cos(latRad) * Math.cos(decRad));

        if (cosH < -1 || cosH > 1) {
            // Planet is either always above or below the horizon
            return new double[] { 0,0,0 };
        }

        double H = Math.toDegrees(Math.acos(cosH)); // hour angle in degrees
        double planetRA_deg = RA * 15; // Convert RA from hours to degrees

        // Estimate planet transit (local noon) in hours
        double sunRA_deg = approximateSunRA(TimeConverter.toJulianDate(date)); // degrees
        
        double transit = 12.0 - (sunRA_deg - planetRA_deg) / 15.0;

        // Time from noon to rise/set in hours
        double deltaT = H / 15.0;

        double rise = transit - deltaT;
        double set = transit + deltaT;
        
        

        return new double[] { normalizeHours(rise), normalizeHours(set), normalizeHours(transit) };
    }

    
    /**
    * Approximates the Right Ascension (RA) of the Sun for a given Julian Date.
    * 
    * This method uses a simplified solar position model based on the Earth's orbital elements 
    * and provides sufficient accuracy for rise/set and transit time calculations in most applications.
    * 
    * The result is given in degrees in the equatorial coordinate system (RA), assuming mean obliquity.
    * 
    * @param julianDate Julian Date (e.g., from J2000.0 epoch: JD = 2451545.0)
    * @return Approximate Right Ascension of the Sun in degrees [0, 360)
    * 
    * @see <a href="https://en.wikipedia.org/wiki/Position_of_the_Sun">Wikipedia: Position of the Sun</a>
    * @see <a href="https://aa.usno.navy.mil/faq/sun_approx>Solar coordinates</a>
    */
    private double approximateSunRA(double julianDate) {
        // days since J2000
        double D = julianDate - TimeConverter.JD_2000_IN_JD;
        // mean anomaly of the Sun
        double g = Math.toRadians((357.529 + 0.98560028 * D) % 360);
        // Geocentric apparent ecliptic longitude of the Sun (adjusted for aberration):
        double lambda = Math.toRadians((280.459 + 0.98564736 * D + 1.915 * Math.sin(g) + 0.020 * Math.sin(2 * g)) % 360); // ecliptic longitude

        // obliquity of ecliptic
        double epsilon = Math.toRadians(23.439 - 0.00000036 * D); 
        double sunRA = Math.atan2(Math.cos(epsilon) * Math.sin(lambda), Math.cos(lambda));
        if (sunRA < 0) sunRA += 2 * Math.PI;
        return Math.toDegrees(sunRA);
    }
    
    /**
     * Normalizes time in hours to the range [0, 24).
     *
     * @param hours the angle in degrees
     * @return the normalized time
     */
    private double normalizeHours(double hours){
        hours = hours % 24.0;
        if (hours < 0) hours += 24.0;
        return hours;
    }
    
    
    /**
     * Normalizes an angle in degrees to the range [0, 360).
     *
     * @param angle the angle in degrees
     * @return the normalized angle
     */
    private double normalizeDegrees(double angle){
        angle = angle % 360.0;
        if (angle < 0) angle += 360.0;
        return angle;
    }
}