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

package com.ning.billing.overdue.api;

import com.google.inject.Inject;
import com.ning.billing.ErrorCode;
import com.ning.billing.catalog.api.CatalogService;
import com.ning.billing.entitlement.api.user.SubscriptionBundle;
import com.ning.billing.junction.api.Blockable;
import com.ning.billing.junction.api.BlockingApi;
import com.ning.billing.overdue.OverdueApiException;
import com.ning.billing.overdue.OverdueState;
import com.ning.billing.overdue.OverdueUserApi;
import com.ning.billing.overdue.config.OverdueConfig;
import com.ning.billing.overdue.config.api.BillingState;
import com.ning.billing.overdue.config.api.OverdueError;
import com.ning.billing.overdue.config.api.OverdueStateSet;
import com.ning.billing.overdue.service.ExtendedOverdueService;
import com.ning.billing.overdue.wrapper.OverdueWrapper;
import com.ning.billing.overdue.wrapper.OverdueWrapperFactory;

public class DefaultOverdueUserApi implements OverdueUserApi { 

    
    private final OverdueWrapperFactory factory;
    private final BlockingApi accessApi; 
    private final OverdueConfig overdueConfig;
   
    @Inject
    public DefaultOverdueUserApi(OverdueWrapperFactory factory,BlockingApi accessApi, ExtendedOverdueService service,  CatalogService catalogService) {
        this.factory = factory;
        this.accessApi = accessApi;
        this.overdueConfig = service.getOverdueConfig();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Blockable> OverdueState<T> getOverdueStateFor(T overdueable) throws OverdueError {
        try {
            String stateName = accessApi.getBlockingStateFor(overdueable).getStateName();
            OverdueStateSet<SubscriptionBundle> states = overdueConfig.getBundleStateSet();
            return (OverdueState<T>) states.findState(stateName);
        } catch (OverdueApiException e) {
            throw new OverdueError(e, ErrorCode.OVERDUE_CAT_ERROR_ENCOUNTERED,overdueable.getId(), overdueable.getClass().getSimpleName());
        }
    }
    
    @Override
    public <T extends Blockable> OverdueState<T> refreshOverdueStateFor(T overdueable) throws OverdueError, OverdueApiException {
        OverdueWrapper<T> wrapper = factory.createOverdueWrapperFor(overdueable);
        return wrapper.refresh();
    } 
 

    @Override
    public <T extends Blockable> void setOverrideBillingStateForAccount(
            T overdueable, BillingState<T> state) {
        throw new UnsupportedOperationException();
    }
    
}
