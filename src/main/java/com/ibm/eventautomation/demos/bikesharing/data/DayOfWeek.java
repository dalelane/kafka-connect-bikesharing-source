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
