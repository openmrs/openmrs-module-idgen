package org.openmrs.module.idgen.rest.resource.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class SequentialIdentifierGeneratorResourceHandlerTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	private IdentifierSourceService service;
	
	private SequentialIdentifierGeneratorResourceHandler handler;
	
	@Before
	public void setup() {
		handler = new SequentialIdentifierGeneratorResourceHandler();
	}
	
	@Test
	public void newDelegate_shouldReturnSequentialIdentifierGenerator() throws Exception  {
		assertTrue(handler.newDelegate().getClass().isAssignableFrom(SequentialIdentifierGenerator.class));
	}
	
	@Test
	public void save_shouldSaveRemoteSequentialIdentifierGenerator() throws Exception  {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
		String name = "Some Name";
		String baseCharacterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String firstIdentifierBase = "100";
		generator.setName(name);
		generator.setBaseCharacterSet(baseCharacterSet);
		generator.setFirstIdentifierBase(firstIdentifierBase);
		generator.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		handler.save(generator);
		int savedIdentifierSourceId = handler.save(generator).getId();
		SequentialIdentifierGenerator src = (SequentialIdentifierGenerator)service.getIdentifierSource(savedIdentifierSourceId);
		assertTrue(src.getClass().isAssignableFrom(SequentialIdentifierGenerator.class));
		assertEquals(name, src.getName());
		assertEquals(firstIdentifierBase, src.getFirstIdentifierBase());
		assertEquals(baseCharacterSet, src.getBaseCharacterSet());
		
	}
	
	@Test
	public void getRepresentationDescription_shouldReturnRepresentation_GivenRepresentation()throws Exception  {
		Representation defaultRep = new DefaultRepresentation();
		Map<String, Property> properties = handler.getRepresentationDescription(defaultRep).getProperties();
		assertNotNull(properties.get("baseCharacterSet"));
		assertNotNull(properties.get("prefix"));
		assertNotNull(properties.get("suffix"));
		assertNotNull(properties.get("firstIdentifierBase"));
		assertNotNull(properties.get("minLength"));
		assertNotNull(properties.get("maxLength"));
		assertNotNull(properties.get("baseCharacterSet"));
		assertEquals(DefaultRepresentation.class, properties.get("identifierType").getRep().getClass());
		
		Representation fullRep = new FullRepresentation();
		properties = handler.getRepresentationDescription(fullRep).getProperties();
		assertNotNull(properties.get("baseCharacterSet"));
		assertNotNull(properties.get("prefix"));
		assertNotNull(properties.get("suffix"));
		assertNotNull(properties.get("firstIdentifierBase"));
		assertNotNull(properties.get("minLength"));
		assertNotNull(properties.get("maxLength"));
		assertNotNull(properties.get("nextSequenceValue"));
		assertEquals(FullRepresentation.class, properties.get("identifierType").getRep().getClass());
		
		Representation refRep = new RefRepresentation();
		properties = handler.getRepresentationDescription(refRep).getProperties();
		assertNotNull(properties.get("baseCharacterSet"));
		assertEquals(RefRepresentation.class, properties.get("identifierType").getRep().getClass());
	}
	
	@Test
	public void getCreatableProperties_shouldReturnCreatableProperties() throws Exception {
		Map<String, Property> properties = handler.getCreatableProperties().getProperties();
		assertThat(properties.size(), is(7));
		assertTrue(properties.keySet().contains("baseCharacterSet"));
		assertTrue(properties.keySet().contains("prefix"));
		assertTrue(properties.keySet().contains("suffix"));
		assertTrue(properties.keySet().contains("firstIdentifierBase"));
		assertTrue(properties.keySet().contains("minLength"));
		assertTrue(properties.keySet().contains("maxLength"));
		assertTrue(properties.keySet().contains("nextSequenceValue"));

	}
	
	@Test
	public void getUpdatableProperties_shouldReturnUpdatableProperties() throws Exception {
		Map<String, Property> properties = handler.getCreatableProperties().getProperties();
		assertThat(properties.size(), is(7));
		assertTrue(properties.keySet().contains("baseCharacterSet"));
		assertTrue(properties.keySet().contains("prefix"));
		assertTrue(properties.keySet().contains("suffix"));
		assertTrue(properties.keySet().contains("firstIdentifierBase"));
		assertTrue(properties.keySet().contains("minLength"));
		assertTrue(properties.keySet().contains("maxLength"));
		assertTrue(properties.keySet().contains("nextSequenceValue"));

	}
	
	@Test
	public void getTypeName_shouldReturnTypeName() throws Exception {
		assertEquals("sequentialidentifiergenerator", handler.getTypeName());
	}
	
}

