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

package com.ning.billing.util.tag.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ning.billing.util.dao.ObjectType;
import com.ning.billing.util.tag.dao.TagDao;

import com.google.inject.Inject;
import com.ning.billing.util.callcontext.CallContext;
import com.ning.billing.util.api.TagDefinitionApiException;
import com.ning.billing.util.api.TagUserApi;
import com.ning.billing.util.tag.Tag;
import com.ning.billing.util.tag.TagDefinition;
import com.ning.billing.util.tag.dao.TagDefinitionDao;

public class DefaultTagUserApi implements TagUserApi {
    private final TagDefinitionDao tagDefinitionDao;
    private final TagDao tagDao;

    @Inject
    public DefaultTagUserApi(TagDefinitionDao tagDefinitionDao, TagDao tagDao) {
        this.tagDefinitionDao = tagDefinitionDao;
        this.tagDao = tagDao;
    }

    @Override
    public List<TagDefinition> getTagDefinitions() {
        return tagDefinitionDao.getTagDefinitions();
    }

    @Override
    public TagDefinition create(final String definitionName, final String description, final CallContext context) throws TagDefinitionApiException {
        return tagDefinitionDao.create(definitionName, description, context);
    }

    @Override
    public void deleteAllTagsForDefinition(final String definitionName, final CallContext context)
            throws TagDefinitionApiException {
        tagDefinitionDao.deleteAllTagsForDefinition(definitionName, context);
    }

    @Override
    public void deleteTagDefinition(final String definitionName, final CallContext context) throws TagDefinitionApiException {
        tagDefinitionDao.deleteAllTagsForDefinition(definitionName, context);
    }

	@Override
	public TagDefinition getTagDefinition(final String name)
			throws TagDefinitionApiException {
		return tagDefinitionDao.getByName(name);
	}

    @Override
    public void addTags(UUID objectId, ObjectType objectType, List<TagDefinition> tagDefinitions, CallContext context) {
        tagDao.insertTags(objectId, objectType, tagDefinitions, context);
    }

    @Override
    public void addTag(UUID objectId, ObjectType objectType, TagDefinition tagDefinition, CallContext context) {
        tagDao.insertTag(objectId, objectType, tagDefinition, context);
    }

    @Override
    public void removeTag(UUID objectId, ObjectType objectType, TagDefinition tagDefinition, CallContext context) {
        tagDao.deleteTag(objectId, objectType, tagDefinition, context);
    }

    @Override
    public void removeTags(UUID objectId, ObjectType objectType, List<TagDefinition> tagDefinitions, CallContext context) {
        // TODO: consider making this batch
        for (TagDefinition tagDefinition : tagDefinitions) {
            tagDao.deleteTag(objectId, objectType, tagDefinition, context);
        }
    }

    @Override
    public Map<String, Tag> getTags(final UUID objectId, final ObjectType objectType) {
        return tagDao.loadEntities(objectId, objectType);
    }

//    @Override
//    public Tag createControlTags(String controlTagName) throws TagDefinitionApiException {
//        ControlTagType type = null;
//        for(ControlTagType t : ControlTagType.values()) {
//            if(t.toString().equals(controlTagName)) {
//                type = t;
//            }
//        }
//
//        if(type == null) {
//            throw new TagDefinitionApiException(ErrorCode.CONTROL_TAG_DOES_NOT_EXIST, controlTagName);
//        }
//        return new DefaultControlTag(type);
//    }
//
//    @Override
//    public Tag createDescriptiveTags(List) throws TagDefinitionApiException {
//        TagDefinition tagDefinition = getTagDefinition(tagDefinitionName);
//
//        return new DescriptiveTag(tagDefinition);
//    }
}
