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
package com.ibm.eventautomation.demos.bikesharing.generators;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.bikesharing.DatagenSourceConfig;
import com.ibm.eventautomation.demos.bikesharing.data.CachedDataItem;
import com.ibm.eventautomation.demos.bikesharing.data.HourData;
import com.ibm.eventautomation.demos.bikesharing.data.RawHourData;
import com.ibm.eventautomation.demos.bikesharing.exceptions.BikeSharingDataException;
import com.ibm.eventautomation.demos.bikesharing.utils.Store;
import com.opencsv.bean.CsvToBeanBuilder;


/**
 * Generic generator for creating events derived from the "hour.csv" CSV file.
 *
 * The type of event generated is defined by the generic class.
 * The events are generated by the abstract itemFactory - the job of this abstract
 *  class is to parse the CSV file and provide the methods to drive getting the
 *  next available record for Kafka.
 */
public abstract class Generator<T extends CachedDataItem> {

    private static final String DATA_SOURCE_FILE = "data/hour.csv";

    private final Store<T> cache;
    private final Queue<SourceRecord> records;

    /** formatter for event timestamps */
    private final DateTimeFormatter timestampFormatter;

    protected Generator(AbstractConfig config, Queue<SourceRecord> connectRecords) throws BikeSharingDataException {
        timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
        this.cache = getCachedData();
        this.records = connectRecords;
    }

    protected abstract T itemFactory(HourData raw);

    protected T nextItem() throws NoSuchElementException {
        return cache.get();
    }

    protected void queue(SourceRecord record) {
        records.add(record);
    }

    protected DateTimeFormatter getTimestampFormatter() {
        return timestampFormatter;
    }

    private Store<T> getCachedData() throws BikeSharingDataException {
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(DATA_SOURCE_FILE))) {
            return new Store<>(new CsvToBeanBuilder<RawHourData>(reader)
                                    .withType(RawHourData.class)
                                    .build().parse().stream()
                                    .map(raw -> new HourData(raw))
                                    .map(all -> itemFactory(all))
                                    .collect(Collectors.toList()));
        }
        catch (IOException exc) {
            throw new BikeSharingDataException(exc);
        }
    }
}
