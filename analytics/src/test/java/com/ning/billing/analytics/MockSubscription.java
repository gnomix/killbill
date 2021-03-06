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

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.catalog.api.Plan;
import com.ning.billing.catalog.api.PlanPhase;
import com.ning.billing.catalog.api.PlanPhaseSpecifier;
import com.ning.billing.catalog.api.PriceList;
import com.ning.billing.catalog.api.ProductCategory;
import com.ning.billing.entitlement.api.user.EntitlementUserApiException;
import com.ning.billing.entitlement.api.user.Subscription;
import com.ning.billing.entitlement.api.user.SubscriptionEvent;
import com.ning.billing.junction.api.BlockingState;
import com.ning.billing.util.callcontext.CallContext;

public class MockSubscription implements Subscription
{
    private static final UUID ID = UUID.randomUUID();
    private static final UUID BUNDLE_ID = UUID.randomUUID();
    private static final DateTime START_DATE = new DateTime(DateTimeZone.UTC);

    private final SubscriptionState state;
    private final Plan plan;
    private final PlanPhase phase;

    public MockSubscription(final SubscriptionState state, final Plan plan, final PlanPhase phase)
    {
        this.state = state;
        this.plan = plan;
        this.phase = phase;
    }

    @Override
    public boolean cancel(DateTime requestedDate, boolean eot, CallContext context)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean changePlan(final String productName, final BillingPeriod term, final String planSet, DateTime requestedDate, CallContext context)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID getId()
    {
        return ID;
    }

    @Override
    public UUID getBundleId()
    {
        return BUNDLE_ID;
    }

    @Override
    public SubscriptionState getState()
    {
        return state;
    }

    @Override
    public DateTime getStartDate()
    {
        return START_DATE;
    }

    @Override
    public Plan getCurrentPlan()
    {
        return plan;
    }

    @Override
    public PlanPhase getCurrentPhase()
    {
        return phase;
    }


    @Override
    public boolean uncancel(CallContext context) throws EntitlementUserApiException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PriceList getCurrentPriceList()
    {
        return null;
    }

    @Override
    public DateTime getEndDate() {
        return null;
    }

    @Override
    public SubscriptionEvent getPendingTransition() {
        throw new UnsupportedOperationException();
    }

	@Override
	public DateTime getChargedThroughDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DateTime getPaidThroughDate() {
        throw new UnsupportedOperationException();
	}

    @Override
    public SubscriptionEvent getPreviousTransition() {
        return null;
    }

    @Override
    public ProductCategory getCategory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean recreate(PlanPhaseSpecifier spec, DateTime requestedDate, CallContext context)
            throws EntitlementUserApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SubscriptionEvent> getBillingTransitions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockingState getBlockingState() {
        throw new UnsupportedOperationException();
    }
}
