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

public enum WeatherType {

    CLEAR(1),
    CLOUDY(2),
    LIGHT_RAIN(3),
    HEAVY_RAIN(4);

    public final int code;

    private WeatherType(int code) {
        this.code = code;
    }

    public static WeatherType valueOfCode(int code) throws BikeSharingDataException {
        for (WeatherType e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        throw new BikeSharingDataException("Unexpected weather type code " + code);
    }
}
