package org.openmrs.module.idgen.web.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.rest.resource.IdentifierSourceResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.test.BaseContextMockTest;

/**
 * Validates methods in IdentifierPoolResourceHandler
 */
public class IdentifierPoolResourceHandlerTest extends BaseContextMockTest{
	public static final String SAMPLE_ID = "0d47284f-9e33-4a81-a88b-8bb42bc0a901";
	public static final String RESERVED_ID = "0d73784f-9e0b-4a81-a99b-7bC45bc0a922";
	@Mock
	IdentifierPool pool;
	@Mock
	Representation rep;
	@Mock
	DelegatingResourceDescription representationDescription;
	@Mock
	IdentifierSourceResource ide;
	@InjectMocks
	IdentifierPoolResourceHandler idpoolhandler;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void getResourceVersion_shouldReturnResourceVersion() {
		String resourceVersion="2.2";
		when(ide.getResourceVersion()).thenReturn(resourceVersion);
		assertThat(idpoolhandler.getResourceVersion(), is(new IdentifierPoolResourceHandler().getResourceVersion()));
	}
	//@Test
	public void newDelegate_shouldReturnIdentifierPoolDelegateInstance() throws Exception {
		Object obj = idpoolhandler.newDelegate();
		when(obj!=null).thenReturn(true);
	}
	@Test
	public void save_shouldSaveIdentifierToPool() throws Exception {
		pool.setId(0);
		pool.setSource(pool);
		pool.setBatchSize(100);
		pool.setSequential(true);
		pool.setRefillWithScheduledTask(true);
		when(idpoolhandler.save(pool)).thenReturn(pool);
	    assertThat(idpoolhandler.save(pool),is(equalTo(pool)));
		}
	@Test
	public void getRepresentationDescription_shouldReturnRepresentationDescription_GivenRepresentation()throws Exception  {
		if(rep.DEFAULT != null) {
		representationDescription.addProperty("uuid");
        representationDescription.addProperty("name");
        representationDescription.addProperty("identifierType", Representation.REF);
        }
        else if(rep.FULL != null) {
        	representationDescription.addProperty("uuid");
        representationDescription.addProperty("name");
        representationDescription.addProperty("identifierType", Representation.FULL);
        representationDescription.addProperty("password");
        representationDescription.addProperty("url");
        representationDescription.addProperty("user");
        }
        else if(rep.REF != null) {
        	 representationDescription.addProperty("uuid");
	         representationDescription.addProperty("name");
        }
        	
        
		when(idpoolhandler.getRepresentationDescription(rep.DEFAULT)).thenReturn(representationDescription);
		when(idpoolhandler.getRepresentationDescription(rep.FULL)).thenReturn(representationDescription);
		when(idpoolhandler.getRepresentationDescription(rep.REF)).thenReturn(representationDescription);
		assertThat(representationDescription, is(idpoolhandler.getRepresentationDescription(rep.DEFAULT)));
		assertThat(representationDescription, is(idpoolhandler.getRepresentationDescription(rep.FULL)));
		assertThat(representationDescription, is(idpoolhandler.getRepresentationDescription(rep.REF)));
	}
	@Test
	public void getCreatableProperties_shouldReturnCreatableProperties()throws Exception  {
		representationDescription.addProperty("name");
		representationDescription.addProperty("identifierType");
		representationDescription.addProperty("sequential");
		representationDescription.addProperty("refillWithScheduledTask");
		representationDescription.addProperty("source");
		representationDescription.addProperty("batchSize");
		representationDescription.addProperty("minPoolSize");	
		when(idpoolhandler.getCreatableProperties()).thenReturn(representationDescription);
		
	    assertThat(representationDescription, is(idpoolhandler.getCreatableProperties()));
	}
	@Test
	public void getUpdatableProperties_shouldReturnUpdatableProperties()throws Exception  {
		representationDescription.addProperty("sequential");
        representationDescription.addProperty("refillWithScheduledTask");
        representationDescription.addProperty("source");
        representationDescription.addProperty("batchSize");
        representationDescription.addProperty("minPoolSize");
        when(idpoolhandler.getUpdatableProperties()).thenReturn(representationDescription);
        assertThat(representationDescription, is(idpoolhandler.getUpdatableProperties()));
	}
	@Test 
	public void getTypeName_shouldReturnTypeName()throws Exception  {
		String typeName="Pool Identifier";
		when(idpoolhandler.getTypeName()).thenReturn(typeName);
		assertThat(idpoolhandler.getTypeName(), is(new IdentifierSourceResource().IDENTIFIER_POOL));
	}
	public void getAllByType_shouldReturnAllTypes()throws Exception  {}
}

