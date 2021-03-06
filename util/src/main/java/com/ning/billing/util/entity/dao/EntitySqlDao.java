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

package com.ning.billing.util.entity.dao;

import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.callcontext.CallContextBinder;
import com.ning.billing.util.entity.Entity;
import com.ning.billing.util.entity.EntityPersistenceException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface EntitySqlDao<T extends Entity> {
    @SqlUpdate
    public void create(@BindBean final T entity, @CallContextBinder final CallContext context) throws EntityPersistenceException;

    @SqlQuery
    public T getById(@Bind("id") final String id);

    @SqlQuery
    public Long getRecordId(@Bind("id") final String id);

    @SqlQuery
    public Long getHistoryRecordId(@Bind("recordId") final Long recordId);

    @SqlQuery
    public List<T> get();

    @SqlUpdate
    public void test();
}
