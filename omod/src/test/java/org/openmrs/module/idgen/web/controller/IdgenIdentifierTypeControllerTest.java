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
        final IdentifierType bahmniIdentifierType = new IdentifierType("uuid", "bahmni", "description", ".*", true, true, Arrays.asList(identifierSource));

        when(serviceWrapper.getPrimaryAndExtraIdentifierTypes()).thenReturn(Arrays.asList(bahmniIdentifierType));

        ResponseEntity<String> allIdentifierType = controller.getPrimaryAndExtraIdentifierTypes();

        List<IdentifierType> identifierTypes = new ObjectMapper().readValue(allIdentifierType.getBody(), new TypeReference<List<IdentifierType>>() {});
        assertEquals(1, identifierTypes.size());
        assertEquals(bahmniIdentifierType.getUuid(), identifierTypes.get(0).getUuid());
        assertEquals(bahmniIdentifierType.getFormat(), identifierTypes.get(0).getFormat());
        assertEquals(bahmniIdentifierType.getName(), identifierTypes.get(0).getName());
        assertEquals(bahmniIdentifierType.getIdentifierSources().size(), identifierTypes.get(0).getIdentifierSources().size());

    }


}