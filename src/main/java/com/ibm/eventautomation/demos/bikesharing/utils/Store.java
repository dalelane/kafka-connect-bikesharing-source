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

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import com.ibm.eventautomation.demos.bikesharing.data.CachedDataItem;

/**
 * Holds a store of data, all items of which have a timestamp.
 */
public class Store<T extends CachedDataItem> {

    private final Queue<T> items;

    /**
     * Holds a sorted collection of items (sorted by timestamp)
     *
     * @param items - data items, sorted by start timestamp
     */
    public Store(List<T> items) {
        this.items = new LinkedList<>(items);
    }

    /**
     * Returns the oldest (first) item in the store with a timestamp that
     * is later than the current time.
     *
     * Items older than this are discarded from the store.
     *
     * @throws NoSuchElementException if the store is now empty, or if
     *  all of the items have a timestamp that is too far in the future
     *  to be returned yet
     */
    public T get() throws NoSuchElementException {
        LocalDateTime now = LocalDateTime.now();
        if (items.isEmpty()) {
            throw new NoSuchElementException("No items left in the store");
        }
        if (items.peek().getStart().getHour() > now.getHour()) {
            throw new NoSuchElementException("Next item missing - wait for the next item");
        }
        T previous = items.remove();
        T next = items.peek();
        while (next.getStart().isBefore(now)) {
            previous = items.remove();
            next = items.peek();
        }
        return previous;
    }

    public T peek() {
        return items.peek();
    }
}
