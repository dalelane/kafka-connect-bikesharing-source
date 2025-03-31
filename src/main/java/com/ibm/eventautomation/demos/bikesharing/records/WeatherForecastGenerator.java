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
package com.ibm.eventautomation.demos.bikesharing.records;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import com.ibm.eventautomation.demos.bikesharing.data.WeatherReport;

public class WeatherForecastGenerator extends RecordGenerator<WeatherReport> {

    private static final Schema TEMPERATURE_SCHEMA = SchemaBuilder.struct()
        .name("temperature")
        .version(1)
            .field("reading",   Schema.FLOAT64_SCHEMA)
            .field("feelslike", Schema.FLOAT64_SCHEMA)
        .build();

    private static final Schema WEATHER_SCHEMA = SchemaBuilder.struct()
        .name("code")
        .version(1)
            .field("description", Schema.STRING_SCHEMA)
            .field("code",        Schema.INT32_SCHEMA)
        .build();

    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("weather")
        .version(1)
            .field("temperature", TEMPERATURE_SCHEMA)
            .field("type",        WEATHER_SCHEMA)
            .field("humidity",    Schema.FLOAT64_SCHEMA)
            .field("windSpeed",   Schema.FLOAT64_SCHEMA)
            .field("time",        Schema.STRING_SCHEMA)
        .build();


    @Override
    public Schema keySchema() {
        return null;
    }

    @Override
    public String key(WeatherReport data) {
        return null;
    }

    @Override
    public Schema valueSchema() {
        return SCHEMA;
    }

    @Override
    public Map<String, String> sourcePartition() {
        return Collections.singletonMap(SOURCE_PARTITION, "weather");
    }

    @Override
    public Struct value(WeatherReport data, DateTimeFormatter formatter) {
        Struct temperature = new Struct(TEMPERATURE_SCHEMA);
        temperature.put(TEMPERATURE_SCHEMA.field("reading"),   data.getTemperature());
        temperature.put(TEMPERATURE_SCHEMA.field("feelslike"), data.getFeelsLikeTemperature());

        Struct type = new Struct(WEATHER_SCHEMA);
        type.put(WEATHER_SCHEMA.field("description"), data.getType().toString());
        type.put(WEATHER_SCHEMA.field("code"),        data.getType().code);

        Struct value = new Struct(SCHEMA);
        value.put(SCHEMA.field("temperature"), temperature);
        value.put(SCHEMA.field("type"),        type);
        value.put(SCHEMA.field("humidity"),    data.getHumidity());
        value.put(SCHEMA.field("windSpeed"),   data.getWindSpeed());
        value.put(SCHEMA.field("time"),        formatter.format(data.getStart()));

        return value;
    }

    @Override
    public String topic(WeatherReport data) {
        return "BIKESHARING.WEATHER";
    }
}
