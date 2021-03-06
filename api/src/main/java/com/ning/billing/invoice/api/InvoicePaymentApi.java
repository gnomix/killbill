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

package com.ning.billing.invoice.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.ning.billing.util.callcontext.CallContext;
import org.joda.time.DateTime;

import com.ning.billing.catalog.api.Currency;

public interface InvoicePaymentApi {

    /**
     * @param accountId
     * @return All invoices, including migrated invoices
     */
    public List<Invoice> getAllInvoicesByAccount(UUID accountId);

    public Invoice getInvoice(UUID invoiceId);

    public Invoice getInvoiceForPaymentAttemptId(UUID paymentAttemptId);

    public InvoicePayment getInvoicePayment(UUID paymentAttemptId);

    public void notifyOfPaymentAttempt(InvoicePayment invoicePayment, CallContext context);

    public void notifyOfPaymentAttempt(UUID invoiceId, BigDecimal amountOutstanding, Currency currency, UUID paymentAttemptId, DateTime paymentAttemptDate, CallContext context);
    
    public void notifyOfPaymentAttempt(UUID invoiceId, UUID paymentAttemptId, DateTime paymentAttemptDate, CallContext context);

}
