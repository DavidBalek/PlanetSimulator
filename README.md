# Planet Simulator

## Getting started
After cloning this repository and compiling with maven, the program is run as follows:
```
mvn exec:java -Dexec.args="--time 2025-04-20T23:00:00Z --body \"mars barycenter\" --latitude 49.7 --longitude 14.0 --file output_mars.txt"
```
All possible parameters can be displayed by calling:
```
mvn exec:java -Dexec.args="--help"
```
* The arguments must be separated by a space, so in case of a multiword parameter use the escaped quotation mark (\\"), e.g: `--body \"mars barycenter\"`
* The values must not start by minus, so in case of needing to use negative value surround it with escaped quotation mark (\\"), e.g: `--latitude \" -30.0 \"`

To generate Javadoc documentation, run:
```
mvn javadoc:javadoc
```
the generated Javadoc can be found then in `target/reports/apidocs/index.html`. 

## Features
* Convert Keplerian obrital elements to RA/Dec
* Compute altitude and azimuth for any observer on Earth
* Estimate rise, set, and transit times
* Communication with [Horizons System](https://ssd.jpl.nasa.gov/horizons/), NASA database of (not only) Keplerian obrital elements
* Supports multiple celestial bodies (by ID or name as by Horizons System)
* Flag-based input parsing (`--time`, `--body`, etc.)
* Unit tested with JUnit 5
* Javadoc-generated API docs
  
## Parameters
| Flag           | Alias | Description                                                                                                                                       | Required |
|----------------|-------|---------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| `--time`       | `-t`  | ISO 8601 date/time in UTC (e.g `2025-04-20T23:00:00Z`) - the time of observation                                                                  | ✅       |
| `--body`       | `-b`  | Integer ID of the planet (e.g. `499` or `apophis`) - see the [Horizon's documentation](https://ssd.jpl.nasa.gov/horizons/manual.html#select)      | ✅       |
| `--latitude`   | `-p`  | Observer's latitude in degrees (e.g `49.70`)                                                                                                      | ❌       |
| `--longitude`  | `-l`  | Observer's longitude in degrees (e.g `14.00`) - the east is positive                                                                              | ❌       |
| `--file`       | `-f`  | Output filename, if missing the data printed to stdout                                                                                            | ❌       |
| `--help`       |       | Show usage instructions                                                                                                                           | ❌       |

## Functionality
This project provides a **simulation** of Solar system bodies. The core functionality principles are:

### 1. Astronomical Calculations:
- **Heliocentric Coordinates**: From Horizon's system gets Keplerian orbital elements, then converts them using Kepler's Equation to positions in heliocentric cartesian coordinates at specified time.
- **Equatorial Coordiantes** From them it converts them to equatorial coordinates (RA/Dec).
- **Altitude and Azimuth**: If position of the observer was provided, calculates the position of a planet in the sky (altitude and azimuth) based o the observer's location and the current time.
- **Rise, Transit and Set**: If position of the observer was provided, estimates the times of rise (object getting over horizon), transit (object culminating) and its set (object getting below horizon).
The calculated times in local mean solar time - to get the UTC, Solar Equation must be subtracted and timezone shift as well. 

### 2. Horizon's API:
- **Ephemeris**: The program uses the [Horizon's API](https://ssd-api.jpl.nasa.gov/doc/horizons.html) to get all needed ephemeris (Keplerian orbital elements) at the time `2025-01-01 12:00:00`. From these information it proceeds with the Keplerian movement simulation.
- **Cheating?**: Of course the call to Horizon's API could be exploited to get all the data needed, but I promise we only get the data mentioned above.

### 3. Time:
- **Julian Date Conversion**: Converts between **Instant** objects (ISO 8601 format) and **Julian Date**, which is commonly used in astronomical computations.
- **Local Sidereal Time**: Calculates the local sidereal time for a specific location, which is needed for Altitude and Azimuth calculations

### 4. Command-Line Interface:
- **Argument Parsing**: Allows users to input specific parameters mentioned above.

### 5. Error Handling:
- **User input**: Ensures that input values like time, body ID, and geographic coordinates are valid, with appropriate error messages for invalid inputs.
- **Horizon's output**: In case of a problem with communication with Horizon's system (typically wrong body ID), it provides the response from the system.
