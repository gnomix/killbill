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

package com.ning.billing.analytics;

import com.ning.billing.analytics.dao.BusinessSubscriptionTransitionBinder;
import com.ning.billing.analytics.dao.BusinessSubscriptionTransitionDao;
import org.skife.jdbi.v2.sqlobject.Bind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockBusinessSubscriptionTransitionDao implements BusinessSubscriptionTransitionDao
{
    private final Map<String, List<BusinessSubscriptionTransition>> content = new HashMap<String, List<BusinessSubscriptionTransition>>();

    @Override
    public List<BusinessSubscriptionTransition> getTransitions(@Bind("event_key") final String key)
    {
        return content.get(key);
    }

    @Override
    public int createTransition(@BusinessSubscriptionTransitionBinder final BusinessSubscriptionTransition transition)
    {
        if (content.get(transition.getKey()) == null) {
            content.put(transition.getKey(), new ArrayList<BusinessSubscriptionTransition>());
        }
        content.get(transition.getKey()).add(transition);
        return 1;
    }

    @Override
    public void test()
    {
    }
}
