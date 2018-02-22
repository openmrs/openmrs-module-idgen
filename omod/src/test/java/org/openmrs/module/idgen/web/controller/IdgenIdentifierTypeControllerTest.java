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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.contract.IdentifierSource;
import org.openmrs.module.idgen.contract.IdentifierType;
import org.openmrs.module.idgen.web.services.IdentifierTypeServiceWrapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class IdgenIdentifierTypeControllerTest {
    @InjectMocks
    private IdgenIdentifierTypeController controller;

    @Mock
    private IdentifierTypeServiceWrapper serviceWrapper;

    @Before
    public void before() throws Exception {
        initMocks(this);
        mockStatic(Context.class);
    }

    @Test
    public void shouldRespondWith401WhenNotAuthenticated() throws IOException {
        when(Context.isAuthenticated()).thenReturn(false);
        ResponseEntity<String> response = controller.getPrimaryAndExtraIdentifierTypes();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldRespondWith401WhenGetIdentifierTypePrivilegeIsNotGivenToUser() throws IOException {
        when(Context.isAuthenticated()).thenReturn(true);
        when(Context.hasPrivilege("Get Identifier Types")).thenReturn(false);
        ResponseEntity<String> response = controller.getPrimaryAndExtraIdentifierTypes();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void ShouldReturnAllIdentifierType() throws Exception {
        when(Context.isAuthenticated()).thenReturn(true);
        when(Context.hasPrivilege("Get Identifier Types")).thenReturn(true);

        final IdentifierSource identifierSource = new IdentifierSource("source-uuid", "some name", "SOM");
        final IdentifierType openmrsIdentifierType = new IdentifierType("uuid", "openmrs", "description", ".*", true, true, Arrays.asList(identifierSource));

        when(serviceWrapper.getPrimaryAndExtraIdentifierTypes()).thenReturn(Arrays.asList(openmrsIdentifierType));

        ResponseEntity<String> allIdentifierType = controller.getPrimaryAndExtraIdentifierTypes();

        List<IdentifierType> identifierTypes = new ObjectMapper().readValue(allIdentifierType.getBody(), new TypeReference<List<IdentifierType>>() {});
        assertEquals(1, identifierTypes.size());
        assertEquals(openmrsIdentifierType.getUuid(), identifierTypes.get(0).getUuid());
        assertEquals(openmrsIdentifierType.getFormat(), identifierTypes.get(0).getFormat());
        assertEquals(openmrsIdentifierType.getName(), identifierTypes.get(0).getName());
        assertEquals(openmrsIdentifierType.getIdentifierSources().size(), identifierTypes.get(0).getIdentifierSources().size());

    }


}