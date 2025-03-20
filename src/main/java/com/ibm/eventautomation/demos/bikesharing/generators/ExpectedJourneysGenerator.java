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
package com.ibm.eventautomation.demos.bikesharing.generators;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.bikesharing.data.ExpectedJourney;
import com.ibm.eventautomation.demos.bikesharing.data.ExpectedJourneys;
import com.ibm.eventautomation.demos.bikesharing.data.HourData;
import com.ibm.eventautomation.demos.bikesharing.data.Location;
import com.ibm.eventautomation.demos.bikesharing.data.RiderType;
import com.ibm.eventautomation.demos.bikesharing.records.JourneyRecordGenerator;
import com.ibm.eventautomation.demos.bikesharing.utils.BikeManager;
import com.ibm.eventautomation.demos.bikesharing.utils.Generators;
import com.ibm.eventautomation.demos.bikesharing.utils.TimeCalculations;

/**
 * Generates imaginary bike journeys.
 *
 *  Details of this are not contained in the source CSV file used by the
 *  Generator superclass, so this class generates random journeys of random
 *  lengths in random locations - that should add up to the number of
 *  journeys that the CSV said occurred during this time period.
 *
 *  Events are emitted at:
 *   - start of a journey
 *   - random intervals during the journey
 *   - end of a journey
 */
public class ExpectedJourneysGenerator extends Generator<ExpectedJourneys> {

    private static final Logger log = LoggerFactory.getLogger(ExpectedJourneysGenerator.class);

    /** Minimum length of a journey. */
    private static final int TEN_MINUTES_IN_SECONDS = 10 * 60;

    /** Minimum time between journey updates. */
    private final static int MIN_JOURNEY_INTERVAL_SECS = 60;
    /** Maximum time between journey updates. */
    private final static int MAX_JOURNEY_INTERVAL_SECS = 120;

    private final JourneyRecordGenerator recordGenerator = new JourneyRecordGenerator();
    private final ScheduledExecutorService scheduler;

    public ExpectedJourneysGenerator(AbstractConfig config, Queue<SourceRecord> connectRecords, ScheduledExecutorService scheduler) {
        super(config, connectRecords);
        this.scheduler = scheduler;

        journeyTask.run();
        scheduler.scheduleAtFixedRate(
            journeyTask,
            TimeCalculations.remainingSecondsInHour() + 1,
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.SECONDS
        );
    }

    @Override
    protected ExpectedJourneys itemFactory(HourData raw) {
        return new ExpectedJourneys(raw);
    }

    private final Runnable journeyTask = () -> {
        try {
            ExpectedJourneys journeys = nextItem();

            int availableJourneySeconds = TimeCalculations.remainingSecondsInHour();
            if ((availableJourneySeconds - TEN_MINUTES_IN_SECONDS) < 0) {
                log.info("No time for any more journeys");
                return;
            }

            scheduleJourneys(availableJourneySeconds, journeys, journeys.getCasual(), RiderType.CASUAL);
            scheduleJourneys(availableJourneySeconds, journeys, journeys.getRegistered(), RiderType.REGISTERED);
        }
        catch (NoSuchElementException nsee) {
            log.error("Unexpected failure to get journey data", nsee);
        }
    };


    private void scheduleJourneys(int availableJourneySeconds, ExpectedJourneys journeys, int numJourneys, RiderType type) {
        for (int i = 0; i < numJourneys; i++) {
            // don't start any journeys after 50 mins past the hour
            int secondsUntilJourneyStart = Generators.randomInt(0, availableJourneySeconds - TEN_MINUTES_IN_SECONDS);
            int secondsUntilJourneyEnd = Generators.randomInt(secondsUntilJourneyStart + TEN_MINUTES_IN_SECONDS, availableJourneySeconds);

            fakeJourney(journeys, secondsUntilJourneyStart, secondsUntilJourneyEnd, i, type);
        }
    }


    private void fakeJourney(ExpectedJourneys set, int startSecs, int finishSecs, int journeyidx, RiderType journeytype) {
        log.info("preparing journey " + journeytype + " " + journeyidx);

        // get a random start location for the journey
        Location journeyStart = Generators.randomLocation();
        // get a bike that will be used for this journey
        String bikeId = BikeManager.checkout();

        // create a journey to emit events about
        ExpectedJourney journey = new ExpectedJourney(set, bikeId, journeytype, journeyStart);

        // START event

        log.info("scheduling start of journey " + journeytype + " " + journeyidx + " : NOW + " + startSecs + " (" + (startSecs / 60) + " mins)");
        scheduler.schedule(() -> {
            journey.updateTime();

            log.info("Journey " + journeyidx + " start " + journey.toString());
            SourceRecord updateRecord = recordGenerator.generate(journey, getTimestampFormatter());
            queue(updateRecord);
        }, startSecs, TimeUnit.SECONDS);


        // UPDATE events

        int currentTime = startSecs;
        while (true) {
            int randomInterval = Generators.randomInt(MIN_JOURNEY_INTERVAL_SECS, MAX_JOURNEY_INTERVAL_SECS);
            currentTime += randomInterval;
            if (currentTime >= finishSecs) {
                break;
            }

            log.info("scheduling next update of journey " + journeytype + " " + journeyidx + " : NOW + " + currentTime + " (" + (currentTime / 60) + " mins)");
            scheduler.schedule(() -> {
                journey.updateCurrentLocation();

                log.info("Journey " + journeyidx + " update " + journey.toString());
                SourceRecord updateRecord = recordGenerator.generate(journey, getTimestampFormatter());
                queue(updateRecord);
            }, currentTime, TimeUnit.SECONDS);
        }

        // END event

        log.info("scheduling end of journey " + journeytype + " " + journeyidx + " : NOW + " + finishSecs + " (" + (finishSecs / 60) + " mins)");
        scheduler.schedule(() -> {
            journey.updateCurrentLocation();

            log.info("Journey " + journeyidx + " complete " + journey.toString());
            SourceRecord updateRecord = recordGenerator.generate(journey, getTimestampFormatter());
            queue(updateRecord);

            BikeManager.checkin(bikeId);
        }, finishSecs, TimeUnit.SECONDS);
    }
}
