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
package com.ibm.eventautomation.demos.bikesharing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.bikesharing.generators.ExpectedJourneysGenerator;
import com.ibm.eventautomation.demos.bikesharing.generators.WeatherReportGenerator;


public class DatagenSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(DatagenSourceTask.class);

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *
     *  When Kafka Connect calls the connector in the poll() method, it
     *  will retrieve messages from this queue.
     *
     *  When the scheduled timers fire to generate randomly created
     *  messages, they will add messages to this queue.
     */
    private final Queue<SourceRecord> queue = new ConcurrentLinkedQueue<>();



    @Override
    public void start(Map<String, String> props) {
        log.info("Starting task {}", props);
        scheduler = Executors.newScheduledThreadPool(1);

        AbstractConfig config = new AbstractConfig(DatagenSourceConfig.CONFIG_DEF, props);

        new WeatherReportGenerator(context, config, queue, scheduler);
        new ExpectedJourneysGenerator(context, config, queue, scheduler);
    }


    @Override
    public void stop() {
        log.info("Stopping task");

        scheduler.shutdownNow();
        queue.clear();
    }


    @Override
    public List<SourceRecord> poll() {
        List<SourceRecord> currentRecords = new ArrayList<>();

        SourceRecord nextItem = queue.poll();
        while (nextItem != null) {
            currentRecords.add(nextItem);

            nextItem = queue.poll();
        }
        return currentRecords;
    }


    @Override
    public String version() {
        return DatagenSourceConnector.VERSION;
    }
}
