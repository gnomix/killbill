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

package com.ning.billing.jaxrs.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import com.ning.billing.entitlement.api.user.SubscriptionBundle;

public class BundleJsonNoSubscriptions extends BundleJsonSimple {
    @JsonView(BundleTimelineViews.Base.class)
    private final String accountId;


    @JsonCreator
    public BundleJsonNoSubscriptions(@JsonProperty("bundleId") String bundleId,
                                     @JsonProperty("accountId") String accountId,
                                     @JsonProperty("externalKey") String externalKey,
                                     @JsonProperty("subscriptions") List<SubscriptionJsonWithEvents> subscriptions) {
        super(bundleId, externalKey);
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    
    public BundleJsonNoSubscriptions(SubscriptionBundle bundle) {
        super(bundle.getId().toString(), bundle.getKey());
        this.accountId = bundle.getAccountId().toString();
    }
    
    public BundleJsonNoSubscriptions() {
        super(null, null);        
        this.accountId = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result
                + ((bundleId == null) ? 0 : bundleId.hashCode());
        result = prime * result
                + ((externalKey == null) ? 0 : externalKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (equalsNoId(obj) == false) {
            return false;
        }
        BundleJsonNoSubscriptions other = (BundleJsonNoSubscriptions) obj;
        if (bundleId == null) {
            if (other.bundleId != null)
                return false;
        } else if (!bundleId.equals(other.bundleId))
            return false;
        return true;
    }

    public boolean equalsNoId(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BundleJsonNoSubscriptions other = (BundleJsonNoSubscriptions) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        if (externalKey == null) {
            if (other.externalKey != null)
                return false;
        } else if (!externalKey.equals(other.externalKey))
            return false;
        return true;
    }
    
    
}
