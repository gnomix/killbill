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

package com.ning.billing.analytics.dao;

import com.ning.billing.analytics.BusinessAccount;

public class MockBusinessAccountDao implements BusinessAccountDao {

    @Override
    public BusinessAccount getAccount(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int createAccount(BusinessAccount account) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int saveAccount(BusinessAccount account) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void test() {
    }
}
