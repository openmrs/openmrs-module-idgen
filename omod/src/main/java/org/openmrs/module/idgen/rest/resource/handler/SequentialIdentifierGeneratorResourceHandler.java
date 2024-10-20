package org.openmrs.module.idgen.rest.resource.handler;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
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

@SubClassHandler(supportedClass = SequentialIdentifierGenerator.class, supportedOpenmrsVersions = {"1.8.* - 9.9.*"})
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
	public Schema<?> getGETSchema(Representation rep) {
		Schema<Object> model = new ObjectSchema();
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model.addProperty("baseCharacterSet", new StringSchema())
					.addProperty("prefix", new StringSchema())
					.addProperty("suffix", new StringSchema())
					.addProperty("firstIdentifierBase", new StringSchema())
					.addProperty("minLength", new IntegerSchema())
					.addProperty("maxLength", new IntegerSchema());
		}
		if (rep instanceof FullRepresentation) {
			model.addProperty("nextSequenceValue", new Schema<Long>().type("integer").format("int64"));
		}
		if (rep instanceof RefRepresentation) {
			model.addProperty("baseCharacterSet", new StringSchema());
		}
		return model;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		return new ObjectSchema()
				.addProperty("baseCharacterSet", new StringSchema())
				.addProperty("prefix", new StringSchema())
				.addProperty("suffix", new StringSchema())
				.addProperty("firstIdentifierBase", new StringSchema())
				.addProperty("minLength", new IntegerSchema())
				.addProperty("nextSequenceValue", new Schema<Long>().type("integer").format("int64"))
				.addProperty("maxLength", new IntegerSchema());
	}
	
	@PropertyGetter("display")
    public String getDisplayString(SequentialIdentifierGenerator identifierSource) {
        return identifierSource.getIdentifierType() + " - " 
                + identifierSource.getName() + " - "
                + identifierSource.getClass().getName();
    }

}
