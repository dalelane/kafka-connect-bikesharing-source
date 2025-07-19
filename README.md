# kafka-connect-bikesharing-source

Random data generator, that generates events using the data in the `hour.csv` file from https://www.kaggle.com/datasets/lakshmi25npathi/bike-sharing-dataset

> This dataset contains the hourly and daily count of rental bikes between the years 2011 and 2012 in the Capital bike share system with the corresponding weather and seasonal information.

It uses the timestamps from the CSV file, but ignores the year value, so the emitted values look like they are current.

Because the CSV file contains two years' worth of data, this connector can run for up to two years before it runs out of events to generate.

Emitted events:
- `BIKESHARING.WEATHER` - weather "forecasts" taken from the CSV file - emitted at the start of the hour, as if they are a forecast for the next hour
- `BIKESHARING.LOCATION` - current location of bikes that are currently on a journey (emitted at the start of a journey, periodically during a journey, and at the end of a journey)

## Config

| **option**           | **default value**         | **notes** |
| -------------------- | ------------------------- | --------- |
| `formats.timestamps` | `yyyy-MM-dd HH:mm:ss.SSS` | The format to use for timestamp strings in the event payload. |
| `timestamps.shift`   | `false`                   | The source for data from this connector is a dataset of bike journeys in 2011 - 2012. If this option is set to true, the timestamps for events from the connector will be timeshifted to the current year. If left as false, the connector will generate events using the timestamps from the original dataset as-is. |
