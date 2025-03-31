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
package com.ibm.eventautomation.demos.bikesharing.utils;

import java.util.Map;

import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.bikesharing.records.RecordGenerator;


public class OffsetManagement {

    private static final Logger log = LoggerFactory.getLogger(OffsetManagement.class);


    public static boolean hasStoredOffset(SourceTaskContext context, Map<String, String> sourcePartition) {
        OffsetStorageReader storageReader = getOffsetStorageReader(context);
        Long offset = getPersistedOffset(storageReader, sourcePartition);
        return offset > 0;
    }


    private static OffsetStorageReader getOffsetStorageReader(SourceTaskContext context) {
        if (context == null) {
            log.debug("No context - assuming that this is the first time the Connector has run");
            return null;
        }
        else if (context.offsetStorageReader() == null) {
            log.debug("No offset reader - assuming that this is the first time the Connector has run");
            return null;
        }
        else {
            return context.offsetStorageReader();
        }
    }

    private static Long getPersistedOffset(OffsetStorageReader offsetReader, Map<String, String> sourcePartition) {
        log.debug("retrieving persisted offset for previously produced events");

        if (offsetReader == null) {
            log.debug("no offset reader available");
            return 0L;
        }

        Map<String, Object> persistedOffsetInfo = offsetReader.offset(sourcePartition);
        if (persistedOffsetInfo == null) {
            log.debug("no persisted offset");
            return 0L;
        }

        Long offset = (Long) persistedOffsetInfo.getOrDefault(RecordGenerator.SOURCE_OFFSET, 0L);
        log.info("previous offset : {}", offset);

        return offset;
    }
}
