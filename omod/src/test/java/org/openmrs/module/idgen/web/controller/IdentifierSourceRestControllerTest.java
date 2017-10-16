/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.web.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class IdentifierSourceRestControllerTest extends MainResourceControllerTest {
	
    public static final String SEQUENTIAL_IDENTIFIER_SOURCE_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a901";
    public static final String REMOTE_IDENTIFIER_SOURCE_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a902";
    public static final String POOL_SOURCE_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a903";
    public static final String IDENTIFIER_TYPE_UUID = "2f470aa8-1d73-43b7-81b5-01f0c0dfa53c";
    public static final String IDENTIFIER_SOURCE_TO_PURGE_UUID = "0d42284f-9e9b-4a81-a88b-8bb42bc0a906";
    public static final String SEQUENTIAL_IDENTIFIER_SOURCE_TYPE = "SequentialIdentifierGenerator";
    public static final String REMOTE_IDENTIFIER_SOURCE_TYPE = "RemoteIdentifierSource";
    public static final String IDENTIFIER_POOL_SOURCE_TYPE = "IdentifierPool";

    private IdentifierSourceService service;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
        this.service = Context.getService(IdentifierSourceService.class);
    }

    @Override
    public String getURI() {
        return "idgen/identifiersource";
    }

    @Override
    public String getUuid() {
        return SEQUENTIAL_IDENTIFIER_SOURCE_UUID;
    }

    @Override
    public long getAllCount()  {
        return service.getAllIdentifierSources(false).size();
    }

    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        super.shouldGetAll();
    }

    @Test
    public void shouldGetAnIdentifierSourceByUuid() throws Exception {
        SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
        assertNotNull(result);
        assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
    }

    @Test
    public void shouldEditASequentialIdentifierSource() throws Exception {
        String name = "{\"name\":\"Updated Sequential Identifier Name\"}";
        assertEquals("Test Sequential Generator", service.getIdentifierSourceByUuid(getUuid()).getName());
        handle(newPostRequest(getURI() + "/" + getUuid(), name));
        assertEquals("Updated Sequential Identifier Name", service.getIdentifierSourceByUuid(getUuid()).getName());
    }

    @Test
    public void shouldEditARemoteIdentifierSource() throws Exception {
        String name = "{\"name\":\"Updated Remote Identifier Name\"}";
        assertEquals("Test Remote Source", service.getIdentifierSourceByUuid(REMOTE_IDENTIFIER_SOURCE_UUID).getName());
        handle(newPostRequest(getURI() + "/" + getUuid(), name));
        assertEquals("Updated Remote Identifier Name", service.getIdentifierSourceByUuid(getUuid()).getName());
    }

    @Test
    public void shouldEditAnIdentifierPoolSource() throws Exception {
        String name = "{\"name\":\"Updated Identifier Pool Name\"}";
        assertEquals("Test Identifier Pool", service.getIdentifierSourceByUuid(POOL_SOURCE_UUID).getName());
        handle(newPostRequest(getURI() + "/" + getUuid(), name));
        assertEquals("Updated Identifier Pool Name", service.getIdentifierSourceByUuid(getUuid()).getName());
    }
    
    @Test
    public void shouldSaveASequentialIdentifierGenerator() throws Exception {
        long initialIdentifierSourceCount = getAllCount();

        SimpleObject identifierSource = new SimpleObject();
        identifierSource.add("name", "test sequential identifier");
        identifierSource.add("description", "This is a test description for the sequential identifier");
        identifierSource.add("identifierType", IDENTIFIER_TYPE_UUID);
        identifierSource.add("firstIdentifierBase", "1");
        identifierSource.add("prefix", "ABC");
        identifierSource.add("suffix", "XYZ");
        identifierSource.add("minLength", "36");
        identifierSource.add("maxLength", "38");
        identifierSource.add("baseCharacterSet", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        identifierSource.add("sourceType", SEQUENTIAL_IDENTIFIER_SOURCE_TYPE);

        String jsonIdentifierSource = new ObjectMapper().writeValueAsString(identifierSource);
        MockHttpServletRequest req = newPostRequest(getURI(), jsonIdentifierSource);

        SimpleObject newIdentifierSource = deserialize(handle(req));
        String newIdentifierSourceUuid = PropertyUtils.getProperty(newIdentifierSource, "uuid").toString();
        SequentialIdentifierGenerator generatedIdentifierSource = (SequentialIdentifierGenerator) service.getIdentifierSourceByUuid(newIdentifierSourceUuid);

        assertEquals(newIdentifierSourceUuid, generatedIdentifierSource.getUuid());
        assertEquals(generatedIdentifierSource.getName(), "test sequential identifier");
        assertEquals(generatedIdentifierSource.getDescription(), "This is a test description for the sequential identifier");
        assertEquals(generatedIdentifierSource.getFirstIdentifierBase(), "1");
        assertEquals(generatedIdentifierSource.getPrefix(), "ABC");
        assertEquals(generatedIdentifierSource.getSuffix(), "XYZ");
        assertEquals(generatedIdentifierSource.getMinLength().toString(), "36");
        assertEquals(generatedIdentifierSource.getMaxLength().toString(), "38");
        assertEquals(generatedIdentifierSource.getBaseCharacterSet(), "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        assertEquals(generatedIdentifierSource.getIdentifierType().getUuid(), IDENTIFIER_TYPE_UUID);
        assertEquals(initialIdentifierSourceCount + 1, getAllCount());
    }

    @Test
    public void shouldSaveARemoteIdentifierGenerator() throws Exception {
        long initialIdentifierSourceCount = getAllCount();

        SimpleObject identifierSource = new SimpleObject();
        identifierSource.add("name", "test remote identifier");
        identifierSource.add("description", "This is a test description for the remote identifier");
        identifierSource.add("url", "http://localhost");
        identifierSource.add("username", "openmrsuser");
        identifierSource.add("password", "openmrspassword");
        identifierSource.add("identifierType", IDENTIFIER_TYPE_UUID);
        identifierSource.add("sourceType", REMOTE_IDENTIFIER_SOURCE_TYPE);

        String jsonIdentifierSource = new ObjectMapper().writeValueAsString(identifierSource);
        MockHttpServletRequest req = newPostRequest(getURI(), jsonIdentifierSource);

        SimpleObject newIdentifierSource = deserialize(handle(req));
        String newIdentifierSourceUuid = PropertyUtils.getProperty(newIdentifierSource, "uuid").toString();
        RemoteIdentifierSource generatedIdentifierSource = (RemoteIdentifierSource) service.getIdentifierSourceByUuid(newIdentifierSourceUuid);

        assertEquals(newIdentifierSourceUuid, generatedIdentifierSource.getUuid());
        assertEquals(generatedIdentifierSource.getName(), "test remote identifier");
        assertEquals(generatedIdentifierSource.getDescription(), "This is a test description for the remote identifier");
        assertEquals(generatedIdentifierSource.getUrl(), "http://localhost");
        assertEquals(generatedIdentifierSource.getUser(), "openmrsuser");
        assertEquals(generatedIdentifierSource.getPassword(), "openmrspassword");
        assertEquals(generatedIdentifierSource.getIdentifierType().getUuid(), IDENTIFIER_TYPE_UUID);
        assertEquals(initialIdentifierSourceCount + 1, getAllCount());
    }

    @Test
    public void shouldSaveAnIdentifierPool() throws Exception {
        long initialIdentifierSourceCount = getAllCount();

        SimpleObject identifierSource = new SimpleObject();
        identifierSource.add("name", "test identifier pool");
        identifierSource.add("description", "This is a test description for the identifier pool");
        identifierSource.add("batchSize", "50");
        identifierSource.add("minPoolSize", "5");
        identifierSource.add("sequential", "true");
        identifierSource.add("refill", "true");
        identifierSource.add("sourceUuid", getUuid());
        identifierSource.add("identifierType", IDENTIFIER_TYPE_UUID);
        identifierSource.add("sourceType", IDENTIFIER_POOL_SOURCE_TYPE);

        String jsonIdentifierSource = new ObjectMapper().writeValueAsString(identifierSource);
        MockHttpServletRequest req = newPostRequest(getURI(), jsonIdentifierSource);

        SimpleObject newIdentifierSource = deserialize(handle(req));
        String newIdentifierSourceUuid = PropertyUtils.getProperty(newIdentifierSource, "uuid").toString();
        IdentifierPool generatedIdentifierSource = (IdentifierPool) service.getIdentifierSourceByUuid(newIdentifierSourceUuid);

        assertEquals(newIdentifierSourceUuid, generatedIdentifierSource.getUuid());
        assertEquals(generatedIdentifierSource.getName(), "test identifier pool");
        assertEquals(generatedIdentifierSource.getDescription(), "This is a test description for the identifier pool");
        assertEquals(generatedIdentifierSource.getBatchSize(), Integer.parseInt("50"));
        assertEquals(generatedIdentifierSource.getMinPoolSize(), Integer.parseInt("5"));
        assertEquals(generatedIdentifierSource.getSource().getUuid(), getUuid());
        assertEquals(initialIdentifierSourceCount + 1, getAllCount());
    }
    
    @Test
    public void shouldThrowAnExceptionWhenARequiredParameterIsMissing() throws Exception {
        expectedException.expect(org.openmrs.module.webservices.validation.ValidationException.class);
        expectedException.expectMessage(allOf(containsString("source type"), containsString("patient identifier type"), containsString("name")));
        
        SimpleObject sequentialIdentifierSource = new SimpleObject();
        sequentialIdentifierSource.add("description", "test identifier source");
        sequentialIdentifierSource.add("sourceType", "");

        String jsonSequentialIdentifierSource = new ObjectMapper().writeValueAsString(sequentialIdentifierSource);
        MockHttpServletRequest req = newPostRequest(getURI(), jsonSequentialIdentifierSource);
        handle(req);
    }

    @Test
    public void shouldDeleteAnIdentifierSource() throws Exception {
        final String reason = "none";

        assertFalse(service.getIdentifierSourceByUuid(getUuid()).isRetired());

        MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
        req.addParameter("reason", reason);
        handle(req);

        assertTrue(service.getIdentifierSourceByUuid(getUuid()).isRetired());
        assertEquals(reason, service.getIdentifierSourceByUuid(getUuid()).getRetireReason());
    }

    @Test
    public void shouldPurgeAnIdentifierSource() throws Exception {
        assertNotNull(service.getIdentifierSourceByUuid(IDENTIFIER_SOURCE_TO_PURGE_UUID));
        handle(newDeleteRequest(getURI() + "/" + IDENTIFIER_SOURCE_TO_PURGE_UUID, new Parameter("purge", "true")));
        assertNull(service.getIdentifierSourceByUuid(IDENTIFIER_SOURCE_TO_PURGE_UUID));
    }

}
