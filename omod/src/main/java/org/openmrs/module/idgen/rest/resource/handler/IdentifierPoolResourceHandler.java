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
package  org.openmrs.module.idgen.rest.resource.handler;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.rest.resource.IdentifierSourceResource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubClassHandler(supportedClass = IdentifierPool.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*", "1.11.*","2.0.*","2.1.*","2.2.*"})
public class IdentifierPoolResourceHandler extends BaseDelegatingSubclassHandler<IdentifierSource, IdentifierPool>
implements DelegatingSubclassHandler<IdentifierSource, IdentifierPool> {

	@Autowired
	IdentifierSourceService service;
	
	@Override
	public String getResourceVersion() {
		return "2.2";
	}

	@Override
	public IdentifierPool newDelegate() {
		return new IdentifierPool();
	}
	
    	@Override
    	public IdentifierPool save(IdentifierPool delegate) {
          	service.saveIdentifierSource(delegate);
	      	return delegate;
    	}
    
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		if (representation instanceof DefaultRepresentation) {
			DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
			representationDescription.addProperty("uuid");
            		representationDescription.addProperty("name");
            		representationDescription.addProperty("identifierType", Representation.DEFAULT);
            		representationDescription.addSelfLink();
            		representationDescription.addProperty("display");
			return representationDescription;	
		}
		if (representation instanceof FullRepresentation) {
			DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
			representationDescription.addProperty("uuid");
		    	representationDescription.addProperty("name");
		    	representationDescription.addProperty("identifierType", Representation.FULL);
		    	representationDescription.addProperty("password");
		    	representationDescription.addProperty("url");
		    	representationDescription.addProperty("user");
		    	representationDescription.addProperty("display");
		    	representationDescription.addSelfLink();
			return representationDescription;
		}
		if (representation instanceof RefRepresentation) {
			DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
			representationDescription.addProperty("uuid");
	        	representationDescription.addProperty("display");
	        	representationDescription.addProperty("identifierType", Representation.REF);
	        	representationDescription.addSelfLink();
			return representationDescription;
		}
		
		return null;
	}
	
	@PropertyGetter("display")
    public String getDisplayString(IdentifierPool identifierSource) {
        return identifierSource.getIdentifierType() + " - " 
                + identifierSource.getName() + " - "
                + identifierSource.getClass().getName();
    }

	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		representationDescription.addProperty("name");
		representationDescription.addProperty("identifierType");
		representationDescription.addProperty("sequential");
		representationDescription.addProperty("refillWithScheduledTask");
		representationDescription.addProperty("source");
		representationDescription.addProperty("batchSize");
		representationDescription.addProperty("minPoolSize");
		return representationDescription;
	}

	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		representationDescription.addProperty("sequential");
        	representationDescription.addProperty("refillWithScheduledTask");
        	representationDescription.addProperty("source");
        	representationDescription.addProperty("batchSize");
        	representationDescription.addProperty("minPoolSize");
		return representationDescription;
	}
	
	@Override
	public String getTypeName() {
		return IdentifierSourceResource.IDENTIFIER_POOL;
	}

	@Override
	public PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException {
		List<IdentifierSource> identifierSources = Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false);
		if (identifierSources == null) {
			return null;
		}
		List<IdentifierPool> identifierPoolSources = new ArrayList<IdentifierPool>();
		for (IdentifierSource src : identifierSources) {
			if (src instanceof IdentifierPool) {
				identifierPoolSources.add((IdentifierPool) src);
			}
		}
		return new NeedsPaging<IdentifierPool>(identifierPoolSources, context);
	}

	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
				.property("uuid", new StringProperty())
				.property("name", new RefProperty("#/definitions/IdentifierPoolResourceGet"))
				.property("display", new StringProperty());	 
		}
		if (rep instanceof FullRepresentation) {
			model
				.property("password", new StringProperty())
				.property("url", new StringProperty())
				.property("user", new RefProperty("#/definitions/UserGet"));
		}
		if (rep instanceof RefRepresentation) {
			model
				.property("uuid", new StringProperty())
				.property("name", new RefProperty("#/definitions/IdentifierPoolResourceGetRef"))
				.property("display", new StringProperty());
		}
		return model;
	}

	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
					.property("name", new RefProperty("#/definitions/IdentifierPoolResourceCreate"))
					.property("identifierType", new RefProperty("#/definitions/IdentifierTypeGet"))
					.property("sequential", new BooleanProperty())
					.property("refillWithScheduledTask", new BooleanProperty())
					.property("source", new RefProperty("#/definitions/IdentifierSourceGet"))
					.property("batchSize", new IntegerProperty())
					.property("minPoolSize", new IntegerProperty());
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		return getCREATEModel(rep); //FIXME add Impl
	}
	
}
