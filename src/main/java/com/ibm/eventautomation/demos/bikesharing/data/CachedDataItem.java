package com.ibm.eventautomation.demos.bikesharing.data;

import java.time.LocalDateTime;

public abstract class CachedDataItem {

    private LocalDateTime start;

    public CachedDataItem(LocalDateTime start) {
        this.start = start;
    }

    public CachedDataItem(HourData rawdata) {
        this.start = rawdata.getStart();
    }

    public LocalDateTime getStart() {
        return start;
    }
}
