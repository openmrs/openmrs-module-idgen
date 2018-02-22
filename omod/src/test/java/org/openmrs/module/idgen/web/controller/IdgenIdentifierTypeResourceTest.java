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

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.web.IdgenWsConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

public class IdgenIdentifierTypeResourceTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private AnnotationMethodHandlerAdapter handlerAdapter;
	
	@Autowired
	@Qualifier("org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping#0")
	private DefaultAnnotationHandlerMapping handlerMapping;
	
	@Before
	public void setup() {
		// setting a primary and an extra identifiers with non-unicode chars in their names
		AdministrationService as = Context.getAdministrationService();
		PatientService ps = Context.getPatientService();
		{
			PatientIdentifierType pit = ps.getPatientIdentifierType(1);
			pit.setName("លេខសម្គាល់ OpenMRS");
			ps.savePatientIdentifierType(pit);
			as.saveGlobalProperty(new GlobalProperty(IdgenWsConstants.GP_PRIMARY_IDTYPE, pit.getUuid()));
		}
		{
			PatientIdentifierType pit = ps.getPatientIdentifierType(2);
			pit.setName("លេខសម្គាល់ old");
			ps.savePatientIdentifierType(pit);
			as.saveGlobalProperty(new GlobalProperty(IdgenWsConstants.GP_EXTRA_IDTYPES, pit.getUuid()));
		}
	}
	
	@Test
	public void getPrimaryAndExtraIdentifierTypes_shouldEncodePITNamesAndDescriptionsInUTF8() throws Exception {
	    // Setup
	    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/" + IdgenWsConstants.PATH_IDGEN_IDTYPE);
	    Object handler = handlerMapping.getHandler(request).getHandler();
	    MockHttpServletResponse response = new MockHttpServletResponse();

	    // Replay
	    handlerAdapter.handle(request, response, handler);
	    	    
	    // Verify
	    Assert.assertEquals(IdgenIdentifierTypeController.contentType, response.getContentType());
	    Assert.assertEquals(IdgenIdentifierTypeController.encoding, response.getCharacterEncoding());
	    String json = response.getContentAsString();

	    HashMap<String, String>[] identifierTypes = new ObjectMapper().readValue(json, HashMap[].class);
	    Assert.assertEquals(2, identifierTypes.length);
	    Assert.assertEquals("លេខសម្គាល់ OpenMRS", identifierTypes[0].get("name"));
	    Assert.assertEquals("លេខសម្គាល់ old", identifierTypes[1].get("name"));
	}
}