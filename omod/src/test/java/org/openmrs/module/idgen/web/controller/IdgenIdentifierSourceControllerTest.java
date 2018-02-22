/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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