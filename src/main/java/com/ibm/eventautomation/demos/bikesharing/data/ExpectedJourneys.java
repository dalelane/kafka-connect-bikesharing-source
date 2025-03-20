package com.ibm.eventautomation.demos.bikesharing.data;

public class ExpectedJourneys extends CachedDataItem {

    private int casual;
    private int registered;

    public ExpectedJourneys(HourData input) {
        super(input);
        this.casual = input.getCasualUsers();
        this.registered = input.getRegisteredUsers();
    }

    public int getCasual() {
        return casual;
    }

    public int getRegistered() {
        return registered;
    }

    public int getTotal() {
        return casual + registered;
    }

    @Override
    public String toString() {
        return "ExpectedJourneys [getCasual()=" + getCasual() +
                    ", getStart()=" + getStart() +
                    ", getRegistered()=" + getRegistered() +
                    "]";
    }
}
