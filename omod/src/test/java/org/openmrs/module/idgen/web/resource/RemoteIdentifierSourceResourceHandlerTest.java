package org.openmrs.module.idgen.web.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.rest.resource.IdentifierSourceResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.test.BaseContextMockTest;

/**
 * Validates methods in RemoteIdentifierSourceResourceHandler
 */
public class RemoteIdentifierSourceResourceHandlerTest extends BaseContextMockTest{

	public static final String SAMPLE_ID = "0d43034f-8f33-4a02-a33b-0bb42cd0a357";
	public static final String URL = "https://www.journaldev.com/";
	public static final String NAME = "ugandaemr";
	public static final String PASSWORD = "Uganda123EmR";
	
	@Mock
	RemoteIdentifierSource remoteid;
	@Mock
	DelegatingResourceDescription representationDescription;
	@Mock
	Representation rep;
	@Mock
	IdentifierSourceResource ide;
	@InjectMocks
	RemoteIdentifierSourceResourceHandler handler;
	
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void getResourceVersion_shouldReturnResourceVersion() {
		String resourceVersion="2.2";
		when(ide.getResourceVersion()).thenReturn(resourceVersion);
		assertThat(handler.getResourceVersion(), is(new RemoteIdentifierSourceResourceHandler().getResourceVersion()));
	}
	@Test
	public void newDelegate_shouldReturnRemoteIdentifierSourceDelegateInstance()throws Exception  {
		when(handler.newDelegate()).thenReturn(new RemoteIdentifierSourceResourceHandler().newDelegate());
		assertThat(handler.newDelegate(), is(not(null)));
	}
	@Test
	public void save_shouldSaveRemoteIdentifierSource()throws Exception  {
		remoteid.addReservedIdentifier(SAMPLE_ID);
		remoteid.setId(0);
		remoteid.setUrl("https://www.journaldev.com/");
		remoteid.setName("UgandaEMR");
		remoteid.setPassword("Uganda123EmR");
		handler.save(remoteid);
	
		when(handler.save(remoteid)).thenReturn(remoteid);
		assertThat(remoteid.getUrl(), is(URL));
		assertThat(remoteid.getPassword(), is(PASSWORD));
		assertThat(remoteid.getName(), is(NAME));
	}
	@Test
	public void getRepresentationDescription_shouldReturnRepresentationDescription_GivenType()throws Exception  {
		if(rep.DEFAULT != null) {
			representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("identifierType", Representation.REF);
            representationDescription.addSelfLink();
			
        }
        else if(rep.FULL != null) {
        	representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("identifierType", Representation.FULL);
            representationDescription.addProperty("password");
            representationDescription.addProperty("url");
            representationDescription.addSelfLink();
			
        }
        else if(rep.REF != null) {
        	representationDescription.addProperty("uuid");
	         representationDescription.addProperty("name");
	         representationDescription.addSelfLink();
			
        }
        	
        
		when(handler.getRepresentationDescription(rep.DEFAULT)).thenReturn(representationDescription);
		when(handler.getRepresentationDescription(rep.FULL)).thenReturn(representationDescription);
		when(handler.getRepresentationDescription(rep.REF)).thenReturn(representationDescription);
		assertThat(representationDescription, is(handler.getRepresentationDescription(rep.DEFAULT)));
		assertThat(representationDescription, is(handler.getRepresentationDescription(rep.FULL)));
		assertThat(representationDescription, is(handler.getRepresentationDescription(rep.REF)));
	}
    @Test
	public void getCreatableProperties_shouldReturnCreatableProperties()throws Exception  {
		representationDescription.addProperty("uuid");
		representationDescription.addProperty("name");
        representationDescription.addProperty("password");
        representationDescription.addProperty("url");	
		when(handler.getCreatableProperties()).thenReturn(representationDescription);
		
	    assertThat(representationDescription, is(handler.getCreatableProperties()));
	}
    @Test
	public void getUpdatableProperties_shouldReturnUpdatableProperties()throws Exception  {
		representationDescription.addProperty("name");
        representationDescription.addProperty("password");
        representationDescription.addProperty("url");
        when(handler.getUpdatableProperties()).thenReturn(representationDescription);
        assertThat(representationDescription, is(handler.getUpdatableProperties()));
	}
    @Test
	public void getTypeName_shouldReturnTypeName()throws Exception  {
    	String typeName="Remote Identifier Source";
		when(handler.getTypeName()).thenReturn(typeName);
		assertThat(handler.getTypeName(), is(new IdentifierSourceResource().REMOTE_IDENTIFIER_SOURCE));
	}
	public void getAllByType_shouldReturnAllTypes()throws Exception  {}
}
