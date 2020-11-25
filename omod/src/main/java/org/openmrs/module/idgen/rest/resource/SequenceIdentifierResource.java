package org.openmrs.module.idgen.rest.resource;

import java.util.Arrays;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.Identifier;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.controller.IdgenRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE
+ "/nextIdentifier", supportedClass = Identifier.class, supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*",
		"1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*" })
public class SequenceIdentifierResource extends DelegatingCrudResource<Identifier> {

	@Override
	public Identifier newDelegate() {
		return new Identifier();
	}

	@Override
	public Identifier save(Identifier delegate) {
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
	protected PageableResult doSearch(RequestContext context) {
		String src = context.getRequest().getParameter("source");
		IdentifierSource source = Context.getService(IdentifierSourceService.class).getIdentifierSource(Integer.parseInt(src));
		String identifierValue = Context.getService(IdentifierSourceService.class).generateIdentifier(source, "comment");
		Identifier id = new Identifier();
		id.setIdentifierValue(identifierValue);
		return new NeedsPaging<Identifier>(Arrays.asList(id), context);
	}

	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		if (rep instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("identifierValue");
		} else if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("identifierValue");
		}
		return description;
	}
}
