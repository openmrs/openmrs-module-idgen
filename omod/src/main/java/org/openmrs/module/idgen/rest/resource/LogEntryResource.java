package org.openmrs.module.idgen.rest.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.web.controller.IdgenRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

public class LogEntryResource extends DelegatingCrudResource<LogEntry> implements SearchHandler {
	private final SearchConfig searchConfig = new SearchConfig("default",
			RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE + "/viewlogentries",
			Arrays.asList("1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"),
			Arrays.asList(new SearchQuery.Builder(
					"Allows you to find log of ID Generation Activities by Source Name, Identifier contents,Generated Date Range,Comment contents and User who generated the log entry")
							.withRequiredParameters("sourceName")
							.withOptionalParameters("idContains", "dateRange", "commentContains", "generatedBy")
							.build()));

	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return getPageableResult(context);

	}

	private PageableResult getPageableResult(RequestContext context) {
		return null;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String sourceName = context.getParameter("sourceName");
		String idContains = context.getParameter("idContains");
		String dateRange = context.getParameter("dateRange");
		String commentContains = context.getParameter("commentContains");
		String generatedBy = context.getParameter("generatedBy");

		List<LogEntry> logentries = new ArrayList<LogEntry>();

		return getPageableResult(context);
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		return null;
	}

	@Override
	public LogEntry newDelegate() {
		return null;
	}

	@Override
	public LogEntry save(LogEntry delegate) {
		return null;
	}

	@Override
	public LogEntry getByUniqueId(String uniqueId) {
		return null;
	}

	@Override
	protected void delete(LogEntry delegate, String reason, RequestContext context) throws ResponseException {

	}

	@Override
	public void purge(LogEntry delegate, RequestContext context) throws ResponseException {

	}

}