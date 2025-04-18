package cz.cuni.mff.balekda.planetSimulator;

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
}