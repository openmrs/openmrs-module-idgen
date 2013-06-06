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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Tests methods in IdentifierSourceController
 */
public class IdentifierSourceControllerTest {

	@Autowired
	@InjectMocks
	private IdentifierSourceController controller;
	@Mock
	private IdentifierSourceService iss;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void exportIdentifiers_shouldReturnJson() throws Exception {
		Mockito.stub(iss.generateIdentifiers(
				Mockito.any(IdentifierSource.class), 
				Mockito.any(Integer.class), 
				Mockito.any(String.class))).toReturn(Arrays.asList("1", "2", "3"));
		
		
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();

		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		
		controller.exportIdentifiers(null, mockRequest, mockResponse, generator, 3, "Mirebalais", null, null);

		Assert.assertEquals("{\"identifiers\":[\"1\",\"2\",\"3\"]}", mockResponse.getContentAsString());
	}

    @Test
    public void importIdentifiers_shouldAcceptJson() throws Exception {
        Mockito.doNothing().when(iss).addIdentifiersToPool(Mockito.any(IdentifierPool.class), (List<String>) Mockito.anyCollectionOf(String.class));

        IdentifierPool identifierPool = new IdentifierPool();
        String identifiers = "{\"identifiers\":[\"1\",\"2\",\"3\"]}";
        InputStream inputStream = new ByteArrayInputStream(identifiers.getBytes());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("inputFile", inputStream);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        controller.addIdentifiersFromFile(null, mockRequest, mockResponse, identifierPool, mockMultipartFile);
        String response = (String) mockRequest.getSession().getAttribute(WebConstants.OPENMRS_MSG_ATTR);
        Assert.assertEquals(response, "Success: Identifiers successfully uploaded.");
    }
}
