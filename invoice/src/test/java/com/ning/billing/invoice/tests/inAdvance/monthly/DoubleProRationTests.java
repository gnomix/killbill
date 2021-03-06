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

package com.ning.billing.invoice.tests.inAdvance.monthly;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.invoice.model.InvalidDateSequenceException;
import com.ning.billing.invoice.tests.inAdvance.ProRationInAdvanceTestBase;

@Test(groups = {"fast", "invoicing", "proRation"})
public class DoubleProRationTests extends ProRationInAdvanceTestBase {
    @Override
    protected BillingPeriod getBillingPeriod() {
        return BillingPeriod.MONTHLY;
    }

    @Test
    public void testDoubleProRation_TargetDateOnStartDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 1, 1);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRation_TargetDateInFirstProRationPeriod() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 1, 7);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRation_TargetDateOnFirstBillingCycleDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 1, 15);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue = ONE.add(FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD));
        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRation_TargetDateInFullBillingPeriod() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 1, 22);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue;
        expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        expectedValue = expectedValue.add(ONE);

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRation_TargetDateOnSecondBillingCycleDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 2, 27);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue;
        expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        expectedValue = expectedValue.add(ONE);
        expectedValue = expectedValue.add(TWELVE.divide(TWENTY_EIGHT, NUMBER_OF_DECIMALS, ROUNDING_METHOD));

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRation_TargetDateInSecondProRationPeriod() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 2, 26);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue;
        expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        expectedValue = expectedValue.add(ONE);
        expectedValue = expectedValue.add(TWELVE.divide(TWENTY_EIGHT, NUMBER_OF_DECIMALS, ROUNDING_METHOD));

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRation_TargetDateOnEndDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 2, 27);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue;
        expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        expectedValue = expectedValue.add(ONE);
        expectedValue = expectedValue.add(TWELVE.divide(TWENTY_EIGHT, NUMBER_OF_DECIMALS, ROUNDING_METHOD));

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRation_TargetDateAfterEndDate() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 3, 7);
        DateTime endDate = buildDateTime(2011, 2, 27);

        BigDecimal expectedValue;
        expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        expectedValue = expectedValue.add(ONE);
        expectedValue = expectedValue.add(TWELVE.divide(TWENTY_EIGHT, NUMBER_OF_DECIMALS, ROUNDING_METHOD));

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }

    @Test
    public void testDoubleProRationWithMultiplePeriods_TargetDateInSecondFullBillingPeriod() throws InvalidDateSequenceException {
        DateTime startDate = buildDateTime(2011, 1, 1);
        DateTime targetDate = buildDateTime(2011, 2, 26);
        DateTime endDate = buildDateTime(2011, 4, 27);

        BigDecimal expectedValue;
        expectedValue = FOURTEEN.divide(THIRTY_ONE, NUMBER_OF_DECIMALS, ROUNDING_METHOD);
        expectedValue = expectedValue.add(TWO);

        testCalculateNumberOfBillingCycles(startDate, endDate, targetDate, 15, expectedValue);
    }
}
