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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class IdentifierSourceServiceWrapperImplTest {
    @Mock
    private SequentialIdentifierGenerator identifierSource;
    @Mock
    private IdentifierSourceService identifierSourceService;
    @Mock
    private AdministrationService administrationService;
    @Mock
    private PatientService patientService;

    private IdentifierSourceServiceWrapperImpl identifierSourceServiceWrapperImpl;

    @Before
    public void before() throws Exception {
        initMocks(this);
        mockStatic(Context.class);
        identifierSourceServiceWrapperImpl = new IdentifierSourceServiceWrapperImpl();
    }

    @Test
    public void shouldGenerateIdentifier() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);
        when(identifierSource.getName()).thenReturn("OPD");
        when(identifierSourceService.getAllIdentifierSources(false)).thenReturn(new ArrayList<IdentifierSource>() {{
            this.add(identifierSource);
        }});

        identifierSourceServiceWrapperImpl.generateIdentifier("OPD", "New HIV Patient");

        verify(identifierSourceService).generateIdentifier(identifierSource, "New HIV Patient");
    }

    @Test
    public void shouldGenerateIdentifierUsingIdentifierSourceUuid() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);
        when(identifierSourceService.getIdentifierSourceByUuid("dead-cafe")).thenReturn(identifierSource);

        identifierSourceServiceWrapperImpl.generateIdentifierUsingIdentifierSourceUuid("dead-cafe", "");

        verify(identifierSourceService).generateIdentifier(identifierSource, "");
        verify(identifierSourceService).getIdentifierSourceByUuid("dead-cafe");
    }

    @Test
    public void shouldGetSequenceValue() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);

        ArrayList<IdentifierSource> identifierSources = new ArrayList<IdentifierSource>();
        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("GAN");
        identifierSources.add(sequentialIdentifierGenerator);

        when(identifierSourceService.getAllIdentifierSources(false)).thenReturn(identifierSources);
        when(identifierSourceService.getSequenceValue(sequentialIdentifierGenerator)).thenReturn((long)123456);

        String identifier = identifierSourceServiceWrapperImpl.getSequenceValue("GAN");
        verify(identifierSourceService).getSequenceValue(sequentialIdentifierGenerator);
        assertEquals("123456", identifier);
    }

    @Test
    public void shouldGetSequenceValueUsingIdentifierSourceUuid() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);

        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("GAN");

        when(identifierSourceService.getIdentifierSourceByUuid("dead-cafe")).thenReturn(sequentialIdentifierGenerator);
        when(identifierSourceService.getSequenceValue(sequentialIdentifierGenerator)).thenReturn((long)123456);

        String identifier = identifierSourceServiceWrapperImpl.getSequenceValueUsingIdentifierSourceUuid("dead-cafe");
        verify(identifierSourceService, times(1)).getIdentifierSourceByUuid("dead-cafe");
        verify(identifierSourceService).getSequenceValue(sequentialIdentifierGenerator);
        assertEquals("123456", identifier);
    }

    @Test
    public void shouldSaveSequenceValue() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);

        ArrayList<IdentifierSource> identifierSources = new ArrayList<IdentifierSource>();
        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("GAN");
        identifierSources.add(sequentialIdentifierGenerator);

        when(identifierSourceService.getAllIdentifierSources(false)).thenReturn(identifierSources);

        Long identifier = identifierSourceServiceWrapperImpl.saveSequenceValue((long) 1234567, "GAN");
        verify(identifierSourceService).saveSequenceValue(sequentialIdentifierGenerator, (long) 1234567);
        assertEquals("1234567", identifier.toString());
    }

    @Test
    public void shouldSaveSequenceValueUsingIdentifierSourceUuid() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);

        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("GAN");

        when(identifierSourceService.getIdentifierSourceByUuid("dead-cafe")).thenReturn(sequentialIdentifierGenerator);

        Long identifier = identifierSourceServiceWrapperImpl.saveSequenceValueUsingIdentifierSourceUuid((long) 1234567, "dead-cafe");
        verify(identifierSourceService).saveSequenceValue(sequentialIdentifierGenerator, (long) 1234567);
        assertEquals("1234567", identifier.toString());
    }


    @Test
    public void shouldGetAllIdentifierSources() {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);

        ArrayList<IdentifierSource> identifierSources = new ArrayList<IdentifierSource>();
        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("name");
        sequentialIdentifierGenerator.setUuid("uuid");
        sequentialIdentifierGenerator.setPrefix("GAN");
        identifierSources.add(sequentialIdentifierGenerator);

        when(identifierSourceService.getAllIdentifierSources(false)).thenReturn(identifierSources);

        List<org.openmrs.module.idgen.contract.IdentifierSource> allIdentifierSources = identifierSourceServiceWrapperImpl.getAllIdentifierSources();

        assertEquals(allIdentifierSources.size(), 1);
        assertEquals("name",(allIdentifierSources.get(0).getName()));
        assertEquals("uuid",(allIdentifierSources.get(0).getUuid()));
        assertEquals("GAN",(allIdentifierSources.get(0).getPrefix()));
    }

    @Test
    public void shouldGetAllIdentifierSourcesBasedOnPrimaryIdentifierType() {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);

        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(administrationService.getGlobalProperty("openmrs.primaryIdentifierType")).thenReturn("dead-cafe");
        when(Context.getPatientService()).thenReturn(patientService);
        PatientIdentifierType patientIdentifierType = new PatientIdentifierType(1);
        patientIdentifierType.setName("abcd");
        PatientIdentifierType patientIdentifierType2 = new PatientIdentifierType(1);
        patientIdentifierType2.setName("abcd");
        when(patientService.getPatientIdentifierTypeByUuid("dead-cafe")).thenReturn(patientIdentifierType2);

        Map<PatientIdentifierType, List<IdentifierSource>> identifierSourcesByType = new HashMap<PatientIdentifierType, List<IdentifierSource>>();

        List<IdentifierSource> identifierSources = new ArrayList<IdentifierSource>();
        SequentialIdentifierGenerator sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator.setName("GAN");
        sequentialIdentifierGenerator.setUuid("alive-cafe");
        sequentialIdentifierGenerator.setPrefix("GAN");
        identifierSources.add(sequentialIdentifierGenerator);
        identifierSourcesByType.put(patientIdentifierType, identifierSources);

        List<IdentifierSource> identifierSources2 = new ArrayList<IdentifierSource>();
        SequentialIdentifierGenerator sequentialIdentifierGenerator2 = new SequentialIdentifierGenerator();
        sequentialIdentifierGenerator2.setName("OMRS");
        sequentialIdentifierGenerator2.setUuid("dead-cafe");
        sequentialIdentifierGenerator2.setPrefix("OMRS");
        identifierSources2.add(sequentialIdentifierGenerator2);
        identifierSourcesByType.put(patientIdentifierType2, identifierSources2);

        when(identifierSourceService.getIdentifierSourcesByType(false)).thenReturn(identifierSourcesByType);

        List<org.openmrs.module.idgen.contract.IdentifierSource> allIdentifierSources = identifierSourceServiceWrapperImpl.getAllIdentifierSourcesOfPrimaryIdentifierType();

        assertEquals(allIdentifierSources.size(), 1);
        assertEquals("OMRS",(allIdentifierSources.get(0).getName()));
        assertEquals("dead-cafe",(allIdentifierSources.get(0).getUuid()));
        assertEquals("OMRS",(allIdentifierSources.get(0).getPrefix()));
    }

}