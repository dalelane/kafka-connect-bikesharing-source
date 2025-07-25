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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.ibm.eventautomation.demos.bikesharing.exceptions.BikeSharingDataException;
import com.ibm.eventautomation.demos.bikesharing.utils.BikeManager;

public class HourData {

    private int recordIndex;

    private LocalDateTime start;

    private Season season;

    private boolean holiday;
    private boolean workingDay;
    private DayOfWeek dayOfWeek;

    private WeatherType weather;

    private NormalizedValue temperature;

    private NormalizedValue feelsLikeTemperature;

    private NormalizedValue humidity;

    private NormalizedValue windSpeed;

    private int casualUsers;
    private int registeredUsers;

    private static final int DATASET_YEAR = 2011;

    public HourData(RawHourData csvData, int yearOffset) throws BikeSharingDataException {
        this.recordIndex = csvData.getInstant();
        this.start = convertToDateTime(csvData.getDteday(),
                                       csvData.getYr() + DATASET_YEAR + yearOffset,
                                       csvData.getHr());
        this.season = Season.valueOfCode(csvData.getSeason());
        this.holiday = csvData.getHoliday() == 1;
        this.workingDay = csvData.getWorkingday() == 1;
        this.dayOfWeek = DayOfWeek.valueOfCode(csvData.getWeekday());
        this.weather = WeatherType.valueOfCode(csvData.getWeathersit());
        this.temperature = new NormalizedValue(csvData.getTemp(), 41);
        this.feelsLikeTemperature = new NormalizedValue(csvData.getAtemp(), 50);
        this.humidity = new NormalizedValue(csvData.getHum(), 100);
        this.windSpeed = new NormalizedValue(csvData.getWindspeed(), 67);
        this.casualUsers = csvData.getCasual();
        this.registeredUsers = csvData.getRegistered();
        int total = csvData.getCasual() + csvData.getRegistered();
        if (total != csvData.getCnt()) {
            throw new BikeSharingDataException(
                "Unexpected total " + csvData.getCnt() +
                " from casual " + csvData.getCasual() +
                " and registered " + csvData.getRegistered());
        }
        if (total > BikeManager.NUM_BIKES) {
            throw new BikeSharingDataException(
                "Insufficient bikes available for required" +
                " number of journeys " + total);
        }
    }


    public int getRecordIndex() {
        return recordIndex;
    }
    public LocalDateTime getStart() {
        return start;
    }
    public Season getSeason() {
        return season;
    }
    public boolean isHoliday() {
        return holiday;
    }
    public boolean isWorkingDay() {
        return workingDay;
    }
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    public WeatherType getWeather() {
        return weather;
    }
    public NormalizedValue getTemperature() {
        return temperature;
    }
    public NormalizedValue getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }
    public NormalizedValue getHumidity() {
        return humidity;
    }
    public NormalizedValue getWindSpeed() {
        return windSpeed;
    }
    public int getCasualUsers() {
        return casualUsers;
    }
    public int getRegisteredUsers() {
        return registeredUsers;
    }





    private static final DateTimeFormatter DATE_STRING_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static LocalDateTime convertToDateTime(String dateStr, int year, int hour) {
        try {
            return LocalDate.parse(dateStr, DATE_STRING_FORMATTER)
                    .withYear(year)
                    .atTime(hour, 0);
        }
        catch (DateTimeParseException e) {
            throw new BikeSharingDataException(e);
        }
    }
}
