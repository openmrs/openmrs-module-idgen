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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.contract.IdentifierType;
import org.openmrs.module.idgen.mapper.IdentifierSourceListMapper;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.IdgenWsConstants;

public class IdentifierTypeServiceWrapperImpl  extends BaseOpenmrsService implements IdentifierTypeServiceWrapper {

    public List<IdentifierType> getPrimaryAndExtraIdentifierTypes() {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        AdministrationService administrationService = Context.getAdministrationService();
        Map<PatientIdentifierType, List<IdentifierSource>> identifierSourcesByType = identifierSourceService.getIdentifierSourcesByType(false);
        String primaryIdentifierTypeUuid = administrationService.getGlobalProperty(IdgenWsConstants.GP_PRIMARY_IDTYPE);
        String extraIdentifierTypeUuid = administrationService.getGlobalProperty(IdgenWsConstants.GP_EXTRA_IDTYPES);
        final String[] extraIdentifierTypeUuids = extraIdentifierTypeUuid != null ? extraIdentifierTypeUuid.split(",") : new String[]{};
        return mapToContractObject(identifierSourcesByType, primaryIdentifierTypeUuid, Arrays.asList(extraIdentifierTypeUuids));
    }

    private List<IdentifierType> mapToContractObject(Map<PatientIdentifierType, List<IdentifierSource>> identifierSourcesByType,
                                                     String primaryIdentifierTypeUuid, List<String> extraIdentifierTypeUuids) {
        List<IdentifierType> identifierTypes = new ArrayList<IdentifierType>();
        Set<PatientIdentifierType> patientIdentifierTypes = identifierSourcesByType.keySet();
        for (PatientIdentifierType patientIdentifierType : patientIdentifierTypes) {
            boolean isPrimary = primaryIdentifierTypeUuid.equals(patientIdentifierType.getUuid());
            if(isPrimary || extraIdentifierTypeUuids.contains(patientIdentifierType.getUuid())) {
                List<org.openmrs.module.idgen.contract.IdentifierSource> identifierSources = IdentifierSourceListMapper.map(identifierSourcesByType.get(patientIdentifierType));
                IdentifierType identifierType = new IdentifierType(patientIdentifierType.getUuid(), patientIdentifierType.getName(),
                        patientIdentifierType.getDescription(), patientIdentifierType.getFormat(), patientIdentifierType.getRequired(), isPrimary, identifierSources);
                identifierTypes.add(identifierType);
            }
        }
        return identifierTypes;
    }
}