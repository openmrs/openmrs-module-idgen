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
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;



@SubClassHandler(supportedClass = IdentifierPool.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*", "1.11.*","1.12.*"," 2.0.*"," 2.1.*", "2.2.*"})
public class IdentifierPoolResourceHandler extends BaseDelegatingSubclassHandler<IdentifierSource, IdentifierPool>
        implements DelegatingSubclassHandler<IdentifierSource, IdentifierPool> {



    @Override
    public String getResourceVersion() {
        return "1.8";
    }

    @Override
    public IdentifierPool newDelegate() {
        return new IdentifierPool();
    }

    @Override
    public IdentifierPool save(IdentifierPool delegate) {
        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
        service.saveIdentifierSource(delegate);
        return delegate;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        if (representation.equals(Representation.DEFAULT)) {
            DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
            representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("display");
            representationDescription.addProperty("identifierType", Representation.REF);
            representationDescription.addSelfLink();
            return representationDescription;
        }
        if (representation.equals(Representation.FULL)) {
            DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
            representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("display");
            representationDescription.addProperty("identifierType", Representation.REF);
            representationDescription.addProperty("sequential");
            representationDescription.addProperty("refillWithScheduledTask");
            representationDescription.addProperty("source", Representation.REF);
            representationDescription.addProperty("batchSize");
            representationDescription.addProperty("minPoolSize");
            representationDescription.addSelfLink();
            return representationDescription;
        }
        if (representation.equals(Representation.REF)) {
            DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
            representationDescription.addProperty("uuid");
            representationDescription.addProperty("display");
            representationDescription.addSelfLink();
            return representationDescription;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
        representationDescription.addRequiredProperty("name");
        representationDescription.addRequiredProperty("identifierType");
        representationDescription.addRequiredProperty("sequential");
        representationDescription.addRequiredProperty("refillWithScheduledTask");
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
    public PageableResult getAllByType(RequestContext requestContext) throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException();
    }


}