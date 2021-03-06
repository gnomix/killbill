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

package com.ning.billing.payment.dao;

import java.util.List;
import java.util.UUID;

import com.ning.billing.payment.api.DefaultPaymentAttempt;
import com.ning.billing.util.ChangeType;
import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.dao.EntityAudit;
import com.ning.billing.util.dao.EntityHistory;
import com.ning.billing.util.dao.TableName;
import org.skife.jdbi.v2.IDBI;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.ning.billing.invoice.api.Invoice;
import com.ning.billing.payment.api.PaymentAttempt;
import com.ning.billing.payment.api.PaymentInfoEvent;
import org.skife.jdbi.v2.Transaction;
import org.skife.jdbi.v2.TransactionStatus;

public class AuditedPaymentDao implements PaymentDao {
    private final PaymentSqlDao paymentSqlDao;
    private final PaymentAttemptSqlDao paymentAttemptSqlDao;

    @Inject
    public AuditedPaymentDao(IDBI dbi) {
        this.paymentSqlDao = dbi.onDemand(PaymentSqlDao.class);
        this.paymentAttemptSqlDao = dbi.onDemand(PaymentAttemptSqlDao.class);
    }

    @Override
    public PaymentAttempt getPaymentAttemptForPaymentId(UUID paymentId) {
        return paymentAttemptSqlDao.getPaymentAttemptForPaymentId(paymentId.toString());
    }

    @Override
    public List<PaymentAttempt> getPaymentAttemptsForInvoiceId(String invoiceId) {
        return paymentAttemptSqlDao.getPaymentAttemptsForInvoiceId(invoiceId);
    }

    @Override
    public PaymentAttempt createPaymentAttempt(final PaymentAttempt paymentAttempt, final CallContext context) {
        return paymentAttemptSqlDao.inTransaction(new Transaction<PaymentAttempt, PaymentAttemptSqlDao>() {
            @Override
            public PaymentAttempt inTransaction(PaymentAttemptSqlDao transactional, TransactionStatus status) throws Exception {
                transactional.insertPaymentAttempt(paymentAttempt, context);
                PaymentAttempt savedPaymentAttempt = transactional.getPaymentAttemptById(paymentAttempt.getId().toString());

                Long recordId = transactional.getRecordId(paymentAttempt.getId().toString());
                EntityHistory<PaymentAttempt> history = new EntityHistory<PaymentAttempt>(paymentAttempt.getId(), recordId, paymentAttempt, ChangeType.INSERT);
                transactional.insertHistoryFromTransaction(history, context);

                Long historyRecordId = transactional.getHistoryRecordId(recordId);
                EntityAudit audit = new EntityAudit(TableName.PAYMENT_ATTEMPTS, historyRecordId, ChangeType.INSERT);
                transactional.insertAuditFromTransaction(audit, context);
                return savedPaymentAttempt;
            }
        });
    }

    @Override
    public PaymentAttempt createPaymentAttempt(final Invoice invoice, final CallContext context) {
        return paymentAttemptSqlDao.inTransaction(new Transaction<PaymentAttempt, PaymentAttemptSqlDao>() {
            @Override
            public PaymentAttempt inTransaction(PaymentAttemptSqlDao transactional, TransactionStatus status) throws Exception {
                final PaymentAttempt paymentAttempt = new DefaultPaymentAttempt(UUID.randomUUID(), invoice);
                transactional.insertPaymentAttempt(paymentAttempt, context);

                Long recordId = transactional.getRecordId(paymentAttempt.getId().toString());
                EntityHistory<PaymentAttempt> history = new EntityHistory<PaymentAttempt>(paymentAttempt.getId(), recordId, paymentAttempt, ChangeType.INSERT);
                transactional.insertHistoryFromTransaction(history, context);

                Long historyRecordId = transactional.getHistoryRecordId(recordId);
                EntityAudit audit = new EntityAudit(TableName.PAYMENT_ATTEMPTS, historyRecordId, ChangeType.INSERT);
                transactional.insertAuditFromTransaction(audit, context);

                return paymentAttempt;
            }
        });
    }

    @Override
    public void savePaymentInfo(final PaymentInfoEvent info, final CallContext context) {
        paymentSqlDao.inTransaction(new Transaction<Void, PaymentSqlDao>() {
            @Override
            public Void inTransaction(PaymentSqlDao transactional, TransactionStatus status) throws Exception {
                transactional.insertPaymentInfo(info, context);
                Long recordId = transactional.getRecordId(info.getId().toString());
                EntityHistory<PaymentInfoEvent> history = new EntityHistory<PaymentInfoEvent>(info.getId(), recordId, info, ChangeType.INSERT);
                transactional.insertHistoryFromTransaction(history, context);

                Long historyRecordId = transactional.getHistoryRecordId(recordId);
                EntityAudit audit = new EntityAudit(TableName.PAYMENTS, historyRecordId, ChangeType.INSERT);
                transactional.insertAuditFromTransaction(audit, context);

                return null;
            }
        });
    }

    @Override
    public void updatePaymentAttemptWithPaymentId(final UUID paymentAttemptId, final UUID id, final CallContext context) {
        paymentAttemptSqlDao.inTransaction(new Transaction<Void, PaymentAttemptSqlDao>() {
            @Override
            public Void inTransaction(PaymentAttemptSqlDao transactional, TransactionStatus status) throws Exception {
                transactional.updatePaymentAttemptWithPaymentId(paymentAttemptId.toString(), id.toString(), context);
                PaymentAttempt paymentAttempt = transactional.getPaymentAttemptById(paymentAttemptId.toString());
                Long recordId = transactional.getRecordId(paymentAttemptId.toString());
                EntityHistory<PaymentAttempt> history = new EntityHistory<PaymentAttempt>(paymentAttemptId, recordId, paymentAttempt, ChangeType.UPDATE);
                transactional.insertHistoryFromTransaction(history, context);

                Long historyRecordId = transactional.getHistoryRecordId(recordId);
                EntityAudit audit = new EntityAudit(TableName.PAYMENT_ATTEMPTS, historyRecordId, ChangeType.UPDATE);
                transactional.insertAuditFromTransaction(audit, context);

                return null;
            }
        });
    }

    @Override
    public void updatePaymentInfo(final String type, final UUID paymentId, final String cardType,
                                  final String cardCountry, final CallContext context) {
        paymentSqlDao.inTransaction(new Transaction<Void, PaymentSqlDao>() {
            @Override
            public Void inTransaction(PaymentSqlDao transactional, TransactionStatus status) throws Exception {
                transactional.updatePaymentInfo(type, paymentId.toString(), cardType, cardCountry, context);
                PaymentInfoEvent paymentInfo = transactional.getPaymentInfo(paymentId.toString());

                Long recordId = transactional.getRecordId(paymentId.toString());
                EntityHistory<PaymentInfoEvent> history = new EntityHistory<PaymentInfoEvent>(paymentInfo.getId(), recordId, paymentInfo, ChangeType.UPDATE);
                transactional.insertHistoryFromTransaction(history, context);

                Long historyRecordId = transactional.getHistoryRecordId(recordId);
                EntityAudit audit = new EntityAudit(TableName.PAYMENT_HISTORY, historyRecordId, ChangeType.UPDATE);
                transactional.insertAuditFromTransaction(audit, context);

                return null;
            }
        });
    }

    @Override
    public List<PaymentInfoEvent> getPaymentInfoList(List<String> invoiceIds) {
        if (invoiceIds == null || invoiceIds.size() == 0) {
            return ImmutableList.<PaymentInfoEvent>of();
        } else {
            return paymentSqlDao.getPaymentInfoList(invoiceIds);
        }
    }

    @Override
    public PaymentInfoEvent getLastPaymentInfo(List<String> invoiceIds) {
        if (invoiceIds == null || invoiceIds.size() == 0) {
            return null;
        } else {
            return paymentSqlDao.getLastPaymentInfo(invoiceIds);
        }
    }

    @Override
    public List<PaymentAttempt> getPaymentAttemptsForInvoiceIds(List<String> invoiceIds) {
        if (invoiceIds == null || invoiceIds.size() == 0) {
            return ImmutableList.<PaymentAttempt>of();
        } else {
            return paymentAttemptSqlDao.getPaymentAttemptsForInvoiceIds(invoiceIds);
        }
    }

    @Override
    public PaymentAttempt getPaymentAttemptById(UUID paymentAttemptId) {
        return paymentAttemptSqlDao.getPaymentAttemptById(paymentAttemptId.toString());
    }

    @Override
    public PaymentInfoEvent getPaymentInfoForPaymentAttemptId(String paymentAttemptIdStr) {
        return paymentSqlDao.getPaymentInfoForPaymentAttemptId(paymentAttemptIdStr);
    }

}
