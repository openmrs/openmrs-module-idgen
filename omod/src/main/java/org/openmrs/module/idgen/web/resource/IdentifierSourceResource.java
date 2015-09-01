package org.openmrs.module.idgen.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/identifierSource", supportedClass = IdentifierSource.class,
        supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*"})
public class IdentifierSourceResource extends MetadataDelegatingCrudResource<IdentifierSource> {
    public static final String IDENTIFIER_POOL = "identifierPool";
    public static final String REMOTE_IDENTIFIER_SOURCE = "remoteIdentifierSource";
    public static final String SEQUENTIAL_IDENTIFIER_GENERATOR = "sequentialIdentifierGenerator";

    @Override
    public IdentifierSource getByUniqueId(String uuid) {
        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
        return service.getIdentifierSourceByUuid(uuid);
    }

    @Override
    public boolean hasTypesDefined() {
        return true;
    }

    @Override
    public IdentifierSource newDelegate() {
        return null;
    }

    @Override
    public IdentifierSource save(IdentifierSource identifierSource) {
        return this.saveIdentifierSource(identifierSource, getResourceHandler(identifierSource.getClass()));
    }

    private <T extends IdentifierSource> T saveIdentifierSource(IdentifierSource identifierSource, DelegatingResourceHandler<T> resourceHandler) {
        resourceHandler.save((T) identifierSource);
        return (T) identifierSource;
    }

    @Override
    public void purge(IdentifierSource identifierSource, RequestContext requestContext) throws ResponseException {
        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
        service.purgeIdentifierSource(identifierSource);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        return null;
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
        return new AlreadyPaged<IdentifierSource>(context, service.getAllIdentifierSources(false), false);
    }


}
