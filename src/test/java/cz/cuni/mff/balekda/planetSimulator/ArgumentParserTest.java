package cz.cuni.mff.balekda.planetSimulator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ArgumentParser class.
 */
public class ArgumentParserTest {
    /**
     * Tests that a valid ISO 8601 time argument is correctly parsed.
     */
    @Test
    public void testValidTimeArgument() {
        String[] args = {"--time", "2025-04-20T10:00:00Z", "--body", "499"};
        ArgumentParser parser = new ArgumentParser(args);
        
        assertTrue(parser.isTimeAvailable());
        assertEquals("2025-04-20T10:00:00Z", parser.getTime().toString());
    }
    
    /**
     * Tests that an invalid time argument (not in ISO 8601 format) results in an exception.
     */
    @Test
    public void testInvalidTimeArgument() {
        try {
           String[] args = {"--time", "invalid-time", "--body", "499"}; 
           ArgumentParser parser = new ArgumentParser(args);
        }
        catch(IllegalArgumentException ex){
            assertTrue(ex.getMessage().contains("Invalid ISO time format"));
        }     
       
    }
    
    /**
     * Tests that the parser throws an exception when the required --body argument is missing.
     */
    @Test
    public void testMissingBodyArgument() {
        try {
           String[] args = {"--time", "2025-04-20T10:00:00Z"}; 
           ArgumentParser parser = new ArgumentParser(args);
        }
        catch(IllegalArgumentException ex){
            assertTrue(ex.getMessage().contains("Missing required argument: --body"));
        } 
        
    }
    
    
    /**
     * Tests that the parser correctly handles a valid latitude argument.
     */
    @Test
    public void testLatitudeArgument() {
        String[] args = {"--time", "2025-04-18T10:00:00Z", "--body", "499", "--latitude", "45.0"};
        ArgumentParser parser = new ArgumentParser(args);

        assertTrue(parser.isLatitudeAvailable());
        assertEquals(45.0, parser.getLatitude());
    }
    
    /**
     * Tests that the parser throws an exception when an invalid latitude value (outside the range [-90, 90]) is provided.
     */
    @Test
    public void testInvalidLatitudeArgument() {
        try {
           String[] args = {"--time", "2025-04-18T10:00:00Z", "--body", "499", "--latitude", "100.0"};
           ArgumentParser parser = new ArgumentParser(args);
        }
        catch(IllegalArgumentException ex){
            assertTrue(ex.getMessage().contains("Latitude must be between -90 and 90"));
        }
    }
    
    /**
     * Tests that the parser knows that all arguments were provided 
     */
    @Test
    public void testProvidedArgument() {
        String[] args = {"--time", "2025-04-18T10:00:00Z", "--body", "499", "--latitude", "45.0", "--longitude", "45.0", "--file", "a.txt"};
        ArgumentParser parser = new ArgumentParser(args);
        assertTrue(parser.isTimeAvailable());
        assertTrue(parser.isBodyAvailable());
        assertTrue(parser.isLatitudeAvailable());
        assertTrue(parser.isLongitudeAvailable());
        assertTrue(parser.isFileAvailable());
    }
    
    /**
     * Tests that the parser knows that only the mandatory arguments were provided
     */
    @Test
    public void testMissingArgument() {
        String[] args = {"--time", "2025-04-18T10:00:00Z", "--body", "499"};
        ArgumentParser parser = new ArgumentParser(args);
        assertTrue(parser.isTimeAvailable());
        assertTrue(parser.isBodyAvailable());
        assertFalse(parser.isLatitudeAvailable());
        assertFalse(parser.isLongitudeAvailable());
        assertFalse(parser.isFileAvailable());
    }
}