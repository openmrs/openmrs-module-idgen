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

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

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
}
