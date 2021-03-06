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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.ning.billing.util.dao.MapperBase;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.ExternalizedSqlViaStringTemplate3;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.ning.billing.catalog.api.Currency;
import com.ning.billing.invoice.api.InvoiceItem;
import com.ning.billing.invoice.model.RecurringInvoiceItem;
import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.callcontext.CallContextBinder;
import com.ning.billing.util.entity.dao.EntitySqlDao;

@ExternalizedSqlViaStringTemplate3()
@RegisterMapper(RecurringInvoiceItemSqlDao.RecurringInvoiceItemMapper.class)
public interface RecurringInvoiceItemSqlDao extends EntitySqlDao<InvoiceItem> {
    @SqlQuery
    List<Long> getRecordIds(@Bind("invoiceId") final String invoiceId);

    @SqlQuery
    List<InvoiceItem> getInvoiceItemsByInvoice(@Bind("invoiceId") final String invoiceId);

    @SqlQuery
    List<InvoiceItem> getInvoiceItemsByAccount(@Bind("accountId") final String accountId);

    @SqlQuery
    List<InvoiceItem> getInvoiceItemsBySubscription(@Bind("subscriptionId") final String subscriptionId);

    @Override
    @SqlUpdate
    void create(@RecurringInvoiceItemBinder final InvoiceItem invoiceItem, @CallContextBinder final CallContext context);

    @SqlBatch(transactional = false)
    void batchCreateFromTransaction(@RecurringInvoiceItemBinder final List<InvoiceItem> items, @CallContextBinder final CallContext context);

    @BindingAnnotation(RecurringInvoiceItemBinder.InvoiceItemBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface RecurringInvoiceItemBinder {
        public static class InvoiceItemBinderFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                return new Binder<RecurringInvoiceItemBinder, RecurringInvoiceItem>() {
                    @Override
                    public void bind(SQLStatement q, RecurringInvoiceItemBinder bind, RecurringInvoiceItem item) {
                        q.bind("id", item.getId().toString());
                        q.bind("invoiceId", item.getInvoiceId().toString());
                        q.bind("accountId", item.getAccountId().toString());
                        q.bind("bundleId", item.getBundleId() == null ? null : item.getBundleId().toString());
                        q.bind("subscriptionId", item.getSubscriptionId() == null ? null : item.getSubscriptionId().toString());
                        q.bind("planName", item.getPlanName());
                        q.bind("phaseName", item.getPhaseName());
                        q.bind("startDate", item.getStartDate().toDate());
                        q.bind("endDate", item.getEndDate().toDate());
                        q.bind("amount", item.getAmount());
                        q.bind("rate", item.getRate());
                        q.bind("currency", item.getCurrency().toString());
                        q.bind("reversedItemId", (item.getReversedItemId() == null) ? null : item.getReversedItemId().toString());
                    }
                };
            }
        }
    }

    public static class RecurringInvoiceItemMapper extends MapperBase implements ResultSetMapper<InvoiceItem> {
        @Override
        public InvoiceItem map(int index, ResultSet result, StatementContext context) throws SQLException {
            UUID id = getUUID(result, "id");
            UUID invoiceId = getUUID(result, "invoice_id");
            UUID accountId = getUUID(result, "account_id");
            UUID subscriptionId = getUUID(result, "subscription_id");
            UUID bundleId = getUUID(result, "bundle_id");
            String planName = result.getString("plan_name");
            String phaseName = result.getString("phase_name");
            DateTime startDate = getDate(result, "start_date");
            DateTime endDate = getDate(result, "end_date");
            BigDecimal amount = result.getBigDecimal("amount");
            BigDecimal rate = result.getBigDecimal("rate");
            Currency currency = Currency.valueOf(result.getString("currency"));
            UUID reversedItemId = getUUID(result, "reversed_item_id");

            return new RecurringInvoiceItem(id, invoiceId, accountId, bundleId, subscriptionId, planName, phaseName, startDate, endDate,
                    amount, rate, currency, reversedItemId);

        }
    }
}
