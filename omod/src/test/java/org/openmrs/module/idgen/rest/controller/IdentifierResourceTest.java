/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.rest.resource.IdentifierResource;
import org.openmrs.module.idgen.web.controller.BaseWebControllerTest;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;

public class IdentifierResourceTest extends BaseWebControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }

    public String getURI() {
        return "idgen/identifiersource";
    }

    @Test
    public void shouldGetNewIdentifier_SequenceIdentifierGenerator() throws Exception {
        SimpleObject result = deserialize(handle(
                newPostRequest(getURI() + "/" + "0d47284f-9e9b-4a81-a88b-8bb42bc0a907" + "/identifier",
                        "{\"comment\":\"foo\"}")), SimpleObject.class);

        assertEquals("MRS000011", result.get(IdentifierResource.IDENTIFIER_KEY));
    }

    @Test
    public void shouldGetNewIdentifier_PooledIdentifier() throws Exception {
        MockHttpServletRequest request = newPostRequest(getURI() + "/" + "0d47284f-9e9b-4a81-a88b-8bb42bc0a903" + "/identifier",
                "{\"comment\":\"foo\"}");

        SimpleObject result = deserialize(handle(request), SimpleObject.class);

        assertEquals("0-0", result.get(IdentifierResource.IDENTIFIER_KEY));
    }
    
	public long getAllCount() {
		return 8;
	}

	public String getUuid() {
		return "0d47284f-9e9b-4a81-a88b-8bb42bc0a907";
	}
}