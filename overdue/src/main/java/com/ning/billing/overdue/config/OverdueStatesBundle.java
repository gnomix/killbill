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

package com.ning.billing.overdue.config;

import javax.xml.bind.annotation.XmlElement;

import org.joda.time.DateTime;

import com.ning.billing.ErrorCode;
import com.ning.billing.catalog.api.Duration;
import com.ning.billing.catalog.api.TimeUnit;
import com.ning.billing.entitlement.api.user.SubscriptionBundle;
import com.ning.billing.overdue.OverdueApiException;
import com.ning.billing.overdue.OverdueState;

public class OverdueStatesBundle extends DefaultOverdueStateSet<SubscriptionBundle>{

    @XmlElement(required=true, name="state")
    private DefaultOverdueState<SubscriptionBundle>[] bundleOverdueStates;

    @Override
    protected DefaultOverdueState<SubscriptionBundle>[] getStates() {
        return bundleOverdueStates;
    }

    protected OverdueStatesBundle setBundleOverdueStates(DefaultOverdueState<SubscriptionBundle>[] bundleOverdueStates) {
        this.bundleOverdueStates = bundleOverdueStates;
        return this;
    }
}
