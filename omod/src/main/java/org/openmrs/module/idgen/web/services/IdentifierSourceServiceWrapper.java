/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.web.services;


import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.idgen.contract.IdentifierSource;
import org.openmrs.module.idgen.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
    public interface IdentifierSourceServiceWrapper extends OpenmrsService {

    @Authorized(value = {PrivilegeConstants.PRIV_MANAGE_IDENTIFIER_SEQUENCE}, requireAll = true)
    Long saveSequenceValue(long identifier, String sourceName) throws Exception;

    String getSequenceValue(String sourceName) throws Exception;

    String generateIdentifier(String sourceName, String comment) throws Exception;

    List<IdentifierSource> getAllIdentifierSources();

    List<IdentifierSource> getAllIdentifierSourcesOfPrimaryIdentifierType();

    String generateIdentifierUsingIdentifierSourceUuid(String identifierSourceUuid, String comment) throws Exception;

    String getSequenceValueUsingIdentifierSourceUuid(String uuid) throws Exception;

    Long saveSequenceValueUsingIdentifierSourceUuid(long identifier, String uuid) throws Exception;

}