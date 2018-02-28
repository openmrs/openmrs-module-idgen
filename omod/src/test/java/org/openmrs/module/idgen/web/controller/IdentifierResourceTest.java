package org.openmrs.module.idgen.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.rest.resource.IdentifierResource;
import org.openmrs.module.webservices.rest.SimpleObject;

import static org.junit.Assert.assertEquals;

public class IdentifierResourceTest extends MainResourceControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }

    public String getURI() {
        return "identifierSource";
    }

    @Test
    public void shouldGetNewIdentifier_SequenceIdentifierGenerator() throws Exception {
        SimpleObject result = deserialize(handle(
                newPostRequest(getURI() + "/" + "0d47284f-9e9b-4a81-a88b-8bb42bc0a907" + "/identifier",
                        "{\"comment\":\"foo\"}")));

        assertEquals("MRS000011", result.get(IdentifierResource.IDENTIFIER_KEY));
    }

    @Test
    public void shouldGetNewIdentifier_PooledIdentifier() throws Exception {
        SimpleObject result = deserialize(handle(
                newPostRequest(getURI() + "/" + "0d47284f-9e9b-4a81-a88b-8bb42bc0a903" + "/identifier",
                        "{\"comment\":\"foo\"}")));

        assertEquals("0-0", result.get(IdentifierResource.IDENTIFIER_KEY));
    }
}