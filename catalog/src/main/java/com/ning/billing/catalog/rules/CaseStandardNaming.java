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

package com.ning.billing.catalog.rules;

import com.ning.billing.catalog.DefaultPriceList;
import com.ning.billing.catalog.DefaultProduct;
import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.catalog.api.ProductCategory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

public abstract class CaseStandardNaming<T> extends Case<T> {
    @XmlElement(required=false, name="product")
    @XmlIDREF
    private DefaultProduct product;
    @XmlElement(required=false, name="productCategory")
    private ProductCategory productCategory;
    
    @XmlElement(required=false, name="billingPeriod")
    private BillingPeriod billingPeriod;
    
    @XmlElement(required=false, name="priceList")
    @XmlIDREF
    private DefaultPriceList priceList;

	public DefaultProduct getProduct(){
		return product;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public BillingPeriod getBillingPeriod() {
		return billingPeriod;
	}
	
	public DefaultPriceList getPriceList() {
		return priceList;
	}

    protected CaseStandardNaming<T> setProduct(DefaultProduct product) {
        this.product = product;
        return this;
    }

    protected CaseStandardNaming<T> setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
        return this;
    }

    protected CaseStandardNaming<T> setBillingPeriod(BillingPeriod billingPeriod) {
        this.billingPeriod = billingPeriod;
        return this;
    }

    protected CaseStandardNaming<T> setPriceList(DefaultPriceList priceList) {
        this.priceList = priceList;
        return this;
    }

}
