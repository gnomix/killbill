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

package com.ning.billing.entitlement.engine.dao;

import com.ning.billing.catalog.api.ProductCategory;
import com.ning.billing.entitlement.api.user.Subscription;
import com.ning.billing.entitlement.api.user.SubscriptionData;
import com.ning.billing.entitlement.api.user.DefaultSubscriptionFactory.SubscriptionBuilder;
import com.ning.billing.util.dao.AuditSqlDao;
import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.callcontext.CallContextBinder;
import com.ning.billing.util.dao.BinderBase;
import com.ning.billing.util.dao.MapperBase;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.CloseMe;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.mixins.Transmogrifier;
import org.skife.jdbi.v2.sqlobject.stringtemplate.ExternalizedSqlViaStringTemplate3;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ExternalizedSqlViaStringTemplate3()
public interface SubscriptionSqlDao extends Transactional<SubscriptionSqlDao>, AuditSqlDao, CloseMe, Transmogrifier {
	@SqlUpdate
    public void insertSubscription(@Bind(binder = SubscriptionBinder.class) SubscriptionData sub,
                                   @CallContextBinder final CallContext context);

    @SqlQuery
    @Mapper(SubscriptionMapper.class)
    public Subscription getSubscriptionFromId(@Bind("id") String id);

    @SqlQuery
    @Mapper(SubscriptionMapper.class)
    public List<Subscription> getSubscriptionsFromBundleId(@Bind("bundleId") String bundleId);

    @SqlUpdate
    public void updateChargedThroughDate(@Bind("id") String id, @Bind("chargedThroughDate") Date chargedThroughDate,
                                        @CallContextBinder final CallContext context);

    @SqlUpdate void updateActiveVersion(@Bind("id") String id, @Bind("activeVersion") long activeVersion,
            @CallContextBinder final CallContext context);
    
    @SqlUpdate
    public void updateForRepair(@Bind("id") String id, @Bind("activeVersion") long activeVersion,
            @Bind("startDate") Date startDate,
            @Bind("bundleStartDate") Date bundleStartDate,
            @CallContextBinder final CallContext context);

    public static class SubscriptionBinder extends BinderBase implements Binder<Bind, SubscriptionData> {
        @Override
        public void bind(@SuppressWarnings("rawtypes") SQLStatement stmt, Bind bind, SubscriptionData sub) {
            stmt.bind("id", sub.getId().toString());
            stmt.bind("bundleId", sub.getBundleId().toString());
            stmt.bind("category", sub.getCategory().toString());
            stmt.bind("startDate", getDate(sub.getStartDate()));
            stmt.bind("bundleStartDate", getDate(sub.getBundleStartDate()));
            stmt.bind("activeVersion", sub.getActiveVersion());
            stmt.bind("chargedThroughDate", getDate(sub.getChargedThroughDate()));
            stmt.bind("paidThroughDate", getDate(sub.getPaidThroughDate()));
        }
    }

    public static class SubscriptionMapper extends MapperBase implements ResultSetMapper<SubscriptionData> {
        @Override
        public SubscriptionData map(int arg0, ResultSet r, StatementContext ctx)
                throws SQLException {

            UUID id = UUID.fromString(r.getString("id"));
            UUID bundleId = UUID.fromString(r.getString("bundle_id"));
            ProductCategory category = ProductCategory.valueOf(r.getString("category"));
            DateTime bundleStartDate = getDate(r, "bundle_start_date");
            DateTime startDate = getDate(r, "start_date");
            DateTime ctd = getDate(r, "charged_through_date");
            DateTime ptd = getDate(r, "paid_through_date");
            long activeVersion = r.getLong("active_version");

            return new SubscriptionData(new SubscriptionBuilder()
            .setId(id)
            .setBundleId(bundleId)
            .setCategory(category)
            .setBundleStartDate(bundleStartDate)
            .setStartDate(startDate)
            .setActiveVersion(activeVersion)
            .setChargedThroughDate(ctd)
            .setPaidThroughDate(ptd));
        }
    }
}
