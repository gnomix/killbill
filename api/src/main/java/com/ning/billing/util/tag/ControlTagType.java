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

package com.ning.billing.util.tag;

import java.util.UUID;

public enum ControlTagType {
    AUTO_PAY_OFF("Suspends payments until removed.", true, false),
    AUTO_INVOICING_OFF("Suspends invoicing until removed.", false, true), 
    OVERDUE_ENFORCEMENT_OFF("Suspends overdue enforcement behaviour until removed.", false, false),
    WRITTEN_OFF("Indicated that an invoice is written off. No billing or payment effect.", false, false);

    private final String description;
    private final boolean autoPaymentOff;
    private final boolean autoInvoicingOff;

    ControlTagType(final String description, final boolean autoPaymentOff, final boolean autoInvoicingOff) {
        this.description = description;
        this.autoPaymentOff = autoPaymentOff;
        this.autoInvoicingOff = autoInvoicingOff;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getAutoPaymentOff() {
        return this.autoPaymentOff;
    }

    public boolean getAutoInvoicingOff() {
        return this.autoInvoicingOff;
    }

    public TagDefinition toTagDefinition() {
        return new TagDefinition() {
            @Override public String getName() {return this.toString();}

            @Override public String getDescription() {return description;}

            @Override public Boolean isControlTag() {return true;}

            @Override public UUID getId() {return null;}
        };
    }
}
