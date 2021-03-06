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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.callcontext.CallContextBinder;
import com.ning.billing.util.dao.BinderBase;
import com.ning.billing.util.dao.EntityHistory;
import com.ning.billing.util.dao.MapperBase;
import com.ning.billing.util.entity.dao.UpdatableEntitySqlDao;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.CloseMe;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.ExternalizedSqlViaStringTemplate3;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.unstable.BindIn;

import com.ning.billing.payment.api.DefaultPaymentInfoEvent;
import com.ning.billing.payment.api.PaymentInfoEvent;

@ExternalizedSqlViaStringTemplate3()
@RegisterMapper(PaymentSqlDao.PaymentInfoMapper.class)
public interface PaymentSqlDao extends Transactional<PaymentSqlDao>, UpdatableEntitySqlDao<PaymentInfoEvent>, CloseMe {
    @SqlQuery
    PaymentInfoEvent getPaymentInfoForPaymentAttemptId(@Bind("paymentAttemptId") String paymentAttemptId);

    @SqlUpdate
    void updatePaymentInfo(@Bind("paymentMethod") String paymentMethod,
                           @Bind("id") String paymentId,
                           @Bind("cardType") String cardType,
                           @Bind("cardCountry") String cardCountry,
                           @CallContextBinder CallContext context);

    @SqlQuery
    List<PaymentInfoEvent> getPaymentInfoList(@BindIn("invoiceIds") final List<String> invoiceIds);

    @SqlQuery
    PaymentInfoEvent getLastPaymentInfo(@BindIn("invoiceIds") final List<String> invoiceIds);

    @SqlUpdate
    void insertPaymentInfo(@Bind(binder = PaymentInfoBinder.class) final PaymentInfoEvent paymentInfo,
                           @CallContextBinder final CallContext context);

    @SqlQuery
    PaymentInfoEvent getPaymentInfo(@Bind("id") final String paymentId);

    @Override
    @SqlUpdate
    public void insertHistoryFromTransaction(@PaymentHistoryBinder final EntityHistory<PaymentInfoEvent> account,
                                            @CallContextBinder final CallContext context);

    public static final class PaymentInfoBinder extends BinderBase implements Binder<Bind, PaymentInfoEvent> {
        @Override
        public void bind(@SuppressWarnings("rawtypes") SQLStatement stmt, Bind bind, PaymentInfoEvent paymentInfo) {
            stmt.bind("id", paymentInfo.getId().toString());
            stmt.bind("externalPaymentId", paymentInfo.getExternalPaymentId());
            stmt.bind("amount", paymentInfo.getAmount());
            stmt.bind("refundAmount", paymentInfo.getRefundAmount());
            stmt.bind("paymentNumber", paymentInfo.getPaymentNumber());
            stmt.bind("bankIdentificationNumber", paymentInfo.getBankIdentificationNumber());
            stmt.bind("status", paymentInfo.getStatus());
            stmt.bind("paymentType", paymentInfo.getType());
            stmt.bind("referenceId", paymentInfo.getReferenceId());
            stmt.bind("paymentMethodId", paymentInfo.getPaymentMethodId());
            stmt.bind("paymentMethod", paymentInfo.getPaymentMethod());
            stmt.bind("cardType", paymentInfo.getCardType());
            stmt.bind("cardCountry", paymentInfo.getCardCountry());
            stmt.bind("effectiveDate", getDate(paymentInfo.getEffectiveDate()));
        }
    }

    public static class PaymentInfoMapper extends MapperBase implements ResultSetMapper<PaymentInfoEvent> {
        @Override
        public PaymentInfoEvent map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
            UUID id = getUUID(rs, "id");
            String externalPaymentId = rs.getString("external_payment_id");
            BigDecimal amount = rs.getBigDecimal("amount");
            BigDecimal refundAmount = rs.getBigDecimal("refund_amount");
            String paymentNumber = rs.getString("payment_number");
            String bankIdentificationNumber = rs.getString("bank_identification_number");
            String status = rs.getString("status");
            String type = rs.getString("payment_type");
            String referenceId = rs.getString("reference_id");
            String paymentMethodId = rs.getString("payment_method_id");
            String paymentMethod = rs.getString("payment_method");
            String cardType = rs.getString("card_type");
            String cardCountry = rs.getString("card_country");            
            DateTime effectiveDate = getDate(rs, "effective_date");

            UUID userToken = null; //rs.getString("user_token") != null ? UUID.fromString(rs.getString("user_token")) : null;
            
            return new DefaultPaymentInfoEvent(id,
                                   externalPaymentId,
                                   amount,
                                   refundAmount,
                                   bankIdentificationNumber,
                                   paymentNumber,
                                   status,
                                   type,
                                   referenceId,
                                   paymentMethodId,
                                   paymentMethod,
                                   cardType,
                                   cardCountry,
                                   userToken,
                                   effectiveDate);
        }
    }

}
