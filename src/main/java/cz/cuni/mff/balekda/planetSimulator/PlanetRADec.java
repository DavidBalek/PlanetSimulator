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
        double HA = LST - RA * 15; 
        if (HA < -180) HA += 360;
        if (HA > 180) HA -= 360;

        double haRad = Math.toRadians(HA);
        double decRad = Math.toRadians(declination);
        double latRad = Math.toRadians(latitude);

        double sinAlt = Math.sin(decRad) * Math.sin(latRad) + Math.cos(decRad) * Math.cos(latRad) * Math.cos(haRad);
        double altitude = Math.toDegrees(Math.asin(sinAlt));
        double numAz = - Math.sin(HA);
        double denomAz = Math.tan(decRad) * Math.cos(latRad) - Math.sin(latRad) * Math.cos(HA);
        double azimuth = Math.atan2(numAz, denomAz);
        azimuth = Math.toDegrees(azimuth);

        return new double[] { altitude, azimuth };
    }

    /**
     * Estimates UTC sunrise and sunset times for the current declination and given observer position.
     *
     * @param date Date of observation (UTC).
     * @param latitude Observer's latitude in degrees.
     * @param longitude Observer's longitude in degrees (East positive).
     * @return Array of two Instant values: [sunrise, sunset] in UTC, 
     * [null, null] in case the object is always above / below horizon throughout the day.
     */
    public Instant[] estimateSunriseSunset(Instant date, double latitude, double longitude) {
        double decRad = Math.toRadians(declination);
        double latRad = Math.toRadians(latitude);

        // Solar elevation at sunrise/sunset is -0.833 degrees (standard atmospheric refraction)
        double h0 = Math.toRadians(-0.833);
        double cosH = (Math.sin(h0) - Math.sin(latRad) * Math.sin(decRad)) /
                      (Math.cos(latRad) * Math.cos(decRad));

        if (cosH < -1 || cosH > 1) {
            // Sun is either always above or below the horizon
            return new Instant[] { null, null };
        }

        double H = Math.toDegrees(Math.acos(cosH)); // hour angle in degrees
        double RA_deg = RA * 15; // Convert RA from hours to degrees

        // Estimate solar transit (local noon) in hours
        double solarNoonHours = ((RA_deg - longitude + 360) % 360) / 15.0;

        // Truncate date to UTC midnight
        Instant base = date.truncatedTo(ChronoUnit.DAYS);
        Instant solarNoon = base.plusSeconds((long)(solarNoonHours * 3600));

        // Time from noon to rise/set in hours
        double deltaT = H / 15.0;

        Instant sunrise = solarNoon.minusSeconds((long)(deltaT * 3600));
        Instant sunset = solarNoon.plusSeconds((long)(deltaT * 3600));

        return new Instant[] { sunrise, sunset };
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