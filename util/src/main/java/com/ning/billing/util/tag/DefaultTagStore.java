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

import com.ning.billing.util.dao.ObjectType;
import com.ning.billing.util.entity.collection.EntityCollectionBase;

public class DefaultTagStore extends EntityCollectionBase<Tag> implements TagStore {
    public DefaultTagStore(final UUID objectId, final ObjectType objectType) {
        super(objectId, objectType);
    }

    @Override
    public String getEntityKey(final Tag entity) {
        return entity.getTagDefinitionName();
    }

    @Override
    /***
     * Collates the contents of the TagStore to determine if payments should be processed
     * @return true if no tags contraindicate payment processing
     */
    public boolean processPayment() {
        for (Tag tag : entities.values()) {
            if (tag instanceof ControlTag) {
                ControlTag controlTag = (ControlTag) tag;
                if (controlTag.getControlTagType() == ControlTagType.AUTO_PAY_OFF) {
                    return false;
                }
            }
        }

        return true;
    }

    /***
     * Collates the contents of the TagStore to determine if invoices should be generated
     * @return true if no tags contraindicate invoice generation
     */
    @Override
    public boolean generateInvoice() {
        for (Tag tag : entities.values()) {
            if (tag instanceof ControlTag) {
                ControlTag controlTag = (ControlTag) tag;
                if (controlTag.getControlTagType() == ControlTagType.AUTO_INVOICING_OFF) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean containsTagForDefinition(final TagDefinition tagDefinition) {
        for (Tag tag : entities.values()) {
            if (tag.getTagDefinitionName().equals(tagDefinition.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsTagForControlTagType(final ControlTagType controlTagType) {
        for (Tag tag : entities.values()) {
            if (tag.getTagDefinitionName().equals(controlTagType.toString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Tag remove(TagDefinition tagDefinition) {
        Tag tag = entities.get(tagDefinition.getName());
        return (tag == null) ? null : entities.remove(tag);
    }
}