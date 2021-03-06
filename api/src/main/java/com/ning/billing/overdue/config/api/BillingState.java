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

package com.ning.billing.overdue.config.api;

import java.math.BigDecimal;
import java.util.UUID;

import org.joda.time.DateTime;

import com.ning.billing.junction.api.Blockable;
import com.ning.billing.util.tag.Tag;

public class BillingState<T extends Blockable> {
	private final UUID objectId;
	private final int numberOfUnpaidInvoices;
	private final BigDecimal balanceOfUnpaidInvoices;
	private final DateTime dateOfEarliestUnpaidInvoice;
	private final PaymentResponse responseForLastFailedPayment;
	private final Tag[] tags;
	
	public BillingState(UUID id, int numberOfUnpaidInvoices, BigDecimal balanceOfUnpaidInvoices,
			DateTime dateOfEarliestUnpaidInvoice,
			PaymentResponse responseForLastFailedPayment,
			Tag[] tags) {
		super();
		this.objectId = id;
		this.numberOfUnpaidInvoices = numberOfUnpaidInvoices;
		this.balanceOfUnpaidInvoices = balanceOfUnpaidInvoices;
		this.dateOfEarliestUnpaidInvoice = dateOfEarliestUnpaidInvoice;
		this.responseForLastFailedPayment = responseForLastFailedPayment;
		this.tags = tags;
	}

	public UUID getObjectId() {
		return objectId;
	}
	
	public int getNumberOfUnpaidInvoices() {
		return numberOfUnpaidInvoices;
	}

	public BigDecimal getBalanceOfUnpaidInvoices() {
		return balanceOfUnpaidInvoices;
	}

	public DateTime getDateOfEarliestUnpaidInvoice() {
		return dateOfEarliestUnpaidInvoice;
	}
	
	public PaymentResponse getResponseForLastFailedPayment() {
		return responseForLastFailedPayment;
	}

	public Tag[] getTags() {
		return tags;
	}

}
