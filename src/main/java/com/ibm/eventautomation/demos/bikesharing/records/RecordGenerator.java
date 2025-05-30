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

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.bikesharing.data.CachedDataItem;


public abstract class RecordGenerator<T extends CachedDataItem> {

    public abstract String topic(T data);

    public Integer topicPartition(T data) {
        return null;
    }


    public ConnectHeaders headers(T data) {
        return new ConnectHeaders();
    }

    public long getTimestamp(T data) {
        return data.getStart().toEpochSecond(ZoneOffset.UTC) * 1000L;
    }

    protected static final String SOURCE_PARTITION = "source";
    public abstract Map<String, ?> sourcePartition();

    public static final String SOURCE_OFFSET = "date";
    public Map<String, Long> sourceOffset(T data) {
        return Collections.singletonMap(SOURCE_OFFSET, data.getStart().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public abstract Schema keySchema();
    public abstract String key(T data);
    public abstract Schema valueSchema();
    public abstract Struct value(T data, DateTimeFormatter formatter);

    public SourceRecord generate(T data,  DateTimeFormatter formatter) {
        return new SourceRecord(sourcePartition(),
                                sourceOffset(data),
                                topic(data), topicPartition(data),
                                keySchema(), key(data),
                                valueSchema(), value(data, formatter),
                                getTimestamp(data),
                                headers(data));
    }
}
