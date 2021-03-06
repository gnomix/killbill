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

package com.ning.billing.invoice.api.migration;

import com.ning.billing.invoice.MockModule;
import com.ning.billing.invoice.api.InvoiceNotifier;
import com.ning.billing.invoice.glue.DefaultInvoiceModule;
import com.ning.billing.invoice.notification.NextBillingDateNotifier;
import com.ning.billing.invoice.notification.NextBillingDatePoster;
import com.ning.billing.invoice.notification.NullInvoiceNotifier;
import com.ning.billing.mock.BrainDeadProxyFactory;
import com.ning.billing.mock.BrainDeadProxyFactory.ZombieControl;
import com.ning.billing.util.email.templates.TemplateModule;

public class MockModuleNoEntitlement extends MockModule {
//	@Override
//	protected void installEntitlementModule() {
//		BillingApi entitlementApi = BrainDeadProxyFactory.createBrainDeadProxyFor(BillingApi.class);
//        ((ZombieControl)entitlementApi).addResult("setChargedThroughDateFromTransaction", BrainDeadProxyFactory.ZOMBIE_VOID);
//        ((ZombieControl)entitlementApi).addResult("getBillingEventsForAccountAndUpdateAccountBCD", BrainDeadProxyFactory.ZOMBIE_VOID);
//		//bind(BillingApi.class).toInstance(entitlementApi);
////        bind(EntitlementUserApi.class).toInstance(BrainDeadProxyFactory.createBrainDeadProxyFor(EntitlementUserApi.class));
//        ChargeThruApi cta = BrainDeadProxyFactory.createBrainDeadProxyFor(ChargeThruApi.class);
//        bind(ChargeThruApi.class).toInstance(cta);
//        ((ZombieControl)cta).addResult("setChargedThroughDateFromTransaction", BrainDeadProxyFactory.ZOMBIE_VOID);
//	}

	@Override
	protected void installInvoiceModule() {
		install(new DefaultInvoiceModule(){

			@Override
			protected void installNotifiers() {
				 bind(NextBillingDateNotifier.class).toInstance(BrainDeadProxyFactory.createBrainDeadProxyFor(NextBillingDateNotifier.class));
				 NextBillingDatePoster poster = BrainDeadProxyFactory.createBrainDeadProxyFor(NextBillingDatePoster.class);
				 ((ZombieControl)poster).addResult("insertNextBillingNotification",BrainDeadProxyFactory.ZOMBIE_VOID);
			     bind(NextBillingDatePoster.class).toInstance(poster);
                bind(InvoiceNotifier.class).to(NullInvoiceNotifier.class).asEagerSingleton();
			}
			
			
		});
        install(new TemplateModule());

		
	}

}
