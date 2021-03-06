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

package com.ning.billing.payment.api;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "error")
public class DefaultPaymentErrorEvent implements PaymentErrorEvent {
	
    private final String type;
    private final String message;
    private final UUID accountId;
    private final UUID invoiceId;
    private final UUID userToken;

    public DefaultPaymentErrorEvent(final DefaultPaymentErrorEvent src, final UUID accountId, final UUID invoiceId) {
        this.type = src.type;
        this.message = src.message;
        this.accountId = accountId;
        this.invoiceId = invoiceId;
        this.userToken = src.userToken;
    }

    @JsonCreator
    public DefaultPaymentErrorEvent(@JsonProperty("type") String type,
            @JsonProperty("message") String message,
            @JsonProperty("accountId") UUID accountId,
            @JsonProperty("invoiceId") UUID invoiceId,
            @JsonProperty("userToken") UUID userToken) {
        this.type = type;
        this.message = message;
        this.accountId = accountId;
        this.invoiceId = invoiceId;
        this.userToken = userToken;        
    }

    public DefaultPaymentErrorEvent(String type, String message) {
    	this(type, message, null, null, null);
    }

    @JsonIgnore
	@Override
	public BusEventType getBusEventType() {
		return BusEventType.PAYMENT_ERROR;
	}

    @Override
    public UUID getUserToken() {
    	return userToken;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public UUID getInvoiceId() {
        return invoiceId;
    }

    @Override
    public UUID getAccountId() {
        return accountId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result + ((invoiceId == null) ? 0 : invoiceId.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        DefaultPaymentErrorEvent other = (DefaultPaymentErrorEvent) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        }
        else if (!accountId.equals(other.accountId))
            return false;
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        }
        else if (!invoiceId.equals(other.invoiceId))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        }
        else if (!message.equals(other.message))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PaymentError [type=" + type + ", message=" + message + ", accountId=" + accountId + ", invoiceId=" + invoiceId + "]";
    }
}
