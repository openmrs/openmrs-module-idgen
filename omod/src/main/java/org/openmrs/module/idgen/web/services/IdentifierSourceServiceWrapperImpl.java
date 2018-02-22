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

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.mapper.IdentifierSourceListMapper;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public class IdentifierSourceServiceWrapperImpl extends BaseOpenmrsService implements IdentifierSourceServiceWrapper {
    @Override
    public Long saveSequenceValue(long identifier, String sourceName) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        IdentifierSource identifierSource = getIdentifierSource(sourceName);
        identifierSourceService.saveSequenceValue((SequentialIdentifierGenerator)identifierSource, identifier);
        return identifier;
    }

    @Override
    public Long saveSequenceValueUsingIdentifierSourceUuid(long identifier, String uuid) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        IdentifierSource identifierSource = getIdentifierSourceByUuid(uuid);
        identifierSourceService.saveSequenceValue((SequentialIdentifierGenerator)identifierSource, identifier);
        return identifier;
    }

    @Override
    public String getSequenceValue(String sourceName) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        IdentifierSource identifierSource = getIdentifierSource(sourceName);
        Long sequenceValue = identifierSourceService.getSequenceValue((SequentialIdentifierGenerator) identifierSource);
        return sequenceValue.toString();
    }

    @Override
    public String getSequenceValueUsingIdentifierSourceUuid(String uuid) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        IdentifierSource identifierSource = getIdentifierSourceByUuid(uuid);
        Long sequenceValue = identifierSourceService.getSequenceValue((SequentialIdentifierGenerator) identifierSource);
        return sequenceValue.toString();
    }


    @Override
    public String generateIdentifier(String sourceName, String comment) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        IdentifierSource identifierSource = getIdentifierSource(sourceName);
        return identifierSourceService.generateIdentifier(identifierSource, comment);
    }

    @Override
    public List<org.openmrs.module.idgen.contract.IdentifierSource> getAllIdentifierSources() {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        List<IdentifierSource> allIdentifierSources = identifierSourceService.getAllIdentifierSources(false);
        return IdentifierSourceListMapper.map(allIdentifierSources);
    }

    @Override
    public List<org.openmrs.module.idgen.contract.IdentifierSource> getAllIdentifierSourcesOfPrimaryIdentifierType() {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        String primaryIdentifierTypeUuid = Context.getAdministrationService().getGlobalProperty("bahmni.primaryIdentifierType");
        PatientIdentifierType primaryIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(primaryIdentifierTypeUuid);

        Map<PatientIdentifierType, List<IdentifierSource>> identifierSourcesByType = identifierSourceService.getIdentifierSourcesByType(false);
        List<IdentifierSource> primaryIdentifierSourcesList = identifierSourcesByType.get(primaryIdentifierType);

        return IdentifierSourceListMapper.map(primaryIdentifierSourcesList);
    }



    private IdentifierSource getIdentifierSource(String sourceName) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        List<IdentifierSource> allIdentifierSources = identifierSourceService.getAllIdentifierSources(false);
        for (IdentifierSource identifierSource : allIdentifierSources) {
            if (identifierSource.getName().equals(sourceName)) {
                return identifierSource;
            }
        }
        throw new Exception("No matching Identifier source found for: " + sourceName);
    }

    @Override
    public String generateIdentifierUsingIdentifierSourceUuid(String identifierSourceUuid, String comment) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        IdentifierSource identifierSource = getIdentifierSourceByUuid(identifierSourceUuid);
        return identifierSourceService.generateIdentifier(identifierSource, comment);
    }

    private IdentifierSource getIdentifierSourceByUuid(String uuid) throws Exception {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        IdentifierSource identifierSource = identifierSourceService.getIdentifierSourceByUuid(uuid);
        if (identifierSource == null) {
            throw new Exception("No matching Identifier source found for: " + uuid);
        }
        return identifierSource;
    }
}