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

package com.ning.billing.invoice.glue;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.skife.jdbi.v2.IDBI;

import com.ning.billing.account.api.AccountUserApi;
import com.ning.billing.catalog.glue.CatalogModule;
import com.ning.billing.dbi.MysqlTestingHelper;
import com.ning.billing.invoice.api.InvoiceNotifier;
import com.ning.billing.invoice.api.test.DefaultInvoiceTestApi;
import com.ning.billing.invoice.api.test.InvoiceTestApi;
import com.ning.billing.invoice.dao.InvoicePaymentSqlDao;
import com.ning.billing.invoice.dao.RecurringInvoiceItemSqlDao;
import com.ning.billing.invoice.notification.MockNextBillingDateNotifier;
import com.ning.billing.invoice.notification.MockNextBillingDatePoster;
import com.ning.billing.invoice.notification.NextBillingDateNotifier;
import com.ning.billing.invoice.notification.NextBillingDatePoster;
import com.ning.billing.invoice.notification.NullInvoiceNotifier;
import com.ning.billing.junction.api.BillingApi;
import com.ning.billing.mock.BrainDeadProxyFactory;
import com.ning.billing.mock.BrainDeadProxyFactory.ZombieControl;
import com.ning.billing.mock.glue.MockEntitlementModule;
import com.ning.billing.util.callcontext.CallContextFactory;
import com.ning.billing.util.callcontext.DefaultCallContextFactory;
import com.ning.billing.util.clock.Clock;
import com.ning.billing.util.clock.DefaultClock;
import com.ning.billing.util.email.templates.TemplateModule;
import com.ning.billing.util.glue.BusModule;
import com.ning.billing.util.glue.CustomFieldModule;
import com.ning.billing.util.glue.GlobalLockerModule;
import com.ning.billing.util.glue.TagStoreModule;
import com.ning.billing.util.notificationq.MockNotificationQueueService;
import com.ning.billing.util.notificationq.NotificationQueueService;

public class InvoiceModuleWithEmbeddedDb extends DefaultInvoiceModule {
    private final MysqlTestingHelper helper = new MysqlTestingHelper();
    private IDBI dbi;

    public void startDb() throws IOException {
        helper.startMysql();
    }

    public void initDb(final String ddl) throws IOException {
        helper.initDb(ddl);
    }

    public void stopDb() {
        helper.stopMysql();
    }

    public IDBI getDbi() {
        return dbi;
    }

    public RecurringInvoiceItemSqlDao getInvoiceItemSqlDao() {
        return dbi.onDemand(RecurringInvoiceItemSqlDao.class);
    }

    public InvoicePaymentSqlDao getInvoicePaymentSqlDao() {
        return dbi.onDemand(InvoicePaymentSqlDao.class);
    }

    private void installNotificationQueue() {
        bind(NotificationQueueService.class).to(MockNotificationQueueService.class).asEagerSingleton();
    }

    @Override
    protected void installNotifiers() {
        bind(NextBillingDateNotifier.class).to(MockNextBillingDateNotifier.class).asEagerSingleton();
        bind(NextBillingDatePoster.class).to(MockNextBillingDatePoster.class).asEagerSingleton();
        bind(InvoiceNotifier.class).to(NullInvoiceNotifier.class).asEagerSingleton();
    }

    @Override
    public void configure() {
        loadSystemPropertiesFromClasspath("/resource.properties");

        dbi = helper.getDBI();
        bind(IDBI.class).toInstance(dbi);

        bind(Clock.class).to(DefaultClock.class).asEagerSingleton();
        bind(CallContextFactory.class).to(DefaultCallContextFactory.class).asEagerSingleton();
        install(new CustomFieldModule());
        install(new TagStoreModule());

        installNotificationQueue();
//      install(new AccountModule());
        bind(AccountUserApi.class).toInstance(BrainDeadProxyFactory.createBrainDeadProxyFor(AccountUserApi.class));

        BillingApi billingApi = BrainDeadProxyFactory.createBrainDeadProxyFor(BillingApi.class);
        ((ZombieControl) billingApi).addResult("setChargedThroughDateFromTransaction", BrainDeadProxyFactory.ZOMBIE_VOID);
        bind(BillingApi.class).toInstance(billingApi);

        install(new CatalogModule());
        install(new MockEntitlementModule());
        install(new GlobalLockerModule());

        super.configure();

        bind(InvoiceTestApi.class).to(DefaultInvoiceTestApi.class).asEagerSingleton();
        install(new BusModule());
        install(new TemplateModule());

    }

    private static void loadSystemPropertiesFromClasspath(final String resource) {
        final URL url = InvoiceModuleWithEmbeddedDb.class.getResource(resource);
        assertNotNull(url);
        try {
            System.getProperties().load( url.openStream() );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
