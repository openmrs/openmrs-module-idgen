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
package org.openmrs.module.idgen.rest.resource.handler;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class IdentifierPoolResourceHandlerTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	IdentifierSourceService service;
	
	@Autowired
	private IdentifierPoolResourceHandler handler;
	
	private IdentifierPool pool;
	
	@Before
	public void setup() {
		pool = new IdentifierPool();
	}
	
	@Test
	public void newDelegate_shouldReturnIdentifierPoolDelegateInstance() throws Exception {
		assertTrue(handler.newDelegate().getClass().isAssignableFrom(IdentifierPool.class));
	}
	
	@Test
	public void save_shouldSaveIdentifierToPool() throws Exception {
		pool.setBatchSize(100);
		pool.setSequential(true);
		pool.setRefillWithScheduledTask(true);
		pool.setName("Sample Pool");
		pool.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		int savedIdentifierSourceId = handler.save(pool).getId();
		IdentifierSource src = service.getIdentifierSource(savedIdentifierSourceId);
		assertTrue(src.getClass().isAssignableFrom(IdentifierPool.class));
		assertEquals("Sample Pool", src.getName());

	}
	
	@Test
	public void getRepresentationDescription_shouldReturnRepresentationDescriptionFromRepresentation() throws Exception  {
		Representation defaultRep = new DefaultRepresentation();
		Map<String, Property> properties = handler.getRepresentationDescription(defaultRep).getProperties();
		assertNotNull(properties.get("uuid"));
		assertNotNull(properties.get("name"));
		assertNotNull(properties.get("display"));
		assertNull(properties.get("url"));
		assertEquals(DefaultRepresentation.class, properties.get("identifierType").getRep().getClass());
		
		Representation fullRep = new FullRepresentation();
		properties = handler.getRepresentationDescription(fullRep).getProperties();
		assertNotNull(properties.get("uuid"));
		assertNotNull(properties.get("name"));
		assertNotNull(properties.get("display"));
		assertNotNull(properties.get("url"));
		assertNotNull(properties.get("user"));
		assertNotNull(properties.get("password"));
		assertEquals(FullRepresentation.class, properties.get("identifierType").getRep().getClass());
		
		Representation refRep = new RefRepresentation();
		properties = handler.getRepresentationDescription(refRep).getProperties();
		assertNotNull(properties.get("uuid"));
		assertNotNull(properties.get("display"));
		assertNull(properties.get("url"));
		assertNull(properties.get("user"));
		assertNull(properties.get("password"));
		assertEquals(RefRepresentation.class, properties.get("identifierType").getRep().getClass());
	}
	
	@Test
	public void getCreatableProperties_shouldReturnCreatableProperties() throws Exception  {
		Map<String, Property> properties = handler.getCreatableProperties().getProperties();
		assertThat(properties.size(), is(7));
		assertTrue(properties.keySet().contains("name"));
		assertTrue(properties.keySet().contains("identifierType"));
		assertTrue(properties.keySet().contains("sequential"));
		assertTrue(properties.keySet().contains("refillWithScheduledTask"));
		assertTrue(properties.keySet().contains("source"));
		assertTrue(properties.keySet().contains("batchSize"));
		assertTrue(properties.keySet().contains("minPoolSize"));
	}
	
	@Test
	public void getUpdatableProperties_shouldReturnUpdatableProperties() throws Exception  {
		Map<String, Property> properties = handler.getUpdatableProperties().getProperties();
		assertThat(properties.size(), is(5));
		assertTrue(properties.keySet().contains("sequential"));
		assertTrue(properties.keySet().contains("refillWithScheduledTask"));
		assertTrue(properties.keySet().contains("source"));
		assertTrue(properties.keySet().contains("batchSize"));
		assertTrue(properties.keySet().contains("minPoolSize"));
		
	}
	
	@Test 
	public void getTypeName_shouldReturnTypeName() throws Exception  {
		assertEquals("Pool Identifier", handler.getTypeName());
	}
	
}
