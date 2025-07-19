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
package com.ibm.eventautomation.demos.bikesharing.data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.ibm.eventautomation.demos.bikesharing.utils.Generators;

public class ExpectedJourney extends CachedDataItem {

    private final String journeyId = UUID.randomUUID().toString();
    private final RiderType rider;
    private final String bikeId;
    private final Location startingLocation;
    private Location currentLocation;
    private int currentBatteryLevel;
    private LocalDateTime update;

    public ExpectedJourney(ExpectedJourneys journeysSet, String bikeId, RiderType rider, Location startingLocation, int startingBatteryLevel) {
        super(journeysSet.getStart());
        this.bikeId = bikeId;
        this.rider = rider;
        this.startingLocation = startingLocation;
        this.currentLocation = new Location(startingLocation);
        this.currentBatteryLevel = startingBatteryLevel;
        this.update = journeysSet.getStart();
    }

    public ExpectedJourney(int yearsOffset) {
        super(LocalDateTime.now().minus(yearsOffset, ChronoUnit.YEARS));
        this.rider = RiderType.HEARTBEAT;
        this.bikeId = "";
        this.startingLocation = new Location(0, 0);
        this.currentLocation = this.startingLocation;
        this.update = LocalDateTime.now().minus(yearsOffset, ChronoUnit.YEARS);
    }

    public String getJourneyId() {
        return journeyId;
    }

    public String getBikeId() {
        return bikeId;
    }

    public RiderType getRider() {
        return rider;
    }

    public Location getStartingLocation() {
        return startingLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
    public void updateCurrentLocation(int yearsOffset) {
        this.currentLocation = Generators.randomLocation(currentLocation);
        this.currentBatteryLevel = Generators.randomBatteryLevel(currentBatteryLevel);

        updateTime(yearsOffset);
    }

    public Integer getBattery() {
        return currentBatteryLevel;
    }

    public void updateTime(int yearsOffset) {
        this.update = LocalDateTime.now().minus(yearsOffset, ChronoUnit.YEARS);
    }

    public void updateTime(LocalDateTime newtime) {
        this.update = newtime;
    }

    public LocalDateTime getUpdate() {
        return update;
    }

    @Override
    public String toString() {
        return "ExpectedJourney [journeyId=" + journeyId +
            ", time=" + update.toString() +
            ", bike=" + bikeId +
            ", rider=" + rider +
            ", battery=" + currentBatteryLevel +
            ", startingLocation=" + startingLocation +
            ", currentLocation=" + currentLocation +
            "]";
    }
}
