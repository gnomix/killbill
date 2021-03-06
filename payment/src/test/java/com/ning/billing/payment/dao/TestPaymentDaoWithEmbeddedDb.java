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

package com.ning.billing.payment.dao;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ning.billing.dbi.MysqlTestingHelper;

@Test(enabled = true, groups = { "slow", "database" })
public class TestPaymentDaoWithEmbeddedDb extends TestPaymentDao {
    private final MysqlTestingHelper helper = new MysqlTestingHelper();

    @BeforeClass(groups = { "slow", "database" })
    public void startMysql() throws IOException {
        final String paymentddl = IOUtils.toString(MysqlTestingHelper.class.getResourceAsStream("/com/ning/billing/payment/ddl.sql"));
        final String utilddl = IOUtils.toString(MysqlTestingHelper.class.getResourceAsStream("/com/ning/billing/util/ddl.sql"));

        helper.startMysql();
        helper.initDb(paymentddl);
        helper.initDb(utilddl);
    }

    @AfterClass(groups = { "slow", "database" })
    public void stopMysql() {
        helper.stopMysql();
    }

    @BeforeMethod(groups = { "slow", "database" })
    public void setUp() throws IOException {
        paymentDao = new AuditedPaymentDao(helper.getDBI());
    }
}
