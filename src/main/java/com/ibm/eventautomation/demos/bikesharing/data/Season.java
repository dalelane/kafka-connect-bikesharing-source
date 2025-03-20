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

public enum Season {
    SPRING(1),
    SUMMER(2),
    FALL(3),
    WINTER(4);

    public final int code;

    private Season(int code) {
        this.code = code;
    }

    public static Season valueOfCode(int code) throws BikeSharingDataException {
        for (Season e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        throw new BikeSharingDataException("Unexpected season code " + code);
    }
}
