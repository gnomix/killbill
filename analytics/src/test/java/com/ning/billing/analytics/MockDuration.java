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

package com.ning.billing.analytics;

import com.ning.billing.catalog.api.Duration;
import com.ning.billing.catalog.api.TimeUnit;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class MockDuration
{
    public static Duration MONHTLY()
    {
        return new Duration()
        {
            @Override
            public TimeUnit getUnit()
            {
                return TimeUnit.MONTHS;
            }

            @Override
            public int getNumber()
            {
                return 1;
            }

            @Override
            public DateTime addToDateTime(DateTime dateTime) {
                throw new NotImplementedException();
            }
            @Override
            public Period toJodaPeriod() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Duration YEARLY()
    {
        return new Duration()
        {
            @Override
            public TimeUnit getUnit()
            {
                return TimeUnit.YEARS;
            }

            @Override
            public int getNumber()
            {
                return 1;
            }

            @Override
            public DateTime addToDateTime(DateTime dateTime) {
                throw new NotImplementedException();
            }
            @Override
            public Period toJodaPeriod() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Duration UNLIMITED()
    {
        return new Duration()
        {
            @Override
            public TimeUnit getUnit()
            {
                return TimeUnit.UNLIMITED;
            }

            @Override
            public int getNumber()
            {
                return 1;
            }

            @Override
            public DateTime addToDateTime(DateTime dateTime) {
                throw new NotImplementedException();
            }
            @Override
            public Period toJodaPeriod() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
