package org.openmrs.module.idgen.rest.resource.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.Map;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoteIdentifierSourceResourceHandlerTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	private RemoteIdentifierSourceResourceHandler handler;
	
	@Autowired
	private IdentifierSourceService service;
	
	@Test
	public void newDelegate_shouldReturnRemoteIdentifierSourceDelegateInstance() throws Exception  {
		assertTrue(handler.newDelegate().getClass().isAssignableFrom(RemoteIdentifierSource.class));
	}
	
	@Test
	public void save_shouldSaveRemoteIdentifierSource() throws Exception  {
		String url = "https://www.journaldev.com/21866/mockito-mock-examples";
		String name = "Sample Name";
		String password = "Some password";
		RemoteIdentifierSource remoteid = new RemoteIdentifierSource();
		remoteid.setUrl(url);
		remoteid.setName(name);
		remoteid.setPassword(password);
		remoteid.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		handler.save(remoteid);
		int savedIdentifierSourceId = handler.save(remoteid).getId();
		RemoteIdentifierSource src = (RemoteIdentifierSource)service.getIdentifierSource(savedIdentifierSourceId);
		assertTrue(src.getClass().isAssignableFrom(RemoteIdentifierSource.class));
		assertEquals(name, src.getName());
		assertEquals(password, src.getPassword());
		assertEquals(url, src.getUrl());
		
	}
	
	@Test
	public void getRepresentationDescription_shouldReturnRepresentationDescription_GivenType()throws Exception  {
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
	public void getCreatableProperties_shouldReturnCreatableProperties()throws Exception  {
    	Map<String, Property> properties = handler.getCreatableProperties().getProperties();
		assertThat(properties.size(), is(4));
		assertTrue(properties.keySet().contains("name"));
		assertTrue(properties.keySet().contains("url"));
		assertTrue(properties.keySet().contains("password"));
		assertTrue(properties.keySet().contains("uuid"));

	}
    
    @Test
	public void getUpdatableProperties_shouldReturnUpdatableProperties()throws Exception  {
    	Map<String, Property> properties = handler.getUpdatableProperties().getProperties();
		assertThat(properties.size(), is(3));
		assertTrue(properties.keySet().contains("name"));
		assertTrue(properties.keySet().contains("password"));
		assertTrue(properties.keySet().contains("url"));
		
	}
    
    @Test
	public void getTypeName_shouldReturnTypeName()throws Exception  {
    	assertEquals("remoteidentifiersource", handler.getTypeName());
	}
    
}