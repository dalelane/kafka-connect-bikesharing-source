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

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains an imaginary record of bikes that are available for a
 *  journey.
 *
 * The manager trusts that all checked out bikes will be checked in
 *  again, so does not maintain a record of bikes that are checked
 *  out.
 */
public class BikeManager {

    private static final List<String> availableBikes = new ArrayList<>();

    public final static int NUM_BIKES = 1000;
    private final static int INITIAL_BIKES_POOL = 250;
    private final static int EMPTY_BIKES_POOL = 25;

    static {
        // create initial set of bikes available for journeys
        for (int i = 0; i < INITIAL_BIKES_POOL; i++) {
            availableBikes.add(Generators.randomStringCode(NUM_BIKES));
        }
    }

    /**
     * Removes a bike from the pool of available bikes.
     *
     * A bike should always be available, so if the current pool of
     *  bikes have all been checked out, an additional bike is
     *  added to the pool and then checked out.
     */
    public static synchronized String checkout() {
        String selectedUUID = null;

        if (availableBikes.size() < EMPTY_BIKES_POOL) {
            // we need an additional bike
            selectedUUID = Generators.randomStringCode(NUM_BIKES);
            return selectedUUID;
        }

        return availableBikes.remove(Generators.randomInt(0, availableBikes.size()));
    }

    /**
     * Returns a previously checked-out bike to the pool.
     */
    public static synchronized void checkin(String bikeid) {
        availableBikes.add(bikeid);
    }
}