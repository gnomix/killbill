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

package com.ning.billing.payment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.ning.billing.account.api.Account;
import com.ning.billing.catalog.api.Currency;
import com.ning.billing.invoice.api.Invoice;
import com.ning.billing.invoice.api.InvoicePaymentApi;
import com.ning.billing.mock.BrainDeadProxyFactory;
import com.ning.billing.mock.BrainDeadProxyFactory.ZombieControl;
import com.ning.billing.payment.MockRecurringInvoiceItem;
import com.ning.billing.payment.TestHelper;
import com.ning.billing.util.bus.Bus;
import com.ning.billing.util.bus.Bus.EventBusException;
import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.callcontext.CallOrigin;
import com.ning.billing.util.callcontext.DefaultCallContext;
import com.ning.billing.util.callcontext.UserType;
import com.ning.billing.util.clock.Clock;

public abstract class TestPaymentApi {
    @Inject
    private Bus eventBus;
    @Inject
    protected PaymentApi paymentApi;
    @Inject
    protected TestHelper testHelper;
    @Inject
    protected InvoicePaymentApi invoicePaymentApi;

    protected CallContext context;

    @Inject
    public TestPaymentApi(Clock clock) {
        context = new DefaultCallContext("Payment Tests", CallOrigin.INTERNAL, UserType.SYSTEM, clock);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws EventBusException {
        eventBus.start();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws EventBusException {
        eventBus.stop();
    }

    @Test(enabled=true)
    public void testCreateCreditCardPayment() throws Exception {
        ((ZombieControl)invoicePaymentApi).addResult("notifyOfPaymentAttempt", BrainDeadProxyFactory.ZOMBIE_VOID);

        final DateTime now = new DateTime(DateTimeZone.UTC);
        final Account account = testHelper.createTestCreditCardAccount();
        final Invoice invoice = testHelper.createTestInvoice(account, now, Currency.USD);
        final BigDecimal amount = new BigDecimal("10.0011");
        final UUID subscriptionId = UUID.randomUUID();
        final UUID bundleId = UUID.randomUUID();

        invoice.addInvoiceItem(new MockRecurringInvoiceItem(invoice.getId(), account.getId(),
                                                       subscriptionId,
                                                       bundleId,
                                                       "test plan", "test phase",
                                                       now,
                                                       now.plusMonths(1),
                                                       amount,
                                                       new BigDecimal("1.0"),
                                                       Currency.USD));

        List<PaymentInfoEvent> results = paymentApi.createPayment(account.getExternalKey(), Arrays.asList(invoice.getId().toString()), context);

        assertEquals(results.size(), 1);

        PaymentInfoEvent paymentInfo = results.get(0);

        assertNotNull(paymentInfo.getId());
        assertTrue(paymentInfo.getAmount().compareTo(amount.setScale(2, RoundingMode.HALF_EVEN)) == 0);
        assertNotNull(paymentInfo.getPaymentNumber());
        assertFalse(paymentInfo.getStatus().equals("Error"));

        PaymentAttempt paymentAttempt = paymentApi.getPaymentAttemptForPaymentId(paymentInfo.getId());
        assertNotNull(paymentAttempt);
        assertNotNull(paymentAttempt.getId());
        assertEquals(paymentAttempt.getInvoiceId(), invoice.getId());
        assertTrue(paymentAttempt.getAmount().compareTo(amount.setScale(2, RoundingMode.HALF_EVEN)) == 0);
        assertEquals(paymentAttempt.getCurrency(), Currency.USD);
        assertEquals(paymentAttempt.getPaymentId(), paymentInfo.getId());
        DateTime nowTruncated = now.withMillisOfSecond(0).withSecondOfMinute(0);
        DateTime paymentAttemptDateTruncated = paymentAttempt.getPaymentAttemptDate().withMillisOfSecond(0).withSecondOfMinute(0);
        assertEquals(paymentAttemptDateTruncated.compareTo(nowTruncated), 0);

        List<PaymentInfoEvent> paymentInfos = paymentApi.getPaymentInfoList(Arrays.asList(invoice.getId().toString()));
        assertNotNull(paymentInfos);
        assertTrue(paymentInfos.size() > 0);

        PaymentInfoEvent paymentInfoFromGet = paymentInfos.get(0);
        assertEquals(paymentInfo.getAmount(), paymentInfoFromGet.getAmount());
        assertEquals(paymentInfo.getRefundAmount(), paymentInfoFromGet.getRefundAmount());
        assertEquals(paymentInfo.getId(), paymentInfoFromGet.getId());
        assertEquals(paymentInfo.getPaymentNumber(), paymentInfoFromGet.getPaymentNumber());
        assertEquals(paymentInfo.getStatus(), paymentInfoFromGet.getStatus());
        assertEquals(paymentInfo.getBankIdentificationNumber(), paymentInfoFromGet.getBankIdentificationNumber());
        assertEquals(paymentInfo.getReferenceId(), paymentInfoFromGet.getReferenceId());
        assertEquals(paymentInfo.getPaymentMethodId(), paymentInfoFromGet.getPaymentMethodId());
        assertEquals(paymentInfo.getEffectiveDate(), paymentInfoFromGet.getEffectiveDate());

        List<PaymentAttempt> paymentAttemptsFromGet = paymentApi.getPaymentAttemptsForInvoiceId(invoice.getId().toString());
        assertEquals(paymentAttempt, paymentAttemptsFromGet.get(0));

    }

    private PaymentProviderAccount setupAccountWithPaypalPaymentMethod() throws Exception  {
        final Account account = testHelper.createTestPayPalAccount();
        paymentApi.createPaymentProviderAccount(account, context);

        String accountKey = account.getExternalKey();

        PaypalPaymentMethodInfo paymentMethod = new PaypalPaymentMethodInfo.Builder()
                                                                           .setBaid("12345")
                                                                           .setEmail(account.getEmail())
                                                                           .setDefaultMethod(true)
                                                                           .build();
        String paymentMethodId = paymentApi.addPaymentMethod(accountKey, paymentMethod, context);

        PaymentMethodInfo paymentMethodInfo = paymentApi.getPaymentMethod(accountKey, paymentMethodId);

        return paymentApi.getPaymentProviderAccount(accountKey);
    }

    @Test(enabled=true)
    public void testCreatePaypalPaymentMethod() throws Exception  {
        PaymentProviderAccount account = setupAccountWithPaypalPaymentMethod();
        assertNotNull(account);
        paymentApi.getPaymentMethods(account.getAccountKey());
    }

    @Test(enabled=true)
    public void testUpdatePaymentProviderAccountContact() throws Exception {
        final Account account = testHelper.createTestPayPalAccount();
        paymentApi.createPaymentProviderAccount(account, context);

        Account updatedAccount = BrainDeadProxyFactory.createBrainDeadProxyFor(Account.class);
        ZombieControl zombieAccount = (ZombieControl) updatedAccount;
        zombieAccount.addResult("getId", account.getId());
        zombieAccount.addResult("getName", "Tester " + RandomStringUtils.randomAlphanumeric(10));
        zombieAccount.addResult("getFirstNameLength", 6);
        zombieAccount.addResult("getExternalKey", account.getExternalKey());
        zombieAccount.addResult("getPhone", "888-888-" + RandomStringUtils.randomNumeric(4));
        zombieAccount.addResult("getEmail", account.getEmail());
        zombieAccount.addResult("getCurrency", account.getCurrency());
        zombieAccount.addResult("getBillCycleDay", account.getBillCycleDay());

        paymentApi.updatePaymentProviderAccountContact(updatedAccount.getExternalKey(), context);
    }

    @Test(enabled=true)
    public void testCannotDeleteDefaultPaymentMethod() throws Exception  {
        PaymentProviderAccount account = setupAccountWithPaypalPaymentMethod();
        paymentApi.deletePaymentMethod(account.getAccountKey(), account.getDefaultPaymentMethodId(), context);
    }
}
