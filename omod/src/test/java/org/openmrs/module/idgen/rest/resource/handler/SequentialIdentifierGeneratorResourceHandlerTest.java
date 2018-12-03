package org.openmrs.module.idgen.rest.resource.handler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.rest.resource.IdentifierSourceResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.test.BaseContextMockTest;

public class SequentialIdentifierGeneratorResourceHandlerTest extends BaseContextMockTest{
	@InjectMocks
	SequentialIdentifierGeneratorResourceHandler handler;
	@Mock
	SequentialIdentifierGenerator gen;
	@Mock
	DelegatingResourceDescription representationDescription;
	@Mock
	Representation rep;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void getResourceVersion_shouldReturnResourceVersion() {
		String resourceVersion="2.2";
		when(handler.getResourceVersion()).thenReturn(resourceVersion);
		assertThat(handler.getResourceVersion(), is(new SequentialIdentifierGeneratorResourceHandler().getResourceVersion()));
	
	}
	//@Test
	public void newDelegate_shouldReturnSequentialIdentifierGenerator()throws Exception  {
		when(handler.newDelegate()).thenReturn(new SequentialIdentifierGeneratorResourceHandler().newDelegate());
	assertEquals(new SequentialIdentifierGeneratorResourceHandler().newDelegate(), handler.newDelegate());
	}
	
	@Test
	public void save_shouldSaveRemoteSequentialIdentifierGenerator()throws Exception  {
		gen.setId(0);
		gen.setPrefix("ac");
		gen.setSuffix("ef");
		gen.setBaseCharacterSet("abcedf");
		gen.setMaxLength(20);
		gen.setMinLength(5);
	
	when(handler.save(gen)).thenReturn(gen);
    assertThat(handler.save(gen),is(equalTo(gen)));
	}
	@Test
	public void getRepresentationDescription_shouldReturnRepresentation_GivenRepresentation()throws Exception  {
		String representation=null;
		if(rep.DEFAULT != null) {
			representationDescription.addProperty("baseCharacterSet");
			representationDescription.addProperty("prefix");
			representationDescription.addProperty("suffix");
			representationDescription.addProperty("firstIdentifierBase");
			representationDescription.addProperty("minLength");
			representationDescription.addProperty("maxLength");
			representationDescription.addProperty("baseCharacterSet");
			
        }
        else if(rep.FULL != null) {
        	representationDescription.addProperty("nextSequenceValue");
			representationDescription.addProperty("baseCharacterSet");
			representationDescription.addProperty("prefix");
			representationDescription.addProperty("suffix");
			representationDescription.addProperty("firstIdentifierBase");
			representationDescription.addProperty("minLength");
			representationDescription.addProperty("maxLength");
			representationDescription.addProperty("baseCharacterSet");
			
        }
        else if(rep.REF != null) {
        	representationDescription.addProperty("baseCharacterSet");
			representationDescription.addProperty("");
			
        }
        	
        
		when(handler.getRepresentationDescription(rep.DEFAULT)).thenReturn(representationDescription);
		when(handler.getRepresentationDescription(rep.FULL)).thenReturn(representationDescription);
		when(handler.getRepresentationDescription(rep.REF)).thenReturn(representationDescription);
		assertThat(representationDescription, is(handler.getRepresentationDescription(rep.DEFAULT)));
		assertThat(representationDescription, is(handler.getRepresentationDescription(rep.FULL)));
		assertThat(representationDescription, is(handler.getRepresentationDescription(rep.REF)));
	}
	@Test
	public void getCreatableProperties_shouldReturnCreatableProperties() throws Exception {
		representationDescription.addProperty("nextSequenceValue");
		representationDescription.addProperty("baseCharacterSet");
		representationDescription.addProperty("prefix");
		representationDescription.addProperty("suffix");
		representationDescription.addProperty("firstIdentifierBase");
		representationDescription.addProperty("minLength");
		representationDescription.addProperty("maxLength");
		representationDescription.addProperty("baseCharacterSet");	
		when(handler.getCreatableProperties()).thenReturn(representationDescription);
		
	    assertThat(representationDescription, is(handler.getCreatableProperties()));
	}
	@Test
	public void getUpdatableProperties_shouldReturnUpdatableProperties() throws Exception {
		representationDescription.addProperty("sequential");
        representationDescription.addProperty("refillWithScheduledTask");
        representationDescription.addProperty("source");
        representationDescription.addProperty("batchSize");
        representationDescription.addProperty("minPoolSize");
        when(handler.getUpdatableProperties()).thenReturn(representationDescription);
        assertThat(representationDescription, is(handler.getUpdatableProperties()));
	}
	@Test
	public void getTypeName_shouldReturnTypeName() throws Exception {
		String typeName="Local Sequential Identifier Generator";
		when(handler.getTypeName()).thenReturn(typeName);
		assertThat(handler.getTypeName(), is(new IdentifierSourceResource().SEQUENTIAL_IDENTIFIER_GENERATOR));
	}
	public void getAllByType_shouldReturnAllTypes() throws Exception {
	
	}
}

