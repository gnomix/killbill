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
package com.ning.billing.catalog;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import com.google.common.io.Resources;
import com.ning.billing.ErrorCode;
import com.ning.billing.catalog.api.CatalogApiException;
import com.ning.billing.catalog.api.Currency;
import com.ning.billing.catalog.api.InvalidConfigException;
import com.ning.billing.catalog.api.Plan;
import com.ning.billing.catalog.io.VersionedCatalogLoader;
import com.ning.billing.lifecycle.KillbillService.ServiceException;
import com.ning.billing.util.clock.DefaultClock;

public class TestVersionedCatalog {
	private static final Logger log = LoggerFactory.getLogger(TestVersionedCatalog.class);
	private final VersionedCatalogLoader loader = new VersionedCatalogLoader(new DefaultClock());
	private VersionedCatalog vc;
	
	@BeforeClass(groups={"fast"})
	public void setUp() throws ServiceException {
		vc = loader.load(Resources.getResource("versionedCatalog").toString());
	}

	@Test(groups={"fast"},enabled=true)
	public void testAddCatalog() throws MalformedURLException, IOException, SAXException, InvalidConfigException, JAXBException, TransformerException, URISyntaxException, ServiceException, CatalogApiException {
		vc.add(new StandaloneCatalog(new Date()));
		assertEquals(4, vc.size());
	}
	
		
	@Test(groups={"fast"},enabled=true)
	public void testFindPlanWithDates() throws Exception {
		DateTime dt0= new DateTime("2010-01-01T00:00:00+00:00");
		DateTime dt1 = new DateTime("2011-01-01T00:01:00+00:00");
		DateTime dt2 = new DateTime("2011-02-02T00:01:00+00:00");
		DateTime dt214 = new DateTime("2011-02-14T00:01:00+00:00");
		DateTime dt3 = new DateTime("2011-03-03T00:01:00+00:00");
		
		// New subscription
		try {
			vc.findPlan("pistol-monthly", dt0, dt0);	
			Assert.fail("Exception should have been thrown there are no plans for this date");
		} catch (CatalogApiException e) {
			// Expected behaviour
			log.error("Expected exception", e);
			
		}
		Plan newSubPlan1 = vc.findPlan("pistol-monthly", dt1, dt1);
		Plan newSubPlan2 = vc.findPlan("pistol-monthly", dt2, dt2);
		Plan newSubPlan214 = vc.findPlan("pistol-monthly", dt214, dt214);
		Plan newSubPlan3 = vc.findPlan("pistol-monthly", dt3, dt3);
		
		Assert.assertEquals(newSubPlan1.getAllPhases()[1].getRecurringPrice().getPrice(Currency.USD), new BigDecimal("1.0"));
		Assert.assertEquals(newSubPlan2.getAllPhases()[1].getRecurringPrice().getPrice(Currency.USD), new BigDecimal("2.0"));
		Assert.assertEquals(newSubPlan214.getAllPhases()[1].getRecurringPrice().getPrice(Currency.USD), new BigDecimal("2.0"));
		Assert.assertEquals(newSubPlan3.getAllPhases()[1].getRecurringPrice().getPrice(Currency.USD), new BigDecimal("3.0"));
		
		// Existing subscription
		
		Plan exSubPlan2 = vc.findPlan("pistol-monthly", dt2, dt1);
		Plan exSubPlan214 = vc.findPlan("pistol-monthly", dt214, dt1);
		Plan exSubPlan3 = vc.findPlan("pistol-monthly", dt3, dt1);
		
		Assert.assertEquals(exSubPlan2.getAllPhases()[1].getRecurringPrice().getPrice(Currency.USD), new BigDecimal("1.0"));
		Assert.assertEquals(exSubPlan214.getAllPhases()[1].getRecurringPrice().getPrice(Currency.USD), new BigDecimal("2.0"));
		Assert.assertEquals(exSubPlan3.getAllPhases()[1].getRecurringPrice().getPrice(Currency.USD), new BigDecimal("2.0"));

	}
	
	@Test(groups={"fast"},enabled=true)
	public void testErrorOnDateTooEarly() {
		DateTime dt0= new DateTime("1977-01-01T00:00:00+00:00");
		try {
			vc.findPlan("foo", dt0);
			Assert.fail("Date is too early an exception should have been thrown");
		} catch (CatalogApiException e) {
			e.printStackTrace();
			Assert.assertEquals(e.getCode(), ErrorCode.CAT_NO_CATALOG_FOR_GIVEN_DATE.getCode());

		}
	}
}
