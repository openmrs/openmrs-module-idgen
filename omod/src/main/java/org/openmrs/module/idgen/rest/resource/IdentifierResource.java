/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.SubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@org.openmrs.module.webservices.rest.web.annotation.SubResource(parent = IdentifierSourceResource.class, path =
        "identifier", supportedClass = String.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*", "1.11.*","1.12.*"," 2.0.*"," 2.1.*", "2.2.*"})
public class IdentifierResource implements SubResource {

    public static final String IDENTIFIER_KEY = "identifier";

    @Override
    public String getUri(Object instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException {
        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
        String identifier = service.generateIdentifier(service.getIdentifierSourceByUuid(parentUniqueId),
                post.containsKey("comment") ? String.valueOf(post.get("comment")) : "");
        SimpleObject response = new SimpleObject();
        response.add(IDENTIFIER_KEY, identifier);
        return response;
    }

    @Override
    public Object retrieve(String parentUniqueId, String uniqueId, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object update(String parentUniqueId, String uniqueId, SimpleObject propertiesToUpdate, RequestContext context) throws
            ResponseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String parentUniqueId, String uniqueId, String reason, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void purge(String parentUniqueId, String uniqueId, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SimpleObject getAll(String parentUniqueId, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException();
    }


}