/**
 * Copyright 2023 IBM Corp. All Rights Reserved.
 *
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

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.NonEmptyString;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;

public class DatagenSourceConfig {

    private static final String CONFIG_GROUP = "Config";
    public static final String CONFIG_FORMATS_TIMESTAMPS = "formats.timestamps";
    public static final String CONFIG_DATE_SHIFT = "timestamps.shift";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(CONFIG_DATE_SHIFT,
                Type.BOOLEAN,
                false,
                Importance.LOW,
                "The source for data from this connector is a dataset of bike journeys in 2011 - 2012. " +
                    "If true, the timestamps for events from the connector will be timeshifted to the current year. " +
                    "If false, the year from the dataset will be used as-is.",
                CONFIG_GROUP, 1, Width.LONG, "Timestamps shift")
        .define(CONFIG_FORMATS_TIMESTAMPS,
                Type.STRING,
                "yyyy-MM-dd HH:mm:ss.SSS",
                new NonEmptyString(),
                Importance.LOW,
                "Format to use for timestamps generated for events.",
                CONFIG_GROUP, 2, Width.LONG, "Timestamp format");

}
