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

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTaskContext;
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

    public ExpectedJourneysGenerator(SourceTaskContext context, AbstractConfig config, Queue<SourceRecord> connectRecords, ScheduledExecutorService scheduler) {
        super(config, connectRecords);
        initialise(context, recordGenerator.sourcePartition());

        this.scheduler = scheduler;

        int initialDelay = TimeCalculations.remainingSecondsInHour() + 1;

        scheduler.scheduleAtFixedRate(
            journeyTask,
            initialDelay,
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.SECONDS
        );
        scheduler.scheduleAtFixedRate(
            heartbeatTask,
            initialDelay,
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.SECONDS
        );
    }

    @Override
    protected ExpectedJourneys itemFactory(HourData raw) {
        return new ExpectedJourneys(raw);
    }

    private final Runnable heartbeatTask = () -> {
        ExpectedJourney heartbeat = new ExpectedJourney(nowYearsOffset);
        SourceRecord updateRecord = recordGenerator.generate(heartbeat, getTimestampFormatter());
        queue(updateRecord);
    };

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
        log.debug("preparing journey " + journeytype + " " + journeyidx);

        // get a random start location for the journey
        Location journeyStart = Generators.randomLocation();
        // get a random starting battery charge level for the bike at the start of the journey
        int batteryStart = Generators.randomInt(80, 100);
        // get a bike that will be used for this journey
        String bikeId = BikeManager.checkout();

        // create a journey to emit events about
        ExpectedJourney journey = new ExpectedJourney(set, bikeId, journeytype, journeyStart, batteryStart);

        // START event

        log.debug("scheduling start of journey " + journeytype + " " + journeyidx + " : NOW + " + startSecs + " (" + (startSecs / 60) + " mins)");
        scheduler.schedule(() -> {
            journey.updateTime(nowYearsOffset);

            log.debug("Journey " + journeyidx + " start " + journey.toString());
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

            log.debug("scheduling next update of journey " + journeytype + " " + journeyidx + " : NOW + " + currentTime + " (" + (currentTime / 60) + " mins)");
            scheduler.schedule(() -> {
                journey.updateCurrentLocation(nowYearsOffset);

                log.debug("Journey " + journeyidx + " update " + journey.toString());
                SourceRecord updateRecord = recordGenerator.generate(journey, getTimestampFormatter());
                queue(updateRecord);
            }, currentTime, TimeUnit.SECONDS);
        }

        // END event

        log.debug("scheduling end of journey " + journeytype + " " + journeyidx + " : NOW + " + finishSecs + " (" + (finishSecs / 60) + " mins)");
        scheduler.schedule(() -> {
            journey.updateCurrentLocation(nowYearsOffset);

            log.debug("Journey " + journeyidx + " complete " + journey.toString());
            SourceRecord updateRecord = recordGenerator.generate(journey, getTimestampFormatter());
            queue(updateRecord);

            BikeManager.checkin(bikeId);
        }, finishSecs, TimeUnit.SECONDS);
    }




    // historical journey records (created the first time the Connector starts to
    //  add events from the start of the year) are simpler than the live journeys
    //  generated - represented by a single location event at 5 minutes past the hour

    @Override
    protected void processNextItem(ExpectedJourneys journeys) {
        historicalJourneys(journeys, journeys.getCasual(), RiderType.CASUAL);
        historicalJourneys(journeys, journeys.getRegistered(), RiderType.REGISTERED);
    }

    private void historicalJourneys(ExpectedJourneys journeys, int numJourneys, RiderType type) {
        LocalDateTime journeyTime = journeys.getStart().withMinute(5);
        for (int i = 0; i < numJourneys; i++) {
            historicalJourney(journeys, journeyTime, type);
        }
    }

    private void historicalJourney(ExpectedJourneys set, LocalDateTime journeyTime, RiderType journeytype) {
        Location journeyStart = Generators.randomLocation();
        int batteryStart = Generators.randomInt(80, 100);
        String bikeId = BikeManager.checkout();
        ExpectedJourney journey = new ExpectedJourney(set, bikeId, journeytype, journeyStart, batteryStart);
        journey.updateTime(journeyTime);

        SourceRecord updateRecord = recordGenerator.generate(journey, getTimestampFormatter());
        queue(updateRecord);

        BikeManager.checkin(bikeId);
    }
}
