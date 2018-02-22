/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import java.util.Date;
import java.util.List;

import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.controller.IdgenRestController;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

@Resource(name = RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE
		+ "/logentry", supportedClass = LogEntry.class, supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*",
				"1.12.*", "2.0.*", "2.1.*" })
public class LogEntryResource extends DelegatingCrudResource<LogEntry> {

	@Override
	protected NeedsPaging<LogEntry> doGetAll(RequestContext context) {
		return new NeedsPaging<LogEntry>(
				Context.getService(IdentifierSourceService.class).getLogEntries(null, null, null, null, null, null),
				context);
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
		UserService service = Context.getUserService();

		String source = context.getRequest().getParameter("source");
		String fromDate = context.getRequest().getParameter("fromDate");
		String toDate = context.getRequest().getParameter("toDate");
		String identifier = context.getRequest().getParameter("identifier");
		String comment = context.getRequest().getParameter("comment");
		String generatedBy = context.getRequest().getParameter("generatedBy");

		IdentifierSource logSource = source != null ? identifierSourceService.getIdentifierSourceByUuid(source) : null;
		Date dateFrom = fromDate != null ? (Date) ConversionUtil.convert(fromDate, Date.class) : null;
		Date dateTo = toDate != null ? (Date) ConversionUtil.convert(toDate, Date.class) : null;
		User user = generatedBy != null ? service.getUserByUuid(generatedBy) : null;
		if (logSource == null && identifier == null && comment == null && dateFrom == null && dateTo == null
				&& user == null) {
			return new EmptySearchResult();
		}
		List<LogEntry> logEntries = identifierSourceService.getLogEntries(logSource, dateFrom, dateTo, identifier, user,
				comment);
		return new NeedsPaging<LogEntry>(logEntries, context);
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		if (rep instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("identifier");
			description.addSelfLink();
		} else if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("source");
			description.addProperty("identifier");
			description.addProperty("comment");
			description.addProperty("generatedBy");
			description.addProperty("dateGenerated");
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			}
		}
		return description;
	}

	@PropertyGetter("display")
	public String getDisplay(LogEntry logentry) {
		return logentry.getSource().toString();
	}

	@Override
	public LogEntry newDelegate() throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public LogEntry save(LogEntry delegate) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public LogEntry getByUniqueId(String uniqueId) {
		List<LogEntry> logentries = Context.getService(IdentifierSourceService.class).getLogEntries(null, null, null,
				uniqueId, null, null);
		if (logentries.size() != 0) {
			return logentries.get(0);
		} else {
			return null;
		}
	}

	@Override
	protected void delete(LogEntry delegate, String reason, RequestContext context)
			throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public void purge(LogEntry delegate, RequestContext context) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}

}
