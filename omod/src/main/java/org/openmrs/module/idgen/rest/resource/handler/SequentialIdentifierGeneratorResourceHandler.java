package org.openmrs.module.idgen.rest.resource.handler;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.rest.resource.IdentifierSourceResource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

@SubClassHandler(supportedClass = SequentialIdentifierGenerator.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*","2.2.*"})
public class SequentialIdentifierGeneratorResourceHandler extends BaseDelegatingSubclassHandler<IdentifierSource, SequentialIdentifierGenerator>
implements DelegatingSubclassHandler<IdentifierSource, SequentialIdentifierGenerator> {

	@Override
	public String getResourceVersion() {
		return "2.2";
	}
	
	@Override
	public SequentialIdentifierGenerator newDelegate() {
		return new SequentialIdentifierGenerator();
	}

	@Override
	public SequentialIdentifierGenerator save(SequentialIdentifierGenerator delegate) {
		IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
		service.saveIdentifierSource(delegate);
		return delegate;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		if (representation instanceof DefaultRepresentation) {
			representationDescription.addProperty("baseCharacterSet");
			representationDescription.addProperty("prefix");
			representationDescription.addProperty("suffix");
			representationDescription.addProperty("firstIdentifierBase");
			representationDescription.addProperty("minLength");
			representationDescription.addProperty("maxLength");
            		representationDescription.addProperty("identifierType", Representation.DEFAULT);
			representationDescription.addSelfLink();
			return representationDescription;
		}
		if (representation instanceof FullRepresentation) {
			representationDescription.addProperty("nextSequenceValue");
			representationDescription.addProperty("baseCharacterSet");
			representationDescription.addProperty("prefix");
			representationDescription.addProperty("suffix");
			representationDescription.addProperty("firstIdentifierBase");
			representationDescription.addProperty("minLength");
			representationDescription.addProperty("maxLength");
            		representationDescription.addProperty("identifierType", Representation.FULL);
			representationDescription.addSelfLink();
			return representationDescription;
		}
		if (representation instanceof RefRepresentation) {
			representationDescription.addProperty("baseCharacterSet");
            		representationDescription.addProperty("identifierType", Representation.REF);
			representationDescription.addSelfLink();
			return representationDescription;
		}
		
		return null;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		representationDescription.addProperty("nextSequenceValue");
		representationDescription.addProperty("baseCharacterSet");
		representationDescription.addProperty("prefix");
		representationDescription.addProperty("suffix");
		representationDescription.addProperty("firstIdentifierBase");
		representationDescription.addProperty("minLength");
		representationDescription.addProperty("maxLength");	
		return representationDescription;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
		representationDescription.addProperty("nextSequenceValue");
		representationDescription.addProperty("baseCharacterSet");
		representationDescription.addProperty("prefix");
		representationDescription.addProperty("suffix");
		representationDescription.addProperty("firstIdentifierBase");
		representationDescription.addProperty("minLength");
		representationDescription.addProperty("maxLength");
		return representationDescription;
	}
	
	@Override
	public String getTypeName() {   
		return IdentifierSourceResource.SEQUENTIAL_IDENTIFIER_GENERATOR;
	}

	@Override
	public PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
}
