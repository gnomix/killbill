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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import com.ning.billing.catalog.api.PlanPhaseSpecifier;
import com.ning.billing.entitlement.api.user.EntitlementUserApi;
import com.ning.billing.entitlement.api.user.EntitlementUserApiException;
import com.ning.billing.entitlement.api.user.Subscription;
import com.ning.billing.entitlement.api.user.SubscriptionBundle;
import com.ning.billing.entitlement.api.user.SubscriptionStatusDryRun;
import com.ning.billing.junction.api.BlockingState;
import com.ning.billing.overdue.OverdueState;
import com.ning.billing.util.callcontext.CallContext;

public class MockEntitlementUserApi implements EntitlementUserApi
{
    private final Map<UUID, String> subscriptionBundles = new HashMap<UUID, String>();

    public MockEntitlementUserApi(final UUID bundleUUID, final String key)
    {
        subscriptionBundles.put(bundleUUID, key);
    }

    @Override
    public SubscriptionBundle getBundleFromId(final UUID id)
    {
        final String key = subscriptionBundles.get(id);
        if (key == null) {
            return null;
        }

        return new SubscriptionBundle()
        {
            @Override
            public UUID getAccountId()
            {
                return UUID.randomUUID();
            }

            @Override
            public UUID getId()
            {
                return id;
            }

            @Override
            public DateTime getStartDate()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getKey()
            {
                return key;
            }

            @Override
            public OverdueState<SubscriptionBundle> getOverdueState() {
                throw new UnsupportedOperationException();
            }

            @Override
            public BlockingState getBlockingState() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Subscription getSubscriptionFromId(final UUID id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SubscriptionBundle> getBundlesForAccount(final UUID accountId)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Subscription> getSubscriptionsForBundle(final UUID bundleId)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public SubscriptionBundle createBundleForAccount(final UUID accountId, final String bundleKey, CallContext context) throws EntitlementUserApiException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Subscription> getSubscriptionsForKey(String bundleKey) {
        throw new UnsupportedOperationException();
    }

	@Override
	public Subscription createSubscription(UUID bundleId, PlanPhaseSpecifier spec,
			DateTime requestedDate, CallContext context) throws EntitlementUserApiException {
        throw new UnsupportedOperationException();
	}

    @Override
    public SubscriptionBundle getBundleForKey(String bundleKey) {
        throw new UnsupportedOperationException();
    }

	@Override
	public DateTime getNextBillingDate(UUID account) {
		throw new UnsupportedOperationException();
	}

    @Override
    public Subscription getBaseSubscription(UUID bundleId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SubscriptionStatusDryRun> getDryRunChangePlanStatus(
            UUID subscriptionId, String productName, DateTime requestedDate)
            throws EntitlementUserApiException {
        throw new UnsupportedOperationException();
    }
}
