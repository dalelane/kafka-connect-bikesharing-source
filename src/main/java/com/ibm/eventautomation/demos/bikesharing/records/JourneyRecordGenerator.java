package com.ibm.eventautomation.demos.bikesharing.records;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import com.ibm.eventautomation.demos.bikesharing.data.ExpectedJourney;

public class JourneyRecordGenerator extends RecordGenerator<ExpectedJourney> {

    private static final Schema LOCATION_SCHEMA = SchemaBuilder.struct()
        .name("location")
        .version(1)
            .field("latitude",  Schema.FLOAT64_SCHEMA)
            .field("longitude", Schema.FLOAT64_SCHEMA)
        .build();

    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("journey")
        .version(1)
            .field("journeyid", Schema.STRING_SCHEMA)
            .field("bikeid",    Schema.STRING_SCHEMA)
            .field("usertype",  Schema.STRING_SCHEMA)
            .field("location",  LOCATION_SCHEMA)
            .field("battery",   Schema.INT16_SCHEMA)
            .field("time",      Schema.STRING_SCHEMA)
        .build();

    @Override
    public String topic(ExpectedJourney data) {
        return "BIKESHARING.LOCATION";
    }

    @Override
    public Schema keySchema() {
        return Schema.STRING_SCHEMA;
    }

    @Override
    public String key(ExpectedJourney data) {
        return data.getBikeId();
    }

    @Override
    public Schema valueSchema() {
        return SCHEMA;
    }

    @Override
    public long getTimestamp(ExpectedJourney data) {
        return data.getUpdate().toEpochSecond(ZoneOffset.UTC) * 1000L;
    }

    @Override
    public Map<String, String> sourcePartition() {
        return Collections.singletonMap(SOURCE_PARTITION, "journey");
    }

    @Override
    public Struct value(ExpectedJourney data, DateTimeFormatter formatter) {
        Struct location = new Struct(LOCATION_SCHEMA);
        location.put(LOCATION_SCHEMA.field("latitude"),  data.getCurrentLocation().getLatitude());
        location.put(LOCATION_SCHEMA.field("longitude"), data.getCurrentLocation().getLongitude());

        Struct value = new Struct(SCHEMA);
        value.put(SCHEMA.field("journeyid"), data.getJourneyId());
        value.put(SCHEMA.field("bikeid"),    data.getBikeId());
        value.put(SCHEMA.field("usertype"),  data.getRider().toString());
        value.put(SCHEMA.field("location"),  location);
        value.put(SCHEMA.field("battery"),   data.getBattery().shortValue());
        value.put(SCHEMA.field("time"),      formatter.format(data.getUpdate()));

        return value;
    }
}
