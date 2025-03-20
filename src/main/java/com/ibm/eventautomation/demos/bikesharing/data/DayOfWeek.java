package com.ibm.eventautomation.demos.bikesharing.data;

import com.ibm.eventautomation.demos.bikesharing.exceptions.BikeSharingDataException;

public enum DayOfWeek {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6);

    public final int code;

    private DayOfWeek(int code) {
        this.code = code;
    }

    public static DayOfWeek valueOfCode(int code) throws BikeSharingDataException {
        for (DayOfWeek e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        throw new BikeSharingDataException("Unexpected day-of-week code " + code);
    }
}
