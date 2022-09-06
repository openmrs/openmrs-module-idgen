
package org.openmrs.module.idgen.rest.resource.handler;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
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
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@SubClassHandler(supportedClass = RemoteIdentifierSource.class, supportedOpenmrsVersions = {"1.8.* - 9.9.*"})
public class RemoteIdentifierSourceResourceHandler extends BaseDelegatingSubclassHandler<IdentifierSource, RemoteIdentifierSource>
implements DelegatingSubclassHandler<IdentifierSource, RemoteIdentifierSource> {

	@Autowired
	IdentifierSourceService service;
	
	@Override
	public String getResourceVersion() {
		return "2.2";
	}
	
	@Override
	public RemoteIdentifierSource newDelegate() {
		return new RemoteIdentifierSource();
	}
	
	@Override
	public RemoteIdentifierSource save(RemoteIdentifierSource delegate) {
		service.saveIdentifierSource(delegate);
		return delegate;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		if (representation instanceof DefaultRepresentation) {
			representationDescription.addProperty("uuid");
            		representationDescription.addProperty("name");
            		representationDescription.addProperty("display");
            		representationDescription.addProperty("identifierType", Representation.DEFAULT);
            		representationDescription.addSelfLink();
			return representationDescription;
		}
		if (representation instanceof FullRepresentation) {
			representationDescription.addProperty("uuid");
            		representationDescription.addProperty("name");
            		representationDescription.addProperty("display");
            		representationDescription.addProperty("identifierType", Representation.FULL);
            		representationDescription.addProperty("password");
            		representationDescription.addProperty("user");
            		representationDescription.addProperty("url");
            		representationDescription.addSelfLink();
			return representationDescription;
		}
		if (representation instanceof RefRepresentation) {
			representationDescription.addProperty("uuid");
	         	representationDescription.addProperty("name");
	         	representationDescription.addProperty("display");
	         	representationDescription.addProperty("identifierType", Representation.REF);
	         	representationDescription.addSelfLink();
	         	return representationDescription;
		}
		return null;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		representationDescription.addProperty("uuid");
		representationDescription.addProperty("name");
        	representationDescription.addProperty("password");
        	representationDescription.addProperty("url");
		return representationDescription;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
        	representationDescription.addProperty("name");
        	representationDescription.addProperty("password");
        	representationDescription.addProperty("url");
		return representationDescription;
	}

	@Override
	public String getTypeName() {
		return IdentifierSourceResource.REMOTE_IDENTIFIER_SOURCE;
	}

	@Override
	public PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException {
		List<IdentifierSource> identifierSources = Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false);
		if (identifierSources == null) {
			return null;
		}
		List<RemoteIdentifierSource> remoteIdentifierSources = new ArrayList<RemoteIdentifierSource>();
		for (IdentifierSource src : identifierSources) {
			if (src instanceof RemoteIdentifierSource) {
				remoteIdentifierSources.add((RemoteIdentifierSource) src);
			}
		}
		return new NeedsPaging<RemoteIdentifierSource>(remoteIdentifierSources, context);
	}

	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl()
						.property("uuid", new StringProperty())
						.property("name", new StringProperty())
						.property("display", new StringProperty());	 
		
		if (rep instanceof FullRepresentation) {
			model
				.property("password", new StringProperty())
				.property("url", new StringProperty())
				.property("user", new RefProperty("#/definitions/UserGet"));
		}
		
		return model;
	}

	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
					.property("uuid", new StringProperty())
					.property("name", new StringProperty())
					.property("url", new StringProperty())
					.property("password", new StringProperty());	 
	}
	
	@PropertyGetter("display")
    public String getDisplayString(RemoteIdentifierSource identifierSource) {
        return identifierSource.getIdentifierType() + " - " 
                + identifierSource.getName() + " - "
                + identifierSource.getClass().getName();
    }
	
}
