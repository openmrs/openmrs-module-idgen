/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.Identifier;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;

@org.openmrs.module.webservices.rest.web.annotation.SubResource(parent = IdentifierSourceResource.class, path =
        "identifier", supportedClass = Identifier.class, supportedOpenmrsVersions = {"1.8.* - 9.9.*"})
public class IdentifierResource extends DelegatingSubResource<Identifier, IdentifierSource, IdentifierSourceResource> {

    public static final String IDENTIFIER_KEY = "identifier";

    @Override
    public String getUri(Object instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException {
    	if (post == null) {
    		return null;
    	}
        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
        String identifier = service.generateIdentifier(service.getIdentifierSourceByUuid(parentUniqueId),
                post.containsKey("comment") ? post.get("comment") : "");
        SimpleObject response = new SimpleObject();
        response.add(IDENTIFIER_KEY, identifier);
        return response;
    }

    @Override
    public Model getGETModel(Representation rep) {
    	return null;
    }
    
    @Override
    public Model getCREATEModel(Representation rep) {
    	return new ModelImpl()
				.property("comment", new StringProperty());
    }
    
    @Override
    public Model getUPDATEModel(Representation rep) {
    	return null;
    }
    
    @Override
    public Object retrieve(String parentUniqueId, String uniqueId, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public Object update(String parentUniqueId, String uniqueId, SimpleObject propertiesToUpdate, RequestContext context) throws
            ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public void delete(String parentUniqueId, String uniqueId, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public void purge(String parentUniqueId, String uniqueId, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public SimpleObject getAll(String parentUniqueId, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public void put(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

	@Override
	public Identifier newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public Identifier save(Identifier delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public IdentifierSource getParent(Identifier instance) {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public void setParent(Identifier instance, IdentifierSource parent) {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public PageableResult doGetAll(IdentifierSource parent, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public Identifier getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	protected void delete(Identifier delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public void purge(Identifier delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();		
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
    public DelegatingResourceDescription getCreatableProperties() {
    	 DelegatingResourceDescription description = new DelegatingResourceDescription();
    	 description.addProperty("comment"); 
         return description;
    }
}
