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

package com.ning.billing.entitlement.api.user;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.ning.billing.api.TestApiListener.NextEvent;
import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.catalog.api.CatalogApiException;
import com.ning.billing.catalog.api.Duration;
import com.ning.billing.catalog.api.PhaseType;
import com.ning.billing.catalog.api.Plan;
import com.ning.billing.catalog.api.PlanAlignmentCreate;
import com.ning.billing.catalog.api.PlanPhase;
import com.ning.billing.catalog.api.PlanSpecifier;
import com.ning.billing.catalog.api.PriceListSet;
import com.ning.billing.catalog.api.ProductCategory;
import com.ning.billing.entitlement.api.TestApiBase;
import com.ning.billing.entitlement.api.user.Subscription.SubscriptionState;
import com.ning.billing.entitlement.api.user.SubscriptionStatusDryRun.DryRunChangeReason;
import com.ning.billing.entitlement.glue.MockEngineModuleSql;
import com.ning.billing.util.clock.DefaultClock;

public class TestUserApiAddOn extends TestApiBase {

    @Override
    public Injector getInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT, new MockEngineModuleSql());
    }

    @Test(enabled=true, groups={"slow"})
    public void testCreateCancelAddon() {

        log.info("Starting testCreateCancelAddon");

        try {
            String baseProduct = "Shotgun";
            BillingPeriod baseTerm = BillingPeriod.MONTHLY;
            String basePriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            createSubscription(baseProduct, baseTerm, basePriceList);

            String aoProduct = "Telescopic-Scope";
            BillingPeriod aoTerm = BillingPeriod.MONTHLY;
            String aoPriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            SubscriptionData aoSubscription = createSubscription(aoProduct, aoTerm, aoPriceList);
            assertEquals(aoSubscription.getState(), SubscriptionState.ACTIVE);

            DateTime now = clock.getUTCNow();
            aoSubscription.cancel(now, false, context);

            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.CANCEL);
            assertTrue(testListener.isCompleted(5000));

            assertEquals(aoSubscription.getState(), SubscriptionState.CANCELLED);

            assertListenerStatus();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(enabled=true, groups={"slow"})
    public void testCancelBPWithAddon() {

        log.info("Starting testCancelBPWithAddon");

        try {

            String baseProduct = "Shotgun";
            BillingPeriod baseTerm = BillingPeriod.MONTHLY;
            String basePriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            // CREATE BP
            SubscriptionData baseSubscription = createSubscription(baseProduct, baseTerm, basePriceList);

            String aoProduct = "Telescopic-Scope";
            BillingPeriod aoTerm = BillingPeriod.MONTHLY;
            String aoPriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            SubscriptionData aoSubscription = createSubscription(aoProduct, aoTerm, aoPriceList);

            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.PHASE);
            testListener.pushExpectedEvent(NextEvent.PHASE);

            // MOVE CLOCK AFTER TRIAL + AO DISCOUNT
            Interval it = new Interval(clock.getUTCNow(), clock.getUTCNow().plusMonths(2));
            clock.addDeltaFromReality(it.toDurationMillis());
            assertTrue(testListener.isCompleted(5000));

            // SET CTD TO CANCEL IN FUTURE
            DateTime now = clock.getUTCNow();
            Duration ctd = getDurationMonth(1);
            // Why not just use clock.getUTCNow().plusMonths(1) ?
            DateTime newChargedThroughDate = DefaultClock.addDuration(now, ctd);
            billingApi.setChargedThroughDate(baseSubscription.getId(), newChargedThroughDate, context);
            baseSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(baseSubscription.getId());

            // FUTURE CANCELLATION
            baseSubscription.cancel(now, false, context);

            // REFETCH AO SUBSCRIPTION AND CHECK THIS IS ACTIVE
            aoSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(aoSubscription.getId());
            assertEquals(aoSubscription.getState(), SubscriptionState.ACTIVE);
            assertTrue(aoSubscription.isSubscriptionFutureCancelled());

            // MOVE AFTER CANCELLATION
            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.CANCEL);
            testListener.pushExpectedEvent(NextEvent.CANCEL);
            
            it = new Interval(clock.getUTCNow(), clock.getUTCNow().plusMonths(1));
            clock.addDeltaFromReality(it.toDurationMillis());
            assertTrue(testListener.isCompleted(5000));

            // REFETCH AO SUBSCRIPTION AND CHECK THIS IS CANCELLED
            aoSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(aoSubscription.getId());
            assertEquals(aoSubscription.getState(), SubscriptionState.CANCELLED);

            assertListenerStatus();

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test(enabled=true, groups={"slow"})
    public void testChangeBPWithAddonIncluded() {

        log.info("Starting testChangeBPWithAddonIncluded");

        try {

            String baseProduct = "Shotgun";
            BillingPeriod baseTerm = BillingPeriod.MONTHLY;
            String basePriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            // CREATE BP
            SubscriptionData baseSubscription = createSubscription(baseProduct, baseTerm, basePriceList);

            String aoProduct = "Telescopic-Scope";
            BillingPeriod aoTerm = BillingPeriod.MONTHLY;
            String aoPriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            SubscriptionData aoSubscription = createSubscription(aoProduct, aoTerm, aoPriceList);

            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.PHASE);
            testListener.pushExpectedEvent(NextEvent.PHASE);

            // MOVE CLOCK AFTER TRIAL + AO DISCOUNT
            Interval it = new Interval(clock.getUTCNow(), clock.getUTCNow().plusMonths(2));
            clock.addDeltaFromReality(it.toDurationMillis());
            assertTrue(testListener.isCompleted(5000));

            // SET CTD TO CHANGE IN FUTURE
            DateTime now = clock.getUTCNow();
            Duration ctd = getDurationMonth(1);
            DateTime newChargedThroughDate = DefaultClock.addDuration(now, ctd);
            billingApi.setChargedThroughDate(baseSubscription.getId(), newChargedThroughDate, context);
            baseSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(baseSubscription.getId());

            // CHANGE IMMEDIATELY WITH TO BP WITH NON INCLUDED ADDON
            String newBaseProduct = "Assault-Rifle";
            BillingPeriod newBaseTerm = BillingPeriod.MONTHLY;
            String newBasePriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            List<SubscriptionStatusDryRun> aoStatus = entitlementApi.getDryRunChangePlanStatus(baseSubscription.getId(), newBaseProduct, now);
            assertEquals(aoStatus.size(), 1);
            assertEquals(aoStatus.get(0).getId(), aoSubscription.getId());
            assertEquals(aoStatus.get(0).getProductName(), aoProduct);
            assertEquals(aoStatus.get(0).getBillingPeriod(), aoTerm);            
            assertEquals(aoStatus.get(0).getPhaseType(), aoSubscription.getCurrentPhase().getPhaseType());                        
            assertEquals(aoStatus.get(0).getPriceList(), aoSubscription.getCurrentPriceList().getName());
            assertEquals(aoStatus.get(0).getReason(), DryRunChangeReason.AO_INCLUDED_IN_NEW_PLAN);            
            
            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.CHANGE);
            testListener.pushExpectedEvent(NextEvent.CANCEL);
            baseSubscription.changePlan(newBaseProduct, newBaseTerm, newBasePriceList, now, context);
            assertTrue(testListener.isCompleted(5000));

            // REFETCH AO SUBSCRIPTION AND CHECK THIS CANCELLED
            aoSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(aoSubscription.getId());
            assertEquals(aoSubscription.getState(), SubscriptionState.CANCELLED);

            assertListenerStatus();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(enabled=true, groups={"slow"})
    public void testChangeBPWithAddonNonAvailable() {

        log.info("Starting testChangeBPWithAddonNonAvailable");

        try {

            String baseProduct = "Shotgun";
            BillingPeriod baseTerm = BillingPeriod.MONTHLY;
            String basePriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            // CREATE BP
            SubscriptionData baseSubscription = createSubscription(baseProduct, baseTerm, basePriceList);

            String aoProduct = "Telescopic-Scope";
            BillingPeriod aoTerm = BillingPeriod.MONTHLY;
            String aoPriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            SubscriptionData aoSubscription = createSubscription(aoProduct, aoTerm, aoPriceList);

            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.PHASE);
            testListener.pushExpectedEvent(NextEvent.PHASE);

            // MOVE CLOCK AFTER TRIAL + AO DISCOUNT
            Interval it = new Interval(clock.getUTCNow(), clock.getUTCNow().plusMonths(2));
            clock.addDeltaFromReality(it.toDurationMillis());
            assertTrue(testListener.isCompleted(5000));

            // SET CTD TO CANCEL IN FUTURE
            DateTime now = clock.getUTCNow();
            Duration ctd = getDurationMonth(1);
            DateTime newChargedThroughDate = DefaultClock.addDuration(now, ctd);
            billingApi.setChargedThroughDate(baseSubscription.getId(), newChargedThroughDate, context);
            baseSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(baseSubscription.getId());

            // CHANGE IMMEDIATELY WITH TO BP WITH NON AVAILABLE ADDON
            String newBaseProduct = "Pistol";
            BillingPeriod newBaseTerm = BillingPeriod.MONTHLY;
            String newBasePriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            List<SubscriptionStatusDryRun> aoStatus = entitlementApi.getDryRunChangePlanStatus(baseSubscription.getId(), newBaseProduct, now);
            assertEquals(aoStatus.size(), 1);
            assertEquals(aoStatus.get(0).getId(), aoSubscription.getId());
            assertEquals(aoStatus.get(0).getProductName(), aoProduct);
            assertEquals(aoStatus.get(0).getBillingPeriod(), aoTerm);   
            assertEquals(aoStatus.get(0).getPhaseType(), aoSubscription.getCurrentPhase().getPhaseType());                                    
            assertEquals(aoStatus.get(0).getPriceList(), aoSubscription.getCurrentPriceList().getName());
            assertEquals(aoStatus.get(0).getReason(), DryRunChangeReason.AO_NOT_AVAILABLE_IN_NEW_PLAN);            
            
            baseSubscription.changePlan(newBaseProduct, newBaseTerm, newBasePriceList, now, context);

            // REFETCH AO SUBSCRIPTION AND CHECK THIS IS ACTIVE
            aoSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(aoSubscription.getId());
            assertEquals(aoSubscription.getState(), SubscriptionState.ACTIVE);
            assertTrue(aoSubscription.isSubscriptionFutureCancelled());

            // MOVE AFTER CHANGE
            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.CHANGE);
            testListener.pushExpectedEvent(NextEvent.CANCEL);
            it = new Interval(clock.getUTCNow(), clock.getUTCNow().plusMonths(1));
            clock.addDeltaFromReality(it.toDurationMillis());
            assertTrue(testListener.isCompleted(5000));


            // REFETCH AO SUBSCRIPTION AND CHECK THIS CANCELLED
            aoSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(aoSubscription.getId());
            assertEquals(aoSubscription.getState(), SubscriptionState.CANCELLED);

            assertListenerStatus();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test(enabled=true, groups={"slow"})
    public void testAddonCreateWithBundleAlign() {

        log.info("Starting testAddonCreateWithBundleAlign");

        try {
            String aoProduct = "Telescopic-Scope";
            BillingPeriod aoTerm = BillingPeriod.MONTHLY;
            String aoPriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            // This is just to double check our test catalog gives us what we want before we start the test
            PlanSpecifier planSpecifier = new PlanSpecifier(aoProduct,
                    ProductCategory.ADD_ON,
                    aoTerm,
                    aoPriceList);
            PlanAlignmentCreate alignement = catalog.planCreateAlignment(planSpecifier, clock.getUTCNow());
            assertEquals(alignement, PlanAlignmentCreate.START_OF_BUNDLE);

            testAddonCreateInternal(aoProduct, aoTerm, aoPriceList, alignement);

            assertListenerStatus();
        } catch (CatalogApiException e) {
            Assert.fail(e.getMessage());
        }
    }

    //TODO MDW - debugging reenable if you find this
    @Test(enabled=true, groups={"slow"})
    public void testAddonCreateWithSubscriptionAlign() {

        log.info("Starting testAddonCreateWithSubscriptionAlign");

        try {
            String aoProduct = "Laser-Scope";
            BillingPeriod aoTerm = BillingPeriod.MONTHLY;
            String aoPriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            // This is just to double check our test catalog gives us what we want before we start the test
            PlanSpecifier planSpecifier = new PlanSpecifier(aoProduct,
                    ProductCategory.ADD_ON,
                    aoTerm,
                    aoPriceList);
            PlanAlignmentCreate alignement = catalog.planCreateAlignment(planSpecifier, clock.getUTCNow());
            assertEquals(alignement, PlanAlignmentCreate.START_OF_SUBSCRIPTION);

            testAddonCreateInternal(aoProduct, aoTerm, aoPriceList, alignement);

            assertListenerStatus();
        } catch (CatalogApiException e) {
            Assert.fail(e.getMessage());
        }
    }


    private void testAddonCreateInternal(String aoProduct, BillingPeriod aoTerm, String aoPriceList, PlanAlignmentCreate expAlignement) {

        try {

            String baseProduct = "Shotgun";
            BillingPeriod baseTerm = BillingPeriod.MONTHLY;
            String basePriceList = PriceListSet.DEFAULT_PRICELIST_NAME;

            // CREATE BP
            SubscriptionData baseSubscription = createSubscription(baseProduct, baseTerm, basePriceList);

            // MOVE CLOCK 14 DAYS LATER
            Interval it = new Interval(clock.getUTCNow(), clock.getUTCNow().plusDays(14));
            clock.addDeltaFromReality(it.toDurationMillis());
  
            // CREATE ADDON
            DateTime beforeAOCreation = clock.getUTCNow();
            SubscriptionData aoSubscription = createSubscription(aoProduct, aoTerm, aoPriceList);
            DateTime afterAOCreation = clock.getUTCNow();

            // CHECK EVERYTHING
            Plan aoCurrentPlan = aoSubscription.getCurrentPlan();
            assertNotNull(aoCurrentPlan);
            assertEquals(aoCurrentPlan.getProduct().getName(),aoProduct);
            assertEquals(aoCurrentPlan.getProduct().getCategory(), ProductCategory.ADD_ON);
            assertEquals(aoCurrentPlan.getBillingPeriod(), aoTerm);

            PlanPhase aoCurrentPhase = aoSubscription.getCurrentPhase();
            assertNotNull(aoCurrentPhase);
            assertEquals(aoCurrentPhase.getPhaseType(), PhaseType.DISCOUNT);

            assertDateWithin(aoSubscription.getStartDate(), beforeAOCreation, afterAOCreation);
            assertEquals(aoSubscription.getBundleStartDate(), baseSubscription.getBundleStartDate());

            // CHECK next AO PHASE EVENT IS INDEED A MONTH AFTER BP STARTED => BUNDLE ALIGNMENT
            SubscriptionEvent aoPendingTranstion = aoSubscription.getPendingTransition();

            if (expAlignement == PlanAlignmentCreate.START_OF_BUNDLE) {
                assertEquals(aoPendingTranstion.getEffectiveTransitionTime(), baseSubscription.getStartDate().plusMonths(1));
            } else {
                assertEquals(aoPendingTranstion.getEffectiveTransitionTime(), aoSubscription.getStartDate().plusMonths(1));
            }

            // ADD TWO PHASE EVENTS (BP + AO)
            testListener.reset();
            testListener.pushExpectedEvent(NextEvent.PHASE);
            testListener.pushExpectedEvent(NextEvent.PHASE);

            // MOVE THROUGH TIME TO GO INTO EVERGREEN
            it = new Interval(clock.getUTCNow(), clock.getUTCNow().plusDays(33));
            clock.addDeltaFromReality(it.toDurationMillis());
            assertTrue(testListener.isCompleted(5000));


            // CHECK EVERYTHING AGAIN
            aoSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(aoSubscription.getId());

            aoCurrentPlan = aoSubscription.getCurrentPlan();
            assertNotNull(aoCurrentPlan);
            assertEquals(aoCurrentPlan.getProduct().getName(),aoProduct);
            assertEquals(aoCurrentPlan.getProduct().getCategory(), ProductCategory.ADD_ON);
            assertEquals(aoCurrentPlan.getBillingPeriod(), aoTerm);

            aoCurrentPhase = aoSubscription.getCurrentPhase();
            assertNotNull(aoCurrentPhase);
            assertEquals(aoCurrentPhase.getPhaseType(), PhaseType.EVERGREEN);


            aoSubscription = (SubscriptionData) entitlementApi.getSubscriptionFromId(aoSubscription.getId());
            aoPendingTranstion = aoSubscription.getPendingTransition();
            assertNull(aoPendingTranstion);

        } catch (EntitlementUserApiException e) {
            Assert.fail(e.getMessage());
        }
    }
}
