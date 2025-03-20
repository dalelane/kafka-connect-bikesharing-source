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

public class NormalizedValue {

    private double raw;
    private double normalized;

    public NormalizedValue(double normalized, int ratio) throws BikeSharingDataException {
        this.normalized = normalized;
        raw = normalized * ratio;

        if (normalized > 1 || normalized < 0) {
            throw new BikeSharingDataException("Unexpected normalized value " + normalized);
        }
    }


    public double getRaw() {
        return raw;
    }

    public double getNormalized() {
        return normalized;
    }


    @Override
    public String toString() {
        return "NormalizedValue [raw=" + raw + ", normalized=" + normalized + "]";
    }
}
