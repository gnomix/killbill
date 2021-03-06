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

package com.ning.billing.account.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class DefaultChangedField implements ChangedField {
    
    private final String fieldName;
    private final String oldValue;
    private final String newValue;
    private final DateTime changeDate;

    @JsonCreator
    public DefaultChangedField(@JsonProperty("fieldName") String fieldName,
            @JsonProperty("oldValue") String oldValue,
            @JsonProperty("newValue") String newValue,
            @JsonProperty("changeDate") DateTime changeDate) {
        this.changeDate = changeDate;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public DefaultChangedField(String fieldName,
            String oldValue,
            String newValue) {
        this(fieldName, oldValue, newValue, new DateTime());
    }

    
    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getOldValue() {
        return oldValue;
    }

    @Override
    public String getNewValue() {
        return newValue;
    }

    @Override
    public DateTime getChangeDate() {
        return changeDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((changeDate == null) ? 0 : changeDate.hashCode());
        result = prime * result
                + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result
                + ((newValue == null) ? 0 : newValue.hashCode());
        result = prime * result
                + ((oldValue == null) ? 0 : oldValue.hashCode());
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
        DefaultChangedField other = (DefaultChangedField) obj;
        if (changeDate == null) {
            if (other.changeDate != null)
                return false;
        } else if (changeDate.compareTo(other.changeDate) != 0)
            return false;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        if (newValue == null) {
            if (other.newValue != null)
                return false;
        } else if (!newValue.equals(other.newValue))
            return false;
        if (oldValue == null) {
            if (other.oldValue != null)
                return false;
        } else if (!oldValue.equals(other.oldValue))
            return false;
        return true;
    }
    
}
