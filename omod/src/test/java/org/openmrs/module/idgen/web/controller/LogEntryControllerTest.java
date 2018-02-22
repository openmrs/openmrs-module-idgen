/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.web.controller;

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

public class LogEntryControllerTest extends MainResourceControllerTest {
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

	@Override
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}

	@Test
	public void shouldListAllLogEntries() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnAListOfLogEntriesWithGivenSourceUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("source", LOG_ENTRY_SOURCE_UUID);
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertEquals(3, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnNothingIfSourceUuidDoesNotMatch() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("source", "invalid-source");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(0, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnAListOfLogEntriesWithIdentifier() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("identifier", "H9");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnNothingIfIdentifierDoesNotMatch() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("identifier", "invalid-identifier");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(0, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnAListOfLogEntriesGeneratedInDateRange() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2016-10-01T12:00:00.423");
		req.addParameter("toDate", "2017-09-30T12:00:00.423");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnNothingIfNoLogsGeneratedWithinRange() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2015-10-01T12:00:00.423");
		req.addParameter("toDate", "2016-09-30T12:00:00.423");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(0, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnAListOfLogEntriesWhoseCommentsContain() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("comment", "New");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnNothingIfValueIsNotFoundInComments() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("comment", "invalid comment");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(0, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnAListOfLogEntriesGeneratedByUser() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("generatedBy", USER_UUID);
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnAListOfLogEntriesWithMatchingSpecifiedParameters() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("generatedBy", USER_UUID);
		req.addParameter("identifier", "100");
		req.addParameter("comment", "New");
		req.addParameter("fromDate", "2016-10-01T12:00:00.423");
		req.addParameter("toDate", "2017-10-04T12:00:00.423");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnNothingIfSelectedUserGeneratedNoLogEntries() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("generatedby", "7");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(0, Util.getResultsSize(result));
	}

	@Test
	public void shouldSearchAndReturnNothingIfOneOfTheParametersDoesnotMatch() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("identifier", "20000");
		req.addParameter("comment", "New");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(0, Util.getResultsSize(result));
	}
}
