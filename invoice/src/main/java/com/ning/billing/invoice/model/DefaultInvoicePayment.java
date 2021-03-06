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

package com.ning.billing.invoice.model;

import com.ning.billing.catalog.api.Currency;
import com.ning.billing.invoice.api.InvoicePayment;
import com.ning.billing.util.entity.EntityBase;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.UUID;

public class DefaultInvoicePayment extends EntityBase implements InvoicePayment {
    private final UUID paymentAttemptId;
    private final UUID invoiceId;
    private final DateTime paymentDate;
    private final BigDecimal amount;
    private final Currency currency;

    public DefaultInvoicePayment(final UUID paymentAttemptId, final UUID invoiceId, final DateTime paymentDate) {
        this(UUID.randomUUID(), paymentAttemptId, invoiceId, paymentDate, null, null);
    }

    public DefaultInvoicePayment(final UUID paymentAttemptId, final UUID invoiceId, final DateTime paymentDate,
                                 final BigDecimal amount, final Currency currency) {
        this(UUID.randomUUID(), paymentAttemptId, invoiceId, paymentDate, amount, currency);
    }

    public DefaultInvoicePayment(final UUID id, final UUID paymentAttemptId, final UUID invoiceId, final DateTime paymentDate,
                                 @Nullable final BigDecimal amount, @Nullable final Currency currency) {
        super(id);
        this.paymentAttemptId = paymentAttemptId;
        this.amount = amount;
        this.invoiceId = invoiceId;
        this.paymentDate = paymentDate;
        this.currency = currency;
    }

    @Override
    public UUID getPaymentAttemptId() {
        return paymentAttemptId;
    }

    @Override
    public UUID getInvoiceId() {
        return invoiceId;
    }

    @Override
    public DateTime getPaymentAttemptDate() {
        return paymentDate;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }
}
