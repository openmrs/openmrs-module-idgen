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
package org.openmrs.module.idgen.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.idgen.web.services.IdentifierSourceServiceWrapper;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class IdgenIdentifierSourceControllerTest {
    @InjectMocks
    private IdgenIdentifierSourceController controller;

    @Mock
    private IdentifierSourceServiceWrapper identifierSourceServiceWrapper;

    @Test
    public void shouldGetAllIdentifierSourcesOfPrimaryIdentifierType() throws Exception {
        List<org.openmrs.module.idgen.contract.IdentifierSource> identifierSources = new ArrayList<org.openmrs.module.idgen.contract.IdentifierSource>() {{
            this.add(new org.openmrs.module.idgen.contract.IdentifierSource("uuid", "name", "GAN"));
        }};
        when(identifierSourceServiceWrapper.getAllIdentifierSourcesOfPrimaryIdentifierType()).thenReturn(identifierSources);

        String resultIdentifierResources = controller.getAllIdentifierSourcesOfPrimaryIdentifierType();

        assertTrue(resultIdentifierResources.contains("\"uuid\":\"uuid\""));
        assertTrue(resultIdentifierResources.contains("\"name\":\"name\""));
        assertTrue(resultIdentifierResources.contains("\"prefix\":\"GAN\""));
    }
}