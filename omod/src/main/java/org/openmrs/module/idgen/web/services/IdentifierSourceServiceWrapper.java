/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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