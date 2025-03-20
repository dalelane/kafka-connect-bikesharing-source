package com.ibm.eventautomation.demos.bikesharing.generators;

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
        try {
            WeatherReport weatherReport = nextItem();
            log.info("Weather forecast " + weatherReport.toString());
            SourceRecord weatherRecord = recordGenerator.generate(weatherReport, getTimestampFormatter());
            queue(weatherRecord);
        }
        catch (NoSuchElementException nsee) {
            log.debug("No record available");
        }
    };
}
