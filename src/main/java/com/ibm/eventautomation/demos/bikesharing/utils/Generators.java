/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibm.eventautomation.demos.bikesharing.utils;

import java.util.Random;

import com.ibm.eventautomation.demos.bikesharing.data.Location;

public class Generators {

    private final static Random RNG = new Random();

    // -------------------------------------------------------------------
    //   generic random data generators
    // -------------------------------------------------------------------

    public static int randomInt(int min, int max) {
        return RNG.nextInt(min, max);
    }

    public static double randomDouble(double min, double max) {
        double randomValue = min + (max - min) * RNG.nextDouble();
        return Math.round(randomValue * 100000.0) / 100000.0;
    }

    private static final String ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Return a random code made up of characters from
     *  the ALPHANUMERICS constant.
     *
     * The length of the code should be sufficient to enable a range of
     *  possible codes that is at least as big as possibleStringsSetSize
     */
    public static String randomStringCode(int possibleStringsSetSize) {
        int stringIdx = randomInt(0, possibleStringsSetSize);
        StringBuilder result = new StringBuilder();
        do {
            result.insert(0, ALPHANUMERICS.charAt(stringIdx % ALPHANUMERICS.length()));
            stringIdx /= ALPHANUMERICS.length();
        } while (stringIdx > 0);
        return result.toString();
    }

    // -------------------------------------------------------------------
    //   contrived random data generators!
    // -------------------------------------------------------------------

    /** Returns a random lat,lon location in London */
    public static Location randomLocation() {
        return new Location(randomDouble(51.450, 51.542),
                            randomDouble(-0.226, -0.068));
    }

    /** Returns a random location that is close to the provided location. */
    public static Location randomLocation(Location startingLocation) {
        return new Location(startingLocation.getLatitude() + randomDouble(-0.003, 0.003),
                            startingLocation.getLongitude() + randomDouble(-0.006, 0.006));
    }

    /** Returns a random battery level that is a little lower than the provided level. */
    public static Integer randomBatteryLevel(Integer startingBatteryLevel) {
        // simulating a small battery drain (between 1 and 6 percent)
        //  that will be reported between location updates for an e-bike that is
        //  currently on the move
        final int SMALLEST_BATTERY_DRAIN = 1;
        final int HIGHEST_BATTERY_DRAIN = 6;

        // want to avoid reporting a completely flat battery, so we will stop
        //  reducing the battery once it is at 8%
        final int LOWEST_BATTERY_LIMIT = 8;

        final int batteryDrain = Generators.randomInt(
            Math.min(SMALLEST_BATTERY_DRAIN, startingBatteryLevel - LOWEST_BATTERY_LIMIT),
            Math.min(HIGHEST_BATTERY_DRAIN,  startingBatteryLevel - LOWEST_BATTERY_LIMIT + 1)
        );

        return startingBatteryLevel - batteryDrain;
    }
}
