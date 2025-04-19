package cz.cuni.mff.balekda.planetSimulator;

import java.time.Instant;

/**
 *
 * @author David Balek
 */
public class PlanetSimulator {

    public static void main(String[] args) {
        double semiMajorAxis = 2.267644626202132E+08 * 1000.0;
        double e = 9.881083678295187E-02;
        double inclination = Math.toRadians(1.852051255467564E+00);
        double argPerihelion = Math.toRadians(2.865716456309895E+02);
        double longitudeNode = Math.toRadians(4.941370856451925E+01);
        double meanAnomaly = Math.toRadians(1.247336684829769E+02);
//        double nowJD = TimeConverter.toJulianDate(Instant.now());
//        double epochJD = TimeConverter.getJ2000Epoch();
//        double diffSeconds = (nowJD - epochJD) * 86400;
        double days = 0.0;
        double diffSeconds = days * 86400;
        PlanetKeplerian mars = new PlanetKeplerian(semiMajorAxis, e, inclination,
                argPerihelion, longitudeNode, meanAnomaly, diffSeconds);
        
        semiMajorAxis = 1.482723189000168E+08 * 1000.0;
        e = 1.293398280839581E-02;
        inclination = Math.toRadians(7.530442636380576E-03);
        argPerihelion = Math.toRadians(6.804721922709237E+01);
        longitudeNode = Math.toRadians(6.787791105814221E+00);
        meanAnomaly = Math.toRadians(2.587134472915172E+01);
        PlanetKeplerian earth = new PlanetKeplerian(semiMajorAxis, e, inclination,
                argPerihelion, longitudeNode, meanAnomaly, diffSeconds);
        
        PlanetHeliocentric marsHel = KeplerianCalculator.computePosition(mars);
        PlanetHeliocentric earthHel = KeplerianCalculator.computePosition(earth);
        
        ////////////////////////////////////
        System.out.println("Mars distance to Sun: " + marsHel.distanceFromSun());
        System.out.println("Earth distance to Sun: " + earthHel.distanceFromSun());
        System.out.println("Sun long: " + Math.atan2(-earthHel.y(), -earthHel.x()) / Math.PI * 180.0);
        
        ///////////////////////
        PlanetRADec marsRADec = earthHel.toRADec(marsHel);
        System.out.println(marsRADec);
        Instant time = TimeConverter.fromJulianDate(2460677 + days);
        double [] azAlt = marsRADec.toAltitudeAzimuth(time, 49.5, 14.5);
        System.out.println("Azimuth = " + azAlt[1] + " altitude = " + azAlt[0]);
        double[] riseSet = marsRADec.estimateRiseSetTimes(time, 49.5);
        System.out.println("Sunrise = " + riseSet[0] + " sunset = " + riseSet[1] + " transit = " + riseSet[2]);
    }
}
