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

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Objects;

public class PaymentProviderAccount {
    private final String id;
    private final String accountNumber;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String currency;
    private final String phoneNumber;
    private final String defaultPaymentMethodId;

    public PaymentProviderAccount(String id,
                                  String accountNumber,
                                  String firstName,
                                  String lastName,
                                  String email,
                                  String currency,
                                  String phoneNumber,
                                  String defaultPaymentMethodId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = StringUtils.substring(firstName, 0, 100);
        this.lastName  = StringUtils.substring(lastName, 0, 100);
        this.email     = StringUtils.substring(lastName, 0, 80);
        this.currency = currency;
        this.phoneNumber = phoneNumber;
        this.defaultPaymentMethodId = defaultPaymentMethodId;
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCurrency() {
        return currency;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDefaultPaymentMethodId() {
        return defaultPaymentMethodId;
    }

    public static class Builder {
        private String id;
        private String accountNumber;
        private String firstName;
        private String lastName;
        private String email;
        private String currency;
        private String phoneNumber;
        private String defaultPaymentMethodId;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setDefaultPaymentMethod(String defaultPaymentMethod) {
            this.defaultPaymentMethodId = defaultPaymentMethod;
            return this;
        }

        public PaymentProviderAccount build() {
            return new PaymentProviderAccount(id, accountNumber, firstName, lastName, email, currency, phoneNumber, defaultPaymentMethodId);
        }

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id,
                                accountNumber,
                                firstName,
                                lastName,
                                phoneNumber,
                                defaultPaymentMethodId);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            PaymentProviderAccount other = (PaymentProviderAccount)obj;
            if (obj == other) {
                return true;
            }
            else {
                return Objects.equal(id, other.id) &&
                       Objects.equal(accountNumber, other.accountNumber) &&
                       Objects.equal(phoneNumber, other.phoneNumber) &&
                       Objects.equal(defaultPaymentMethodId, other.defaultPaymentMethodId);
            }
        }
        return false;
    }

}