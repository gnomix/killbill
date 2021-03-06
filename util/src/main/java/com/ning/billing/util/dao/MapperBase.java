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

package com.ning.billing.util.dao;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public abstract class MapperBase {
    protected DateTime getDate(ResultSet rs, String fieldName) throws SQLException {
        final Timestamp resultStamp = rs.getTimestamp(fieldName);
        return rs.wasNull() ? null : new DateTime(resultStamp).toDateTime(DateTimeZone.UTC);
    }

    protected UUID getUUID(ResultSet resultSet, String fieldName) throws SQLException {
        String result = resultSet.getString(fieldName);
        return result == null ? null : UUID.fromString(result);
    }
}
