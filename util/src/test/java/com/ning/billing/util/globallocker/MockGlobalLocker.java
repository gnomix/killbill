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

package com.ning.billing.util.globallocker;

import com.ning.billing.util.globallocker.GlobalLock;
import com.ning.billing.util.globallocker.GlobalLocker;

public class MockGlobalLocker implements GlobalLocker {

    @Override
    public GlobalLock lockWithNumberOfTries(LockerService service,
            String lockKey, int retry) {
        return new GlobalLock() {
            @Override
            public void release() {
            }
        };
    }

    @Override
    public Boolean isFree(LockerService service, String lockKey) {
        return Boolean.TRUE;
    }
}
