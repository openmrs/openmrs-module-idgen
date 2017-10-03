/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.web.controller;

import org.junit.Test;
import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import org.junit.Assert;

public class LogEntryControllerTest extends MainResourceControllerTest {
    private static final String LOG_ENTRIES_XML = "logEntriesTestDataset.xml";

    private IdentifierSourceService service;

    @Before
    public void before() {
        this.service = Context.getService(IdentifierSourceService.class);

    }

    @Override
    public long getAllCount() {
        return service.getLogEntries(null, null, null, null, null, null).size();
    }

    @Override
    public String getURI() {
        return "idgen/viewlogentries";
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Test
    public void shouldListAllLogEntries() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        SimpleObject result = deserialize(handle(req));
        Assert.assertNotNull(result);
        Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnAListOfLogEntriesWithGivenSourceName() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("source", "Generator");
        SimpleObject result = deserialize(handle(req));
        Assert.assertNotNull(result);
        Assert.assertEquals(3, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnNothingIfSourceNameDoesNotMatch() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("source", "invalid_source");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(0, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnAListOfLogEntriesWithIdentifier() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("identifier", "H9");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(2, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnNothingIfIdentifierDoesNotMatch() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("identifier", "invalid-identifier");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(0, Util.getResultsSize(result));

    }

    @Test
    public void shouldSearchAndReturnAListOfLogEntriesGeneratedInDateRange() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("fromDate", "1/10/2016");
        req.addParameter("toDate", "30/09/2017");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(2, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnNothingIfNoLogsGeneratedWithinRange() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("fromDate", "1/10/2015");
        req.addParameter("toDate", "30/09/2016");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(0, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnAListOfLogEntriesWhoseCommentsContain() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("comment", "New");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(2, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnNothingIfCommentsDontContain() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("comment", "invalid comment");
    }

    @Test
    public void shouldSearchAndReturnAListOfLogEntriesGeneratedByUser() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("generatedby", "Admin");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(4, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnAListOfLogEntriesWithMatchingSpecifiedParameters() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("generatedby", "Admin");
        req.addParameter("identifier", "100");
        req.addParameter("comment", "New");
        req.addParameter("fromDate", "1/10/2016");
        req.addParameter("toDate", "30/09/2017");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(4, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnNothingIfSelectedUserGeneratedNoLogEntries() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("generatedby", "nologsuser");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(0, Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchAndReturnNothingIfOneOfTheParametersDoesnotMatch() throws Exception {
        executeDataSet(LOG_ENTRIES_XML);
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("generatedby", "nologsuser");
        req.addParameter("identifier", "100");
        req.addParameter("comment", "New");
        SimpleObject result = deserialize(handle(req));
        Assert.assertEquals(0, Util.getResultsSize(result));

    }

}