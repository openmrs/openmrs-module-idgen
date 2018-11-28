package org.openmrs.module.idgen.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.rest.resource.IdentifierSourceResource;
import org.openmrs.module.idgen.service.BaseIdentifierSourceService;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

@SubClassHandler(supportedClass = RemoteIdentifierSource.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*", "1.11.*","2.2.*"})
public class RemoteIdentifierSourceResourceHandler extends BaseDelegatingSubclassHandler<IdentifierSource, RemoteIdentifierSource>
implements DelegatingSubclassHandler<IdentifierSource, RemoteIdentifierSource> {

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
		BaseIdentifierSourceService serv = (BaseIdentifierSourceService) Context.getService(IdentifierSourceService.class);
		serv.saveIdentifierSource(delegate);
		return delegate;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		if (representation.equals(Representation.DEFAULT)) {
			representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("identifierType", Representation.REF);
            representationDescription.addSelfLink();
			return representationDescription;
		}
		if (representation.equals(Representation.FULL)) {
			representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("identifierType", Representation.FULL);
            representationDescription.addProperty("password");
            representationDescription.addProperty("url");
            representationDescription.addSelfLink();
           
			return representationDescription;
		}
		if (representation.equals(Representation.REF)) {
			 representationDescription.addProperty("uuid");
	         representationDescription.addProperty("name");
	         representationDescription.addSelfLink();
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
		return new IdentifierSourceResource().REMOTE_IDENTIFIER_SOURCE;
	}

	@Override
	public PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	
	
}

