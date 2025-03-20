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

import com.opencsv.bean.CsvBindByName;

public class RawHourData {

    @CsvBindByName(required = true)
    private int instant;

    @CsvBindByName(required = true)
    private String dteday;

    @CsvBindByName(required = true)
    private int season;

    @CsvBindByName(required = true)
    private int yr;

    @CsvBindByName(required = true)
    private int mnth;

    @CsvBindByName(required = true)
    private int hr;

    @CsvBindByName(required = true)
    private int holiday;

    @CsvBindByName(required = true)
    private int weekday;

    @CsvBindByName(required = true)
    private int workingday;

    @CsvBindByName(required = true)
    private int weathersit;

    @CsvBindByName(required = true)
    private double temp;

    @CsvBindByName(required = true)
    private double atemp;

    @CsvBindByName(required = true)
    private double hum;

    @CsvBindByName(required = true)
    private double windspeed;

    @CsvBindByName(required = true)
    private int casual;

    @CsvBindByName(required = true)
    private int registered;

    @CsvBindByName(required = true)
    private int cnt;




    public int getInstant() {
        return instant;
    }

    public String getDteday() {
        return dteday;
    }

    public int getSeason() {
        return season;
    }

    public int getYr() {
        return yr;
    }

    public int getMnth() {
        return mnth;
    }

    public int getHr() {
        return hr;
    }

    public int getHoliday() {
        return holiday;
    }

    public int getWeekday() {
        return weekday;
    }

    public int getWorkingday() {
        return workingday;
    }

    public int getWeathersit() {
        return weathersit;
    }

    public double getTemp() {
        return temp;
    }

    public double getAtemp() {
        return atemp;
    }

    public double getHum() {
        return hum;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public int getCasual() {
        return casual;
    }

    public int getRegistered() {
        return registered;
    }

    public int getCnt() {
        return cnt;
    }

    @Override
    public String toString() {
        return "RawHourData [yr=" + yr + ", mnth=" + mnth + ", dteday=" + dteday + ", hr=" + hr + "]";
    }
}
