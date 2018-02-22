/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.rest.resource.LogEntryResource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class LogEntryResourceTest extends BaseDelegatingResourceTest<LogEntryResource, LogEntry> {

	private final static String LOG_ENTRY_IDENTIFIER = "100892";

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
	}

	@Override
	public String getDisplayProperty() {
		return getObject().getSource().toString();
	}

	@Override
	public String getUuidProperty() {
		return LOG_ENTRY_IDENTIFIER;
	}

	@Override
	public LogEntry newObject() {
		return Context.getService(IdentifierSourceService.class)
				.getLogEntries(null, null, null, getUuidProperty(), null, null).get(0);
	}

	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		assertPropEquals("uuid", getUuidProperty());
		assertPropEquals("display", getDisplayProperty());
		assertPropEquals("identifier", getObject().getIdentifier());
	}

	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("uuid");
		assertPropPresent("source");
		assertPropPresent("identifier");
		assertPropPresent("comment");
		assertPropPresent("generatedBy");
		assertPropPresent("dateGenerated");
	}

	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("uuid");
		assertPropPresent("source");
		assertPropPresent("identifier");
		assertPropPresent("comment");
		assertPropPresent("generatedBy");
		assertPropPresent("dateGenerated");
	}
}