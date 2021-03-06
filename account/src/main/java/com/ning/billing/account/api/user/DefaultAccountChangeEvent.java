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

package com.ning.billing.account.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ning.billing.account.api.Account;
import com.ning.billing.account.api.AccountChangeEvent;
import com.ning.billing.account.api.ChangedField;
import com.ning.billing.account.api.DefaultChangedField;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultAccountChangeEvent implements AccountChangeEvent {
	
	private final UUID userToken;
    private final List<ChangedField> changedFields;
    private final UUID accountId;

    
    @JsonCreator
    public DefaultAccountChangeEvent(@JsonProperty("userToken") UUID userToken,
            @JsonProperty("changeFields") List<ChangedField> changedFields,
            @JsonProperty("accountId") UUID accountId) {
        super();
        this.userToken = userToken;
        this.accountId = accountId;
        this.changedFields = changedFields;
        //new ArrayList<ChangedField>();
        //this.changedFields.addAll(changedFields);
    }

    public DefaultAccountChangeEvent(UUID id, UUID userToken, Account oldData, Account newData) {
        this.accountId = id;
        this.userToken = userToken;
        this.changedFields = calculateChangedFields(oldData, newData);
    }

    @JsonIgnore
	@Override
	public BusEventType getBusEventType() {
		return BusEventType.ACCOUNT_CHANGE;
	}

    @Override
    public UUID getUserToken() {
        return userToken;
	}

    @Override
    public UUID getAccountId() {
        return accountId;
    }

    @JsonDeserialize(contentAs = DefaultChangedField.class)
    @Override
    public List<ChangedField> getChangedFields() {
        return changedFields;
    }

    @JsonIgnore
    @Override
    public boolean hasChanges() {
        return (changedFields.size() > 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result
                + ((changedFields == null) ? 0 : changedFields.hashCode());
        result = prime * result
                + ((userToken == null) ? 0 : userToken.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultAccountChangeEvent other = (DefaultAccountChangeEvent) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        if (changedFields == null) {
            if (other.changedFields != null)
                return false;
        } else if (!changedFields.equals(other.changedFields))
            return false;
        if (userToken == null) {
            if (other.userToken != null)
                return false;
        } else if (!userToken.equals(other.userToken))
            return false;
        return true;
    }

    private List<ChangedField> calculateChangedFields(Account oldData, Account newData) {

        List<ChangedField> tmpChangedFields = new ArrayList<ChangedField>();

        addIfValueChanged(tmpChangedFields, "externalKey",
                oldData.getExternalKey(), newData.getExternalKey());

        addIfValueChanged(tmpChangedFields, "email",
                oldData.getEmail(), newData.getEmail());

        addIfValueChanged(tmpChangedFields, "firstName",
                oldData.getName(), newData.getName());

        addIfValueChanged(tmpChangedFields, "currency",
                (oldData.getCurrency() != null) ? oldData.getCurrency().toString() : null,
                 (newData.getCurrency() != null) ? newData.getCurrency().toString() : null);

        addIfValueChanged(tmpChangedFields,
                "billCycleDay",
                Integer.toString(oldData.getBillCycleDay()), Integer.toString(newData.getBillCycleDay()));

        addIfValueChanged(tmpChangedFields,"paymentProviderName",
                oldData.getPaymentProviderName(), newData.getPaymentProviderName());

        addIfValueChanged(tmpChangedFields, "locale", oldData.getLocale(), newData.getLocale());

        addIfValueChanged(tmpChangedFields, "timeZone",
                (oldData.getTimeZone() == null) ? null : oldData.getTimeZone().toString(),
                (newData.getTimeZone() == null) ? null : newData.getTimeZone().toString());

        addIfValueChanged(tmpChangedFields, "address1", oldData.getAddress1(), newData.getAddress1());
        addIfValueChanged(tmpChangedFields, "address2", oldData.getAddress2(), newData.getAddress2());
        addIfValueChanged(tmpChangedFields, "city", oldData.getCity(), newData.getCity());
        addIfValueChanged(tmpChangedFields, "stateOrProvince", oldData.getStateOrProvince(), newData.getStateOrProvince());
        addIfValueChanged(tmpChangedFields, "country", oldData.getCountry(), newData.getCountry());
        addIfValueChanged(tmpChangedFields, "postalCode", oldData.getPostalCode(), newData.getPostalCode());
        addIfValueChanged(tmpChangedFields, "phone", oldData.getPhone(), newData.getPhone());

        return tmpChangedFields;
    }

    private void addIfValueChanged(List<ChangedField> inputList, String key, String oldData, String newData) {
        // If both null => no changes
        if (newData == null && oldData == null) {
            return;
        // If only one is null
        } else if (newData == null || oldData == null) {
            inputList.add(new DefaultChangedField(key, oldData, newData));
        // If neither are null we can safely compare values
        } else if (!newData.equals(oldData)) {
            inputList.add(new DefaultChangedField(key, oldData, newData));
        }
    }
}
