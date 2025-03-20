package com.ibm.eventautomation.demos.bikesharing.data;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ibm.eventautomation.demos.bikesharing.utils.Generators;

public class ExpectedJourney extends CachedDataItem {

    private final String journeyId = UUID.randomUUID().toString();
    private final RiderType rider;
    private final String bikeId;
    private final Location startingLocation;
    private Location currentLocation;
    private LocalDateTime update;

    public ExpectedJourney(ExpectedJourneys journeysSet, String bikeId, RiderType rider, Location startingLocation) {
        super(journeysSet.getStart());
        this.bikeId = bikeId;
        this.rider = rider;
        this.startingLocation = startingLocation;
        this.currentLocation = new Location(startingLocation);
        this.update = journeysSet.getStart();
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
    public void updateCurrentLocation() {
        this.currentLocation = Generators.randomLocation(currentLocation);
        updateTime();
    }

    public void updateTime() {
        this.update = LocalDateTime.now();
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
            ", startingLocation=" + startingLocation +
            ", currentLocation=" + currentLocation +
            "]";
    }
}
