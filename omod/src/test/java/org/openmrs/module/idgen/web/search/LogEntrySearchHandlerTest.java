/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.web.search;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class LogEntrySearchHandlerTest extends MainResourceControllerTest {
	public static String USER_UUID = "1010d442-e134-11de-babe-001e378eb67e";
	public static String LOG_ENTRY_SOURCE_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a901";

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
	}

	@Override
	public long getAllCount() {
		return Context.getService(IdentifierSourceService.class).getLogEntries(null, null, null, null, null, null)
				.size();
	}

	@Override
	public String getURI() {
		return "idgen/logentry";
	}

	@Override
	public String getUuid() {
		return "100892";
	}

	@Test
	public void getSearchConfig_shouldReturnLogEntryBySource() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("source", LOG_ENTRY_SOURCE_UUID);
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertEquals(3, Util.getResultsSize(result));
	}

	@Test
	public void getSearchConfig_shouldReturnLogEntryByComment() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("comment", "New");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void getSearchConfig_shouldReturnLogEntryByIdentifier() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("identifier", "H9");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void getSearchConfig_shouldReturnLogEntryByDateGenerated() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2016-10-01T12:00:00.433");
		req.addParameter("toDate", "2017-09-30T12:00:00.433");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void getSearchConfig_shouldReturnLogEntryByGeneratedBy() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("generatedBy", USER_UUID);
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}
}
