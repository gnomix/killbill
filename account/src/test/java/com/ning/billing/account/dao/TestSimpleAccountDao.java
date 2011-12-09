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

package com.ning.billing.account.dao;

import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;
import org.testng.annotations.Test;
import com.ning.billing.account.api.Account;
import com.ning.billing.account.api.DefaultAccount;
import com.ning.billing.account.api.DefaultTagDescription;
import com.ning.billing.account.api.Tag;
import com.ning.billing.account.api.TagDescription;
import com.ning.billing.account.api.user.AccountBuilder;
import com.ning.billing.catalog.api.Currency;
import com.ning.billing.util.clock.DefaultClock;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = {"account-dao"})
public class TestSimpleAccountDao extends AccountDaoTestBase {
    private final String key = "test1234";
    private final String firstName = "Wesley";
    private final String email = "me@me.com";

    private DefaultAccount createTestAccount() {
        String thisKey = key + UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        String thisEmail = email + " " + UUID.randomUUID();
        String name = firstName + " " + lastName;

        int firstNameLength = firstName.length();
        return new AccountBuilder().externalKey(thisKey).name(name).firstNameLength(firstNameLength)
                                   .email(thisEmail).currency(Currency.USD).build();
    }

    public void testBasic() {

        Account a = createTestAccount();
        accountDao.save(a);
        String key = a.getExternalKey();

        Account r = accountDao.getAccountByKey(key);
        assertNotNull(r);
        assertEquals(r.getExternalKey(), a.getExternalKey());

        r = accountDao.getById(r.getId().toString());
        assertNotNull(r);
        assertEquals(r.getExternalKey(), a.getExternalKey());

        List<Account> all = accountDao.get();
        assertNotNull(all);
        assertTrue(all.size() >= 1);
    }

    @Test
    public void testGetById() {
        Account account = createTestAccount();
        UUID id = account.getId();
        String key = account.getExternalKey();
        String name = account.getName();
        int firstNameLength = account.getFirstNameLength();

        accountDao.save(account);

        account = accountDao.getById(id.toString());
        assertNotNull(account);
        assertEquals(account.getId(), id);
        assertEquals(account.getExternalKey(), key);
        assertEquals(account.getName(), name);
        assertEquals(account.getFirstNameLength(), firstNameLength);

    }

    @Test
    public void testCustomFields() {
        Account account = createTestAccount();
        String fieldName = "testField1";
        String fieldValue = "testField1_value";
        account.setFieldValue(fieldName, fieldValue);

        accountDao.save(account);

        Account thisAccount = accountDao.getAccountByKey(account.getExternalKey());
        assertNotNull(thisAccount);
        assertEquals(thisAccount.getExternalKey(), account.getExternalKey());
        assertEquals(thisAccount.getFieldValue(fieldName), fieldValue);
    }

    @Test
    public void testTags() {
        Account account = createTestAccount();
        TagDescription description = new DefaultTagDescription("Test Tag", "For testing only", true, true, "Test System", new DateTime());
        TagDescriptionDao tagDescriptionDao = dbi.onDemand(TagDescriptionDao.class);
        tagDescriptionDao.save(description);

        String addedBy = "testTags()";
        DateTime dateAdded = new DefaultClock().getUTCNow();
        account.addTag(description, addedBy, dateAdded);
        assertEquals(account.getTagList().size(), 1);
        accountDao.save(account);

        Account thisAccount = accountDao.getById(account.getId().toString());
        List<Tag> tagList = thisAccount.getTagList();
        assertEquals(tagList.size(), 1);
        Tag tag = tagList.get(0);
        assertEquals(tag.getName(), description.getName());
        assertEquals(tag.getGenerateInvoice(), description.getGenerateInvoice());
        assertEquals(tag.getProcessPayment(), description.getProcessPayment());
        assertEquals(tag.getTagDescriptionId(), description.getId());
        assertEquals(tag.getAddedBy(), addedBy);
        assertEquals(tag.getDateAdded().compareTo(dateAdded), 0);
    }
}
