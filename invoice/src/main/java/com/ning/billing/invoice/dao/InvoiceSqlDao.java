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

import com.ning.billing.catalog.api.Currency;
import com.ning.billing.invoice.api.Invoice;
import com.ning.billing.invoice.model.DefaultInvoice;
import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.dao.AuditSqlDao;
import com.ning.billing.util.dao.UuidMapper;
import com.ning.billing.util.callcontext.CallContextBinder;
import com.ning.billing.util.entity.dao.EntitySqlDao;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.CloseMe;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.mixins.Transmogrifier;
import org.skife.jdbi.v2.sqlobject.stringtemplate.ExternalizedSqlViaStringTemplate3;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ExternalizedSqlViaStringTemplate3()
@RegisterMapper(InvoiceSqlDao.InvoiceMapper.class)
public interface InvoiceSqlDao extends EntitySqlDao<Invoice>, AuditSqlDao, Transactional<InvoiceSqlDao>, Transmogrifier, CloseMe {
    @Override
    @SqlUpdate
    void create(@InvoiceBinder Invoice invoice, @CallContextBinder final CallContext context);

    @SqlQuery
    List<Invoice> getInvoicesByAccount(@Bind("accountId") final String accountId);
    
    @SqlQuery
    List<Invoice> getAllInvoicesByAccount(@Bind("accountId") final String string);

    @SqlQuery
    List<Invoice> getInvoicesByAccountAfterDate(@Bind("accountId") final String accountId,
                                                @Bind("fromDate") final Date fromDate);

    @SqlQuery
    List<Invoice> getInvoicesBySubscription(@Bind("subscriptionId") final String subscriptionId);

    @SqlQuery
    @RegisterMapper(UuidMapper.class)
    UUID getInvoiceIdByPaymentAttemptId(@Bind("paymentAttemptId") final String paymentAttemptId);

    @SqlQuery
    @RegisterMapper(BalanceMapper.class)
    BigDecimal getAccountBalance(@Bind("accountId") final String accountId);

    @SqlQuery
    List<Invoice> getUnpaidInvoicesByAccountId(@Bind("accountId") final String accountId,
                                               @Bind("upToDate") final Date upToDate);
    
    

    @BindingAnnotation(InvoiceBinder.InvoiceBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface InvoiceBinder {
        public static class InvoiceBinderFactory implements BinderFactory {
            @Override
            public Binder<InvoiceBinder, Invoice> build(Annotation annotation) {
                return new Binder<InvoiceBinder, Invoice>() {
                    @Override
                    public void bind(@SuppressWarnings("rawtypes") SQLStatement q, InvoiceBinder bind, Invoice invoice) {
                        q.bind("id", invoice.getId().toString());
                        q.bind("accountId", invoice.getAccountId().toString());
                        q.bind("invoiceDate", invoice.getInvoiceDate().toDate());
                        q.bind("targetDate", invoice.getTargetDate().toDate());
                        q.bind("currency", invoice.getCurrency().toString());
                        q.bind("migrated", invoice.isMigrationInvoice());
                    }
                };
            }
        }
    }

    public static class InvoiceMapper implements ResultSetMapper<Invoice> {
        @Override
        public Invoice map(int index, ResultSet result, StatementContext context) throws SQLException {
            UUID id = UUID.fromString(result.getString("id"));
            UUID accountId = UUID.fromString(result.getString("account_id"));
            int invoiceNumber = result.getInt("invoice_number");
            DateTime invoiceDate = new DateTime(result.getTimestamp("invoice_date"));
            DateTime targetDate = new DateTime(result.getTimestamp("target_date"));
            Currency currency = Currency.valueOf(result.getString("currency"));
            boolean isMigrationInvoice = result.getBoolean("migrated");

            return new DefaultInvoice(id, accountId, invoiceNumber, invoiceDate, targetDate, currency, isMigrationInvoice);
        }
    }

    public static class BalanceMapper implements ResultSetMapper<BigDecimal> {
        @Override
        public BigDecimal map(final int index, final ResultSet result, final StatementContext context) throws SQLException {
            BigDecimal amountInvoiced = result.getBigDecimal("amount_invoiced");
            BigDecimal amountPaid = result.getBigDecimal("amount_paid");

            if (amountInvoiced == null) {
                amountInvoiced = BigDecimal.ZERO;
            }

            if (amountPaid == null) {
                amountPaid = BigDecimal.ZERO;
            }

            return amountInvoiced.subtract(amountPaid);
        }
    }



}

