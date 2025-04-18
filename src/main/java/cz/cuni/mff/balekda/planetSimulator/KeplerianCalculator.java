package cz.cuni.mff.balekda.planetSimulator;

/**
 * 
 * @author David Balek
 */

/**
 * A simulator for computing the position of a celestial body in orbit around the Sun
 * using Keplerian orbital elements and Kepler's laws.
 */
public class KeplerianCalculator {
    /** 
     * Gravitational parameter of the Sun (GM) in m^3/s^2 
     *  @see <a href="https://iau-a3.gitlab.io/NSFA/NSFA_cbe.html#GMS2012">IAU</a>
     */
    private static final double SUN_GRAVITY = 1.327_124_400_41e20;
    
    /**
     * Solves Kepler's Equation for the Eccentric Anomaly using Newton's method.
     *
     * @param M Mean anomaly (radians)
     * @param e Eccentricity
     * @return Eccentric anomaly (radians)
     * @see <a href="https://en.wikipedia.org/wiki/Kepler's_equation#Newton's_method">Kepler's equation</a>
     */
    private static double solveKeplersEquation(double M, double e) {
        double E = M + e*Math.sin(M);
        double epsilon = 1e-6;
        for (int i = 0; i < 1000; i++) {
            double deltaM = M - E + e * Math.sin(E);
            double deltaE = deltaM / (1 - e * Math.cos(E));
            if (Math.abs(deltaE) < epsilon) {
                break;
            }
            E += deltaE;
        }
        return E;
    }
    
    /**
     * Computes the 3D position of a celestial body in its orbit after time T. 
     * 
     * @param a Semi-major axis (m)
     * @param e Eccentricity
     * @param i Inclination (radians)
     * @param omega Argument of perihelion (radians)
     * @param Omega Longitude of ascending node (radians)
     * @param MEpoch Mean anomaly at epoch (radians)
     * @param T Time after epoch (seconds)
     * @return Position as a 3-element array [x, y, z] in m
     * @see <a href="https://ssd.jpl.nasa.gov/planets/approx_pos.html">Approximate Positions of the Planets</a>
     */
    public double[] computePosition(double a, double e, double i, double omega, double Omega, double MEpoch, double T) {
        // Mean motion -- we have meanMotion = 2*PI / T, which from 3rd Kepler's law  is sqrt(GM/a^3)
        double meanMotion = Math.sqrt(SUN_GRAVITY / Math.pow(a, 3)); 
        double M = MEpoch + meanMotion * T;
        
        // Solve Kepler's Equation for Eccentric Anomaly
        double E = solveKeplersEquation(M, e);
        double xOrbital = a * (Math.cos(E) - e);
        double yOrbital = a * (Math.sin(E) * Math.sqrt((1-e*e)));

        // Now to the calculation of ecliptical cartesian coordinates
        double cosOmega = Math.cos(Omega);
        double sinOmega = Math.sin(Omega);
        double cosI = Math.cos(i);
        double sinI = Math.sin(i);
        double cosomega = Math.cos(omega);
        double sinomega = Math.sin(omega);

        double x = (cosomega * cosOmega - sinomega * sinOmega * cosI) * xOrbital + 
                   (-sinomega * cosOmega - cosomega * sinOmega * cosI) * yOrbital;
        double y = (cosomega * cosOmega + cosOmega * sinomega * cosI) * xOrbital + 
                   (-sinomega * sinOmega + cosomega * cosOmega * cosI) * yOrbital;
        double z = (sinomega * sinI) * xOrbital + 
                   (cosomega * sinI) * yOrbital;

        return new double[]{x, y, z};
    }
    
    /**
     * Computes the 3D position of a celestial body in its orbit after time T.
     * @param planet The composing object of all needed Keplerian parameters
     * @return Position as a 3-element array [x, y, z] in m
     */
    public double[] computePosition(PlanetKeplerian planet){
        return computePosition(
                planet.semiMajorAxis(), 
                planet.eccentricity(),
                planet.inclination(),
                planet.argumentPerihelion(),
                planet.longitudeNode(),
                planet.meanAnomaly(), 
                planet.time());
    }
}