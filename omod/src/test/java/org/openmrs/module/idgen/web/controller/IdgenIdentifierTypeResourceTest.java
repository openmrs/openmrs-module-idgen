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

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
	
	@Ignore
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