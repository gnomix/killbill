/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.util.notificationq;

import java.util.UUID;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.sqlobject.mixins.Transmogrifier;

import com.ning.billing.util.notificationq.NotificationQueueService.NotificationQueueHandler;
import com.ning.billing.util.queue.QueueLifecycle;

public interface NotificationQueue extends QueueLifecycle {

    /**
     *
     * Record the need to be called back when the notification is ready
     *
     * @param futureNotificationTime the time at which the notification is ready
     * @param notificationKey the key for that notification
     */
    public void recordFutureNotification(final DateTime futureNotificationTime, final NotificationKey notificationKey);

    /**
     *
     * Record from within a transaction the need to be called back when the notification is ready
     *
     * @param transactionalDao the transactionalDao
     * @param futureNotificationTime the time at which the notification is ready
     * @param notificationKey the key for that notification
     */
    public void recordFutureNotificationFromTransaction(final Transmogrifier transactionalDao,
            final DateTime futureNotificationTime, final NotificationKey notificationKey);

  
    /**
     * Remove all notifications associated with this key   
     * 
     * @param key
     */
    public void removeNotificationsByKey(UUID key);

    /**
     * This is only valid when the queue has been configured with isNotificationProcessingOff is true
     * In which case, it will callback users for all the ready notifications.
     *
     * @return the number of entries we processed
     */
    public int processReadyNotification();

    /**
     *
     * @return the name of that queue
     */
    public String getFullQName();



}
