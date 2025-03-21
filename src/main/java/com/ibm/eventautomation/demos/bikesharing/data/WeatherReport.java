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

import java.time.LocalDateTime;

public class WeatherReport extends CachedDataItem {

    private double temperature;
    private double feelsLikeTemperature;
    private double humidity;
    private double windSpeed;
    private WeatherType type;

    public WeatherReport(HourData input) {
        super(input);
        this.temperature = input.getTemperature().getRaw();
        this.feelsLikeTemperature = input.getFeelsLikeTemperature().getRaw();
        this.humidity = input.getHumidity().getRaw();
        this.windSpeed = input.getWindSpeed().getRaw();
        this.type = input.getWeather();
    }

    public WeatherReport(LocalDateTime newTimestamp, WeatherReport reportToClone) {
        super(newTimestamp);
        this.temperature = reportToClone.getTemperature();
        this.feelsLikeTemperature = reportToClone.getFeelsLikeTemperature();
        this.humidity = reportToClone.getHumidity();
        this.windSpeed = reportToClone.getWindSpeed();
        this.type = reportToClone.getType();
    }

    public double getTemperature() {
        return temperature;
    }

    public double getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public WeatherType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "WeatherReport [temperature=" + temperature + ", feelsLikeTemperature=" + feelsLikeTemperature
                + ", humidity=" + humidity + ", windSpeed=" + windSpeed + ", type=" + type + "]";
    }
}
