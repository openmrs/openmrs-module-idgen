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

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.contract.IdentifierSource;
import org.openmrs.module.idgen.contract.IdentifierType;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.IdgenWsConstants;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class IdentifierTypeServiceWrapperImplTest {
    @Mock
    private IdentifierSourceService identifierSourceService;
    @Mock
    private AdministrationService administrationService;

    @Mock
    private PatientService patientService;

    private IdentifierTypeServiceWrapperImpl serviceWrapperImp;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(Context.class);
        serviceWrapperImp = new IdentifierTypeServiceWrapperImpl();

    }

    @Test
    public void shouldGetAllIdentifierType() throws Exception {
        final IdentifierType primaryIdentifierType = new IdentifierType("primary-identifier-type-uuid", "bahmni", "description", ".*", true, true, new ArrayList<IdentifierSource>());
        final IdentifierType extraIdentifierType1 = new IdentifierType("extra-identifier-type-uuid1", "pan", "description", ".*", false, false, new ArrayList<IdentifierSource>());
        final IdentifierType extraIdentifierType2 = new IdentifierType("extra-identifier-type-uuid2", "phone no", "description", ".*", false, false, new ArrayList<IdentifierSource>());

        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);

        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(administrationService.getGlobalProperty(IdgenWsConstants.GP_PRIMARY_IDTYPE)).thenReturn("primary-identifier-type-uuid");
        when(administrationService.getGlobalProperty(IdgenWsConstants.GP_EXTRA_IDTYPES)).thenReturn("extra-identifier-type-uuid1,extra-identifier-type-uuid2");
        PatientIdentifierType primaryPatientIdentifierType = new PatientIdentifierType(1);
        primaryPatientIdentifierType.setName("abcd");
        primaryPatientIdentifierType.setUuid("primary-identifier-type-uuid");
        PatientIdentifierType extraPatientIdentifierType1 = new PatientIdentifierType(1);
        extraPatientIdentifierType1.setName("abcd");
        extraPatientIdentifierType1.setUuid("extra-identifier-type-uuid1");

        PatientIdentifierType extraPatientIdentifierType2 = new PatientIdentifierType(1);
        extraPatientIdentifierType2.setName("abcd");
        extraPatientIdentifierType2.setUuid("extra-identifier-type-uuid2");

        when(patientService.getPatientIdentifierTypeByUuid("primary-identifier-type-uuid")).thenReturn(primaryPatientIdentifierType);
        when(patientService.getPatientIdentifierTypeByUuid("extra-identifier-type-uuid1")).thenReturn(extraPatientIdentifierType1);
        when(patientService.getPatientIdentifierTypeByUuid("extra-identifier-type-uuid2")).thenReturn(extraPatientIdentifierType2);

        Map<PatientIdentifierType, List<org.openmrs.module.idgen.IdentifierSource>> identifierSourcesByType = new HashMap<PatientIdentifierType, List<org.openmrs.module.idgen.IdentifierSource>>();

        List<org.openmrs.module.idgen.IdentifierSource> identifierSources = new ArrayList<org.openmrs.module.idgen.IdentifierSource>();
        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("GAN");
        sequentialIdentifierGenerator.setUuid("alive-cafe");
        sequentialIdentifierGenerator.setPrefix("GAN");
        identifierSources.add(sequentialIdentifierGenerator);
        identifierSourcesByType.put(primaryPatientIdentifierType, identifierSources);

        List<org.openmrs.module.idgen.IdentifierSource> identifierSources2 = new ArrayList<org.openmrs.module.idgen.IdentifierSource>();
        SequentialIdentifierGenerator sequentialIdentifierGenerator2 = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator2.setName("OMRS");
        sequentialIdentifierGenerator2.setUuid("dead-cafe");
        sequentialIdentifierGenerator2.setPrefix("OMRS");
        identifierSources2.add(sequentialIdentifierGenerator2);
        identifierSourcesByType.put(extraPatientIdentifierType2, identifierSources2);

        identifierSourcesByType.put(extraPatientIdentifierType1, new ArrayList<org.openmrs.module.idgen.IdentifierSource>());
        when(identifierSourceService.getIdentifierSourcesByType(false)).thenReturn(identifierSourcesByType);
        final List<IdentifierType> allIdentifierTypes = serviceWrapperImp.getPrimaryAndExtraIdentifierTypes();

        assertEquals(3, allIdentifierTypes.size());
        assertThat(allIdentifierTypes, hasItem(primaryIdentifierType));
        assertThat(allIdentifierTypes, hasItem(extraIdentifierType1));
        assertThat(allIdentifierTypes, hasItem(extraIdentifierType2));

    }
}