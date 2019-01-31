package org.openmrs.module.idgen.rest.resource.handler;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
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

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.StringProperty;

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
			representationDescription.addProperty("uuid");
			representationDescription.addProperty("name");
			representationDescription.addProperty("description");
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
			representationDescription.addProperty("uuid");
			representationDescription.addProperty("name");
			representationDescription.addProperty("description");
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
			representationDescription.addProperty("uuid");
			representationDescription.addProperty("display");
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
		List<IdentifierSource> identifierSources = Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false);
		if (identifierSources == null) {
			return null;
		}
		List<SequentialIdentifierGenerator> sequentialGenerators = new ArrayList<SequentialIdentifierGenerator>();
		for (IdentifierSource src : identifierSources) {
			if (src instanceof SequentialIdentifierGenerator) {
				sequentialGenerators.add((SequentialIdentifierGenerator) src);
			}
		}
		return new NeedsPaging<SequentialIdentifierGenerator>(sequentialGenerators, context);
	}

	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
				.property("baseCharacterSet", new StringProperty())
				.property("prefix", new StringProperty())
				.property("suffix", new StringProperty())
				.property("firstIdentifierBase", new StringProperty())
				.property("minLength", new IntegerProperty())
				.property("maxLength", new IntegerProperty());
		}
		if (rep instanceof FullRepresentation) {
			model
				.property("nextSequenceValue", new LongProperty());
		}
		if (rep instanceof RefRepresentation) {
			model
				.property("baseCharacterSet", new StringProperty());
		}
		return model;
	}

	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
				.property("baseCharacterSet", new StringProperty())
				.property("prefix", new StringProperty())
				.property("suffix", new StringProperty())
				.property("firstIdentifierBase", new StringProperty())
				.property("minLength", new IntegerProperty())
				.property("nextSequenceValue", new LongProperty())
				.property("maxLength", new IntegerProperty());
	}
	
	@PropertyGetter("display")
    public String getDisplayString(SequentialIdentifierGenerator identifierSource) {
        return identifierSource.getIdentifierType() + " - " 
                + identifierSource.getName() + " - "
                + identifierSource.getClass().getName();
    }

	
}
