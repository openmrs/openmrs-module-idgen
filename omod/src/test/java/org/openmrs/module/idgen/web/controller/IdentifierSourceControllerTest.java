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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
