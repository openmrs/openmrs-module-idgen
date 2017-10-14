package org.openmrs.module.idgen.rest.controller;

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests CRUD operations for {@link AutoGenerationOption}s via web service calls
 */
public class AutoGenerationOptionControllerTest extends MainResourceControllerTest {
	
	@Autowired
	private IdentifierSourceService identifierSourceService;
	
	public static final String NEW_LOCATION = "9356400c-a5a2-4532-8f2b-2361b3446eb8";
	
	public static final String NEW_SOURCE = "0d47284f-9e9b-4a81-a88b-8bb42bc0a901";
	
	public static final String LOCATION = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
	
	public static final String SOURCE = "0d47284f-9e9b-4a81-a88b-8bb42bc0a902";
	
	public static final String AUTO_GENERATION_OPTION_UUID = "2";
	
	public static final String IDENTIFIER_TYPE_UUID = "2f470aa8-1d73-43b7-81b5-01f0c0dfa53c";
	
	public static final String UNKNOWN_LOCATION = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
	
	@Before
	public void before() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
	}
	
	@Override
	public String getURI() {
		return "idgen/autogenerationoption";
	}
	
	@Override
	public String getUuid() {
		return AUTO_GENERATION_OPTION_UUID;
	}
	
	@Override
	public long getAllCount() {
		Integer allAutoGenerationOptions = 0;
		for (PatientIdentifierType patientIdentifierType : Context.getPatientService().getAllPatientIdentifierTypes()) {
			allAutoGenerationOptions += identifierSourceService.getAutoGenerationOptions(patientIdentifierType).size();
		}
		return allAutoGenerationOptions;
	}
	
	@Test()
	public void shouldCreateNewAutoGenerationOption() throws Exception {
		long originalCount = getAllCount();
		SimpleObject autoGenerationOption = new SimpleObject();
		autoGenerationOption.add("identifierType", IDENTIFIER_TYPE_UUID);
		autoGenerationOption.add("location", LOCATION);
		autoGenerationOption.add("source", SOURCE);
		autoGenerationOption.add("automaticGenerationEnabled", true);
		autoGenerationOption.add("manualEntryEnabled", true);
		String json = new ObjectMapper().writeValueAsString(autoGenerationOption);
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		SimpleObject newAutoGenerationOption = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(newAutoGenerationOption, "uuid"));
		assertEquals(originalCount + 1, getAllCount());
		assertEquals(SOURCE, PropertyUtils.getProperty(newAutoGenerationOption, "source"));
		assertEquals(LOCATION, PropertyUtils.getProperty(newAutoGenerationOption, "location"));
		assertEquals(IDENTIFIER_TYPE_UUID, PropertyUtils.getProperty(newAutoGenerationOption, "identifierType"));
		assertEquals(true,
		    PropertyUtils.getProperty(newAutoGenerationOption, "automaticGenerationEnabled"));
		assertEquals(true, PropertyUtils.getProperty(newAutoGenerationOption, "manualEntryEnabled"));
	}
	
	@Test()
	public void shouldCreateNewAutoGenerationOptionWithoutLocation() throws Exception {
		long originalCount = getAllCount();
		SimpleObject autoGenerationOption = new SimpleObject();
		autoGenerationOption.add("identifierType", IDENTIFIER_TYPE_UUID);
		autoGenerationOption.add("source", SOURCE);
		autoGenerationOption.add("automaticGenerationEnabled", true);
		autoGenerationOption.add("manualEntryEnabled", true);
		String json = new ObjectMapper().writeValueAsString(autoGenerationOption);
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		SimpleObject newAutoGenerationOption = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(newAutoGenerationOption, "uuid"));
		assertEquals(originalCount + 1, getAllCount());
		assertEquals(SOURCE, PropertyUtils.getProperty(newAutoGenerationOption, "source"));
		assertEquals(UNKNOWN_LOCATION, PropertyUtils.getProperty(newAutoGenerationOption, "location"));
		assertEquals(IDENTIFIER_TYPE_UUID, PropertyUtils.getProperty(newAutoGenerationOption, "identifierType"));
	}
	
	@Test(expected = org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException.class)
	public void shouldNotCreateNewAutoGenerationOptionWithNullParameters() throws Exception {
		SimpleObject autoGenerationOption = new SimpleObject();
		autoGenerationOption.add("identifierType", null);
		autoGenerationOption.add("location", null);
		autoGenerationOption.add("source", null);
		autoGenerationOption.add("automaticGenerationEnabled", null);
		autoGenerationOption.add("manualEntryEnabled", null);
		String json = new ObjectMapper().writeValueAsString(autoGenerationOption);
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		handle(req);
	}
	
	@Test
	public void shouldEditAnAutoGenerationOption() throws Exception {
		assertEquals(LOCATION,
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).getLocation().getUuid());
		assertEquals(SOURCE,
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).getSource().getUuid());
		assertTrue(
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isAutomaticGenerationEnabled());
		assertTrue(identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isManualEntryEnabled());
		SimpleObject autoGenerationOption = new SimpleObject();
		autoGenerationOption.add("location", NEW_LOCATION);
		autoGenerationOption.add("source", NEW_SOURCE);
		autoGenerationOption.add("manualEntryEnabled", false);
		autoGenerationOption.add("automaticGenerationEnabled", false);
		String json = new ObjectMapper().writeValueAsString(autoGenerationOption);
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		assertEquals(NEW_LOCATION,
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).getLocation().getUuid());
		assertEquals(NEW_SOURCE,
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).getSource().getUuid());
		assertFalse(
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isAutomaticGenerationEnabled());
		assertFalse(identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isManualEntryEnabled());
	}
	
	@Test(expected = org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException.class)
	public void shouldNotEditAnAutoGenerationOptionProvidedWithAllNullParams() throws Exception {
		SimpleObject autoGenerationOption = new SimpleObject();
		autoGenerationOption.add("location", null);
		autoGenerationOption.add("source", null);
		autoGenerationOption.add("manualEntryEnabled", null);
		autoGenerationOption.add("automaticGenerationEnabled", null);
		String json = new ObjectMapper().writeValueAsString(autoGenerationOption);
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
	}
	
	@Test
	public void shouldEditAnAutoGenerationOptionByAnyNumberOFArguments() throws Exception {
		assertTrue(
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isAutomaticGenerationEnabled());
		assertTrue(identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isManualEntryEnabled());
		SimpleObject autoGenerationOption = new SimpleObject();
		autoGenerationOption.add("manualEntryEnabled", false);
		autoGenerationOption.add("automaticGenerationEnabled", false);
		String json = new ObjectMapper().writeValueAsString(autoGenerationOption);
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		assertFalse(
		    identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isAutomaticGenerationEnabled());
		assertFalse(identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())).isManualEntryEnabled());
	}
	
	@Test
	public void shouldPurgeAnAutoGenerationOption() throws Exception {
		assertNotNull(identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "");
		handle(req);
		assertNull(identifierSourceService.getAutoGenerationOption(Integer.parseInt(getUuid())));
	}
}
