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

package com.ning.billing.invoice.dao;

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.TransactionCallback;
import org.skife.jdbi.v2.TransactionStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.ning.billing.config.InvoiceConfig;
import com.ning.billing.invoice.glue.InvoiceModuleWithEmbeddedDb;
import com.ning.billing.invoice.model.DefaultInvoiceGenerator;
import com.ning.billing.invoice.model.InvoiceGenerator;
import com.ning.billing.invoice.tests.InvoicingTestBase;
import com.ning.billing.util.bus.BusService;
import com.ning.billing.util.bus.DefaultBusService;
import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.callcontext.CallOrigin;
import com.ning.billing.util.callcontext.DefaultCallContextFactory;
import com.ning.billing.util.callcontext.UserType;
import com.ning.billing.util.clock.Clock;

public abstract class InvoiceDaoTestBase extends InvoicingTestBase {
    protected IDBI dbi;
    protected InvoiceDao invoiceDao;
    protected RecurringInvoiceItemSqlDao recurringInvoiceItemDao;
    protected InvoicePaymentSqlDao invoicePaymentDao;
    protected InvoiceModuleWithEmbeddedDb module;
    protected Clock clock;
    protected CallContext context;
    protected InvoiceGenerator generator;

    private final InvoiceConfig invoiceConfig = new InvoiceConfig() {
        @Override
        public long getSleepTimeMs() {throw new UnsupportedOperationException();}
        @Override
        public boolean isNotificationProcessingOff() {throw new UnsupportedOperationException();}
        @Override
        public int getNumberOfMonthsInFuture() {return 36;}
    };

    @BeforeClass(alwaysRun = true)
    protected void setup() throws IOException {
        module = new InvoiceModuleWithEmbeddedDb();
        dbi = module.getDbi();

        final String invoiceDdl = IOUtils.toString(DefaultInvoiceDao.class.getResourceAsStream("/com/ning/billing/invoice/ddl.sql"));
        final String utilDdl = IOUtils.toString(DefaultInvoiceDao.class.getResourceAsStream("/com/ning/billing/util/ddl.sql"));

        module.startDb();
        module.initDb(invoiceDdl);
        module.initDb(utilDdl);

        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, module);

        invoiceDao = injector.getInstance(InvoiceDao.class);
        invoiceDao.test();

        recurringInvoiceItemDao = module.getInvoiceItemSqlDao();

        invoicePaymentDao = module.getInvoicePaymentSqlDao();
        clock = injector.getInstance(Clock.class);
        context = new DefaultCallContextFactory(clock).createCallContext("Count Rogan", CallOrigin.TEST, UserType.TEST);
        generator = new DefaultInvoiceGenerator(clock, invoiceConfig);

        BusService busService = injector.getInstance(BusService.class);
        ((DefaultBusService) busService).startBus();

        assertTrue(true);
       
    }

    @BeforeMethod(alwaysRun = true)
    public void cleanupData() {
        module.getDbi().inTransaction(new TransactionCallback<Void>() {
            @Override
            public Void inTransaction(Handle h, TransactionStatus status)
                    throws Exception {
                h.execute("truncate table invoices");
                h.execute("truncate table fixed_invoice_items");
                h.execute("truncate table recurring_invoice_items");

                return null;
            }
        });
    }

    @AfterClass(alwaysRun = true)
    protected void tearDown() {
        module.stopDb();
        assertTrue(true);
    }
}
