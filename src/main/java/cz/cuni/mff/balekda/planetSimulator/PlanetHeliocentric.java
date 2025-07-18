package cz.cuni.mff.balekda.planetSimulator;


/**
 * Represents a heliocentric position vector (x, y, z) of a celestial body in the Solar System.
 * 
 * Coordinates are given in meters and refer to the position of a planet or other object relative to the Sun,
 * in a right-handed Cartesian coordinate system aligned with the ecliptic, where x axis points to vernal equinox
 * This record is commonly used for position computation of planets with respect to the Sun.
 * @param x X-coordinate (m), aligned with the vernal equinox direction.
 * @param y Y-coordinate (m).
 * @param z Z-coordinate (m), positive above the ecliptic plane.
 * 
 * @author David Balek
 */
public record PlanetHeliocentric(
    double x,
    double y,
    double z
) {

    private static final double EARTH_OBLIQUITY = Math.toRadians(23.43928); // in radians

    /**
     * Computes the distance from the Sun to this body.
     *
     * @return Distance in meters.
     */
    public double distanceFromSun() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Subtracts this vector (presumably Earth) from another position vector 
     * to get a relative (presumably geocentric) vector.
     *
     * @param other The other PlanetHeliocentric object to subtract from.
     * @return Relative position vector.
     */
    private PlanetHeliocentric subtractFrom(PlanetHeliocentric other) {
        return new PlanetHeliocentric(other.x - this.x , other.y - this.y, other.z - this.z);
    }

    /**
     * Converts the heliocentric position of PlanetHeliocentric other
     * to equatorial coordinates of this object (presumably Earth):
     * Right Ascension (RA) and Declination (Dec).
     * @param other The reference planet whose heliocentric coordinates are used
     * to calculate relative position.
     * @return A PlanetRADec object with RA in hours and Dec in degrees.
     */
    public PlanetRADec toRADec(PlanetHeliocentric other) {
        
        PlanetHeliocentric relative = this.subtractFrom(other);        
        double xRel = relative.x;
        double yRel = relative.y;
        double zRel = relative.z;
        
        
        // Rotate around x-axis by obliquity to get equatorial coordinates
        double xe = xRel;
        double ye = yRel * Math.cos(EARTH_OBLIQUITY) - zRel * Math.sin(EARTH_OBLIQUITY);
        double ze = yRel * Math.sin(EARTH_OBLIQUITY) + zRel * Math.cos(EARTH_OBLIQUITY);

        double r = relative.distanceFromSun();

        // RA in radians
        double RA = Math.atan2(ye, xe);
        // Normalizzation to [0, 2 PI)
        if (RA < 0) RA += 2 * Math.PI;
        RA = Math.toDegrees(RA) / 15.0;
        double dec = Math.asin(ze / r);
        dec = Math.toDegrees(dec);

        return new PlanetRADec(normalizeHours(RA), dec);
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
     * Returns a 3-element array [x, y, z].
     * 
     * @return A double array of size 3 representing the [x, y, z] heliocentric coordinates.
     */
    public double[] toArray() {
        return new double[] { x, y, z };
    }

    /**
     * String representation of heliocentric coordinates.
     * 
     * @return what it says it returns :)
     */
    @Override
    public String toString() {
        return String.format("Heliocentric Coordinates: x=%.6f m, y=%.6f m, z=%.6f m", x, y, z);
    }
}
