package cz.cuni.mff.balekda.planetSimulator;

import java.io.IOException;


 /**
  * Entry point for the planetary simulation program.
  * Parses arguments, performs orbital calculations, and outputs the result.
  * 
  * This class creates a {@code ResultData} instance, computes results, and
  * prints them either to standard output or a file depending on user input.
  * 
  * @author David Balek
  */
public class PlanetSimulator {
    
    
    /**
     * Default constructor for the PlanetSimulator class.
     * Initializes the class with the default behavior.
     */
    public PlanetSimulator() {}
    
    /**
     * Main method that drives the planetary simulation.
     *
     * Parses command-line arguments, calculates the planetary positions,
     * and prints or writes the results.
     *
     * @param args Command-line arguments used to configure the simulation.
     */
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
