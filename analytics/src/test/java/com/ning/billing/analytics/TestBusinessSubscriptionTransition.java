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

import com.ning.billing.catalog.api.Catalog;
import com.ning.billing.catalog.api.CatalogService;
import com.ning.billing.catalog.api.PhaseType;
import com.ning.billing.catalog.api.Plan;
import com.ning.billing.catalog.api.PlanPhase;
import com.ning.billing.catalog.api.Product;
import com.ning.billing.catalog.api.ProductCategory;
import com.ning.billing.entitlement.api.user.Subscription;
import com.ning.billing.mock.BrainDeadProxyFactory;
import com.ning.billing.mock.BrainDeadProxyFactory.ZombieControl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.ning.billing.catalog.api.Currency.USD;

public class TestBusinessSubscriptionTransition
{
    private BusinessSubscription prevSubscription;
    private BusinessSubscription nextSubscription;
    private BusinessSubscriptionEvent event;
    private DateTime requestedTimestamp;
    private UUID id;
    private String key;
    private String accountKey;
    private BusinessSubscriptionTransition transition;

    private final CatalogService catalogService = BrainDeadProxyFactory.createBrainDeadProxyFor(CatalogService.class);
    private final Catalog catalog = BrainDeadProxyFactory.createBrainDeadProxyFor(Catalog.class);
    
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception
    {
        final Product product = new MockProduct("platinium", "subscription", ProductCategory.BASE);
        final Plan plan = new MockPlan("platinum-monthly", product);
        final PlanPhase phase = new MockPhase(PhaseType.EVERGREEN, plan, MockDuration.UNLIMITED(), 25.95);
        final Subscription prevISubscription = new MockSubscription(Subscription.SubscriptionState.ACTIVE, plan, phase);
        final Subscription nextISubscription = new MockSubscription(Subscription.SubscriptionState.CANCELLED, plan, phase);

        ((ZombieControl) catalog).addResult("findPlan", plan);
        ((ZombieControl) catalog).addResult("findPhase", phase);        
        ((ZombieControl) catalogService).addResult("getFullCatalog", catalog); 
        
        DateTime now = new DateTime();
        
        prevSubscription = new BusinessSubscription(prevISubscription, USD, catalog);
        nextSubscription = new BusinessSubscription(nextISubscription, USD, catalog);
        event = BusinessSubscriptionEvent.subscriptionCancelled(prevISubscription.getCurrentPlan().getName(), catalog, now, now);
        requestedTimestamp = new DateTime(DateTimeZone.UTC);
        id = UUID.randomUUID();
        key = "1234";
        accountKey = "pierre-1234";
        transition = new BusinessSubscriptionTransition(id, key, accountKey, requestedTimestamp, event, prevSubscription, nextSubscription);
    }

    @Test(groups = "fast")
    public void testConstructor() throws Exception
    {
        Assert.assertEquals(transition.getEvent(), event);
        Assert.assertEquals(transition.getPreviousSubscription(), prevSubscription);
        Assert.assertEquals(transition.getNextSubscription(), nextSubscription);
        Assert.assertEquals(transition.getRequestedTimestamp(), requestedTimestamp);
    }

    @Test(groups = "fast")
    public void testEquals() throws Exception
    {
        Assert.assertSame(transition, transition);
        Assert.assertEquals(transition, transition);
        Assert.assertTrue(transition.equals(transition));

        BusinessSubscriptionTransition otherTransition;

        otherTransition = new BusinessSubscriptionTransition(id, key, accountKey, new DateTime(), event, prevSubscription, nextSubscription);
        Assert.assertTrue(!transition.equals(otherTransition));

        otherTransition = new BusinessSubscriptionTransition(id, "12345", accountKey, requestedTimestamp, event, prevSubscription, nextSubscription);
        Assert.assertTrue(!transition.equals(otherTransition));

        otherTransition = new BusinessSubscriptionTransition(id, key, accountKey, requestedTimestamp, event, prevSubscription, prevSubscription);
        Assert.assertTrue(!transition.equals(otherTransition));

        otherTransition = new BusinessSubscriptionTransition(id, key, accountKey, requestedTimestamp, event, nextSubscription, nextSubscription);
        Assert.assertTrue(!transition.equals(otherTransition));

        otherTransition = new BusinessSubscriptionTransition(id, key, accountKey, requestedTimestamp, event, nextSubscription, prevSubscription);
        Assert.assertTrue(!transition.equals(otherTransition));
    }

    @Test(groups = "fast")
    public void testRejectInvalidTransitions() throws Exception
    {
        try {
            new BusinessSubscriptionTransition(null, key, accountKey, requestedTimestamp, event, prevSubscription, nextSubscription);
            Assert.fail();
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        try {
            new BusinessSubscriptionTransition(id, null, accountKey, requestedTimestamp, event, prevSubscription, nextSubscription);
            Assert.fail();
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        try {
            new BusinessSubscriptionTransition(id, key, accountKey, null, event, prevSubscription, nextSubscription);
            Assert.fail();
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        try {
            new BusinessSubscriptionTransition(id, key, accountKey, requestedTimestamp, null, prevSubscription, nextSubscription);
            Assert.fail();
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }
}
