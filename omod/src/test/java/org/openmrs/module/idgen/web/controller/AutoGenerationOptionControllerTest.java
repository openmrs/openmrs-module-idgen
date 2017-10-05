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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.record.formula.functions.True;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.idgen.web.RestTestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Tests CRUD operations for {@link AutoGenerationOption}s via web service calls
 */
public class AutoGenerationOptionControllerTest extends MainResourceControllerTest {
  
  private IdentifierSourceService service;

  public PatientIdentifierType ptid;

  @Before
	public void before() throws Exception {
    executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    this.service = Context.getService(IdentifierSourceService.class);
  }

  @Override
	public String getURI() {
		return "idgen/autogenerationoption";
  }
  
  @Override
	public String getUuid() {
		return "1";
  }

  @Override
	public long getAllCount() {
		return service.getAutoGenerationOptions(ptid).size();
	}
  
  
  @Test
  public void shouldGetAnAutoGenerationOptionById() throws Exception {
    MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
    SimpleObject result = deserialize(handle(req));
    
    AutoGenerationOption autoGenerationOption = service.getAutoGenerationOption(Integer.parseInt(getUuid()));
    assertEquals(autoGenerationOption.getId(), PropertyUtils.getProperty(result, "id"));
    assertEquals(autoGenerationOption.getIdentifierType(), PropertyUtils.getProperty(result, "identifierType"));
    assertEquals(autoGenerationOption.getLocation(), PropertyUtils.getProperty(result, "location"));
    assertEquals(autoGenerationOption.getSource(), PropertyUtils.getProperty(result, "source"));
    assertEquals(autoGenerationOption.isAutomaticGenerationEnabled(), PropertyUtils.getProperty(result, "isAutomaticGenerationEnabled"));
    assertEquals(autoGenerationOption.isManualEntryEnabled(), PropertyUtils.getProperty(result, "isManualEntryEnabled"));
  }

  @Test
  public void shouldGetAnAutoGenerationOptionByPatientIdentifierType() throws Exception {
    MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + ptid);
    SimpleObject result = deserialize(handle(req));
    
    AutoGenerationOption autoGenerationOption = service.getAutoGenerationOption(ptid);
    assertEquals(autoGenerationOption.getId(), PropertyUtils.getProperty(result, "id"));
    assertEquals(autoGenerationOption.getIdentifierType(), PropertyUtils.getProperty(result, "identifierType"));
    assertEquals(autoGenerationOption.getLocation(), PropertyUtils.getProperty(result, "location"));
    assertEquals(autoGenerationOption.getSource(), PropertyUtils.getProperty(result, "source"));
    assertEquals(autoGenerationOption.isAutomaticGenerationEnabled(), PropertyUtils.getProperty(result, "isAutomaticGenerationEnabled"));
    assertEquals(autoGenerationOption.isManualEntryEnabled(), PropertyUtils.getProperty(result, "isManualEntryEnabled"));
  }

  @Test
  public void shouldCreateNewAutoGenerationOption() throws Exception {
    long originalCount = getAllCount();

    SimpleObject autoGenerationOption = new SimpleObject();
    autoGenerationOption.add("identifierType", "idgen");
    autoGenerationOption.add("location", "National Hospital");
    autoGenerationOption.add("source", "New source");
    autoGenerationOption.add("isAutomaticGenerationEnabled", true);
    autoGenerationOption.add("isManualEntryEnabled", true);

    String json = new ObjectMapper().writeValueAsString(autoGenerationOption);

    MockHttpServletRequest req = request(RequestMethod.POST, getURI());
    req.setContent(json.getBytes());
    
    SimpleObject newAutoGenerationOption = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(newAutoGenerationOption, "id"));
		assertEquals(originalCount + 1, getAllCount());
  }

  @Test
	public void shouldEditAnAutoGenerationOption() throws Exception {
    MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
    SimpleObject result = deserialize(handle(req));
    
    final Object location = PropertyUtils.getProperty(result, "location");
    final Object source = PropertyUtils.getProperty(result, "source");

    final String newLocation = "Updated Location";
    final String newSource = "Updated Source";
    
		SimpleObject newValues = new SimpleObject();
    newValues.add("location", newLocation);
    newValues.add("source", newSource);

    AutoGenerationOption autoGenerationOption = service.getAutoGenerationOption(Integer.parseInt(getUuid()));
		
		String json = new ObjectMapper().writeValueAsString(autoGenerationOption);
		
		MockHttpServletRequest newReq = request(RequestMethod.POST, getURI() + "/" + getUuid());
		newReq.setContent(json.getBytes());
		handle(newReq);
    assertEquals(newLocation, service.getAutoGenerationOption(Integer.parseInt(getUuid())).getLocation());
    assertEquals(newSource, service.getAutoGenerationOption(Integer.parseInt(getUuid())).getSource());
    assertNotEquals(location, service.getAutoGenerationOption(Integer.parseInt(getUuid())).getLocation());
    assertNotEquals(source, service.getAutoGenerationOption(Integer.parseInt(getUuid())).getSource());
  }
  
  @Test
	public void shouldPurgeAnAutoGenerationOption() throws Exception {
    assertNotNull(service.getAutoGenerationOption(Integer.parseInt(getUuid())));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "");
		handle(req);
    assertNull(service.getAutoGenerationOption(Integer.parseInt(getUuid())));
	}
}
