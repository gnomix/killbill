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

package com.ning.billing.invoice.tests.inAdvance.quarterly;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.invoice.model.InvalidDateSequenceException;
import com.ning.billing.invoice.tests.inAdvance.ProRationInAdvanceTestBase;

@Test(groups = {"fast", "invoicing", "proRation"})
public class TrailingProRationTests extends ProRationInAdvanceTestBase {
    @Override
    protected BillingPeriod getBillingPeriod() {
        return BillingPeriod.QUARTERLY;
    }

    @Test
    public void testTargetDateOnStartDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2010, 6, 17);
        DateTime endDate = buildDateTime(2010, 9, 25);
        DateTime targetDate = buildDateTime(2010, 6, 17);

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 17, ONE);
    }

    @Test
    public void testTargetDateInFirstBillingPeriod() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2010, 6, 17);
        DateTime endDate = buildDateTime(2010, 9, 25);
        DateTime targetDate = buildDateTime(2010, 6, 20);

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 17, ONE);
    }

    @Test
    public void testTargetDateAtEndOfFirstBillingCycle() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2010, 6, 17);
        DateTime endDate = buildDateTime(2010, 9, 25);
        DateTime targetDate = buildDateTime(2010, 9, 17);

        BigDecimal expectedValue = ONE.add(EIGHT.divide(NINETY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD));
        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 17, expectedValue);
    }

    @Test
    public void testTargetDateInProRationPeriod() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2010, 6, 17);
        DateTime endDate = buildDateTime(2010, 9, 25);
        DateTime targetDate = buildDateTime(2010, 9, 18);

        BigDecimal expectedValue = ONE.add(EIGHT.divide(NINETY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD));
        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 17, expectedValue);
    }

    @Test
    public void testTargetDateOnEndDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2010, 6, 17);
        DateTime endDate = buildDateTime(2010, 9, 25);

        BigDecimal expectedValue = ONE.add(EIGHT.divide(NINETY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD));
        testCalculateNumberOfBillingCycles(startDate, endDate, endDate, 17, expectedValue);
    }

    @Test
    public void testTargetDateAfterEndDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2010, 6, 17);
        DateTime endDate = buildDateTime(2010, 9, 25);
        DateTime targetDate = buildDateTime(2010, 9, 30);

        BigDecimal expectedValue = ONE.add(EIGHT.divide(NINETY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD));
        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 17, expectedValue);
    }
}