package org.openmrs.module.idgen.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.springframework.beans.factory.annotation.Autowired;

@SubClassHandler(supportedClass = SequentialIdentifierGenerator.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*"})
public class SequentialIdentifierGeneratorResourceHandler
        extends BaseDelegatingSubclassHandler<IdentifierSource, SequentialIdentifierGenerator>
        implements DelegatingSubclassHandler<IdentifierSource, SequentialIdentifierGenerator> {

    @Autowired
    private IdentifierSourceService identifierSourceService;

    @Override
    public SequentialIdentifierGenerator newDelegate() {
        return new SequentialIdentifierGenerator();
    }

    @Override
    public SequentialIdentifierGenerator save(SequentialIdentifierGenerator delegate) {
        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
        service.saveIdentifierSource(delegate);
        identifierSourceService.saveSequenceValue(delegate, delegate.getNextSequenceValue());
        return delegate;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        if (representation.equals(Representation.DEFAULT)) {
            DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
            representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("identifierType", Representation.REF);
            representationDescription.addProperty("nextSequenceValue");
            representationDescription.addSelfLink();
            return representationDescription;
        }
        if (representation.equals(Representation.FULL)) {
            DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
            representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addProperty("prefix");
            representationDescription.addProperty("suffix");
            representationDescription.addProperty("firstIdentifierBase");
            representationDescription.addProperty("minLength");
            representationDescription.addProperty("maxLength");
            representationDescription.addProperty("baseCharacterSet");
            representationDescription.addProperty("nextSequenceValue");
            representationDescription.addProperty("identifierType", Representation.REF);
            representationDescription.addSelfLink();
            return representationDescription;
        }
        if (representation.equals(Representation.REF)) {
            DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
            representationDescription.addProperty("uuid");
            representationDescription.addProperty("name");
            representationDescription.addSelfLink();
            return representationDescription;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
        representationDescription.addRequiredProperty("name");
        representationDescription.addRequiredProperty("firstIdentifierBase");
        representationDescription.addRequiredProperty("baseCharacterSet");
        representationDescription.addRequiredProperty("identifierType");
        representationDescription.addProperty("prefix");
        representationDescription.addProperty("suffix");
        representationDescription.addProperty("minLength");
        representationDescription.addProperty("maxLength");
        return representationDescription;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription representationDescription = new DelegatingResourceDescription();
        representationDescription.addRequiredProperty("name");
        representationDescription.addRequiredProperty("firstIdentifierBase");
        representationDescription.addRequiredProperty("baseCharacterSet");
        representationDescription.addRequiredProperty("identifierType");
        representationDescription.addProperty("prefix");
        representationDescription.addProperty("suffix");
        representationDescription.addProperty("minLength");
        representationDescription.addProperty("maxLength");
        representationDescription.addProperty("nextSequenceValue");
        return representationDescription;
    }

    @Override
    public String getTypeName() {
        return IdentifierSourceResource.SEQUENTIAL_IDENTIFIER_GENERATOR;
    }

    @Override
    public PageableResult getAllByType(RequestContext requestContext) throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException();
    }
}
