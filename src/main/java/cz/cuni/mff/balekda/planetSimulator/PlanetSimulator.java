package cz.cuni.mff.balekda.planetSimulator;

import java.io.IOException;

/**
 *
 * @author David Balek
 */
public class PlanetSimulator {
    

    public static void main(String[] args) {
        try {
            ResultData results = new ResultData(args);
            results.calculate();
            results.print();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
