package com.ibm.eventautomation.demos.bikesharing.generators;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.bikesharing.data.HourData;
import com.ibm.eventautomation.demos.bikesharing.data.WeatherReport;
import com.ibm.eventautomation.demos.bikesharing.records.WeatherForecastGenerator;
import com.ibm.eventautomation.demos.bikesharing.utils.TimeCalculations;

public class WeatherReportGenerator extends Generator<WeatherReport> {

    private static final Logger log = LoggerFactory.getLogger(WeatherReportGenerator.class);

    private final WeatherForecastGenerator recordGenerator = new WeatherForecastGenerator();

    public WeatherReportGenerator(AbstractConfig config, Queue<SourceRecord> connectRecords, ScheduledExecutorService scheduler) {
        super(config, connectRecords);
        weatherTask.run();

        scheduler.scheduleAtFixedRate(
            weatherTask,
            TimeCalculations.remainingSecondsInHour() + 1,
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.SECONDS
        );
    }

    @Override
    protected WeatherReport itemFactory(HourData raw) {
        return new WeatherReport(raw);
    }

    private final Runnable weatherTask = () -> {
        WeatherReport weatherReport;
        try {
            weatherReport = nextItem();
        }
        catch (NoSuchElementException nsee) {
            weatherReport = peek();
            if (weatherReport == null) {
                log.debug("No record available - no more reports can be generated");
                return;
            }
            else {
                log.debug("Record for this hour is missing - (re)use the record for the next hour to fill the gap");
                LocalDateTime topOfHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
                weatherReport = new WeatherReport(topOfHour, weatherReport);
            }
        }

        log.info("Weather forecast " + weatherReport.toString());
        SourceRecord weatherRecord = recordGenerator.generate(weatherReport, getTimestampFormatter());
        queue(weatherRecord);
    };
}
