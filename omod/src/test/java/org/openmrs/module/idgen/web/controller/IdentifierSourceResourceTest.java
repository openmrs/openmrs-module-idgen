package org.openmrs.module.idgen.web.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.rest.resource.IdentifierSourceResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;

public class IdentifierSourceResourceTest extends MainResourceControllerTest {

    public static final String IDENTIFIER_POOL_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a903";
    public static final String REMOTE_IDENTIFIER_SOURCE_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a902";
    public static final String LOCAL_SEQ_ID_GENERATOR_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a907";
    public static final String IDENTIFIER_TYPE_UUID = "2f470aa8-1d73-43b7-81b5-01f0c0dfa53c";

    @Before
    public void init() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }

    public String getURI() {
        return "identifierSource";
    }


    @Test
    public void shouldGetDefaultByUuid_SequentialIdentifierGenerator() throws Exception {
        MockHttpServletResponse response = handle(request(RequestMethod.GET, getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID));
        SimpleObject result = deserialize(response);

        Assert.assertNotNull(result);
        Assert.assertEquals(LOCAL_SEQ_ID_GENERATOR_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Local Sequential Generator", PropertyUtils.getProperty(result, "name"));
        Assert.assertEquals(11, PropertyUtils.getProperty(result, "nextSequenceValue"));
    }

    @Test
    public void shouldGetRefByUuid_SequentialIdentifierGenerator() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID);
        request.addParameter("v", "ref");
        SimpleObject result = deserialize(handle(request));

        Assert.assertNotNull(result);
        Assert.assertEquals(LOCAL_SEQ_ID_GENERATOR_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Local Sequential Generator", PropertyUtils.getProperty(result, "name"));
        ArrayList links = (ArrayList) PropertyUtils.getProperty(result, "links");
        Assert.assertEquals(1, links.size());
    }

    @Test
    public void shouldGetFullByUuid_SequentialIdentifierGenerator() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID);
        request.addParameter("v", "full");
        SimpleObject result = deserialize(handle(request));

        Assert.assertNotNull(result);
        Assert.assertEquals(LOCAL_SEQ_ID_GENERATOR_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals(11, PropertyUtils.getProperty(result, "nextSequenceValue"));
        Assert.assertEquals("MRS", PropertyUtils.getProperty(result, "prefix"));
        Assert.assertEquals(4, PropertyUtils.getProperty(result, "minLength"));
        Assert.assertEquals(9, PropertyUtils.getProperty(result, "maxLength"));
        Assert.assertEquals("0123456789", PropertyUtils.getProperty(result, "baseCharacterSet"));
        Assert.assertEquals("200000", PropertyUtils.getProperty(result, "firstIdentifierBase"));
    }

    @Test
    public void shouldCreateSequentialIdentifierGenerator() throws Exception {
        Assert.assertEquals((long) 7, Util.getResultsSize(deserialize(handle(request(RequestMethod.GET, getURI())))));

        SimpleObject sequentialIdentifierGenerator = new SimpleObject();
        sequentialIdentifierGenerator.add("type", IdentifierSourceResource.SEQUENTIAL_IDENTIFIER_GENERATOR);
        sequentialIdentifierGenerator.add("name", "BAH");
        sequentialIdentifierGenerator.add("prefix", "BAH");
        sequentialIdentifierGenerator.add("suffix", "");
        sequentialIdentifierGenerator.add("firstIdentifierBase", "200000");
        sequentialIdentifierGenerator.add("minLength", 4);
        sequentialIdentifierGenerator.add("maxLength", 9);
        sequentialIdentifierGenerator.add("baseCharacterSet", "0123456789");
        sequentialIdentifierGenerator.add("identifierType", IDENTIFIER_TYPE_UUID);

        MockHttpServletRequest request = newPostRequest(getURI(), sequentialIdentifierGenerator);
        SimpleObject result = deserialize(handle(request));
        Assert.assertEquals((long) 8, Util.getResultsSize(deserialize(handle(request(RequestMethod.GET, getURI())))));
    }

    @Test
    public void shouldUpdateSequentialIdentifierGenerator() throws Exception {

        SimpleObject sequentialIdentifierGenerator = new SimpleObject();
        sequentialIdentifierGenerator.add("uuid", LOCAL_SEQ_ID_GENERATOR_UUID);
        sequentialIdentifierGenerator.add("name", "BOO");

        MockHttpServletRequest updateRequest = newPostRequest(getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID,
                sequentialIdentifierGenerator);
        SimpleObject result = deserialize(handle(updateRequest));

        MockHttpServletResponse response = handle(request(RequestMethod.GET, getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID));
        Assert.assertEquals("BOO", PropertyUtils.getProperty(deserialize(response), "name"));
    }

    @Test
    public void shouldNotUpdateSequentialIdentifierGenerator_NextSequenceValue() throws Exception {
        MockHttpServletResponse seqIdentifier = handle(request(RequestMethod.GET, getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID));
        Object prevSequenceValue = PropertyUtils.getProperty(deserialize(seqIdentifier), "nextSequenceValue");

        SimpleObject sequentialIdentifierGenerator = new SimpleObject();
        sequentialIdentifierGenerator.add("uuid", LOCAL_SEQ_ID_GENERATOR_UUID);
        sequentialIdentifierGenerator.add("nextSequenceValue", "200027");

        MockHttpServletRequest updateRequest = newPostRequest(getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID,
                sequentialIdentifierGenerator);
        SimpleObject result = deserialize(handle(updateRequest));
        seqIdentifier = handle(request(RequestMethod.GET, getURI() + "/" + LOCAL_SEQ_ID_GENERATOR_UUID));
        Assert.assertEquals(prevSequenceValue, PropertyUtils.getProperty(deserialize(seqIdentifier), "nextSequenceValue"));
    }


    @Test
    public void shouldGetDefaultByUuid_IdentifierPool() throws Exception {
        MockHttpServletResponse response = handle(request(RequestMethod.GET, getURI() + "/" + IDENTIFIER_POOL_UUID));
        SimpleObject result = deserialize(response);

        Assert.assertNotNull(result);
        Assert.assertEquals(IDENTIFIER_POOL_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Test Identifier Pool", PropertyUtils.getProperty(result, "name"));
    }

    @Test
    public void shouldGetRefByUuid_IdentifierPool() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + IDENTIFIER_POOL_UUID);
        request.addParameter("v", "ref");
        SimpleObject result = deserialize(handle(request));

        Assert.assertNotNull(result);
        Assert.assertEquals(IDENTIFIER_POOL_UUID, PropertyUtils.getProperty(result, "uuid"));
    }

    @Test
    public void shouldGetFullByUuid_IdentifierPool() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + IDENTIFIER_POOL_UUID);
        request.addParameter("v", "full");
        SimpleObject result = deserialize(handle(request));

        Assert.assertNotNull(result);
        Assert.assertEquals(IDENTIFIER_POOL_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Test Identifier Pool", PropertyUtils.getProperty(result, "name"));
    }


    @Test
    public void shouldCreateIdentifierPool() throws Exception {
        Assert.assertEquals((long) 7, Util.getResultsSize(deserialize(handle(request(RequestMethod.GET, getURI())))));

        SimpleObject identifierPool = new SimpleObject();
        identifierPool.add("type", IdentifierSourceResource.IDENTIFIER_POOL);
        identifierPool.add("name", "BAH");
        identifierPool.add("sequential", true);
        identifierPool.add("refillWithScheduledTask", true);
        identifierPool.add("source", LOCAL_SEQ_ID_GENERATOR_UUID);
        identifierPool.add("batchSize", 1000);
        identifierPool.add("minPoolSize", 500);
        identifierPool.add("identifierType", IDENTIFIER_TYPE_UUID);

        MockHttpServletRequest request = newPostRequest(getURI(), identifierPool);
        SimpleObject result = deserialize(handle(request));
        Assert.assertEquals((long) 8, Util.getResultsSize(deserialize(handle(request(RequestMethod.GET, getURI())))));
    }

    @Test
    public void shouldUpdateIdentifierPool() throws Exception {

        SimpleObject identifierPool = new SimpleObject();
        identifierPool.add("type", IdentifierSourceResource.IDENTIFIER_POOL);
        identifierPool.add("sequential", true);
        identifierPool.add("refillWithScheduledTask", true);
        identifierPool.add("source", IDENTIFIER_POOL_UUID);
        identifierPool.add("batchSize", 1001);
        identifierPool.add("minPoolSize", 501);

        MockHttpServletRequest updateRequest = newPostRequest(getURI() + "/" + IDENTIFIER_POOL_UUID,
                identifierPool);
        SimpleObject result = deserialize(handle(updateRequest));

        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + IDENTIFIER_POOL_UUID);
        request.addParameter("v", "full");
        MockHttpServletResponse response = handle(request);
        Assert.assertEquals(1001, PropertyUtils.getProperty(deserialize(response), "batchSize"));
        Assert.assertEquals(501, PropertyUtils.getProperty(deserialize(response), "minPoolSize"));
    }


    @Test
    public void shouldGetDefaultByUuid_RemoteIdentifierSource() throws Exception {
        MockHttpServletResponse response = handle(request(RequestMethod.GET, getURI() + "/" + REMOTE_IDENTIFIER_SOURCE_UUID));
        SimpleObject result = deserialize(response);

        Assert.assertNotNull(result);
        Assert.assertEquals(REMOTE_IDENTIFIER_SOURCE_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Test Remote Source", PropertyUtils.getProperty(result, "name"));
    }

    @Test
    public void shouldGetRefByUuid_RemoteIdentifierSource() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + REMOTE_IDENTIFIER_SOURCE_UUID);
        request.addParameter("v", "ref");
        SimpleObject result = deserialize(handle(request));

        Assert.assertNotNull(result);
        Assert.assertEquals(REMOTE_IDENTIFIER_SOURCE_UUID, PropertyUtils.getProperty(result, "uuid"));
    }

    @Test
    public void shouldGetFullByUuid_RemoteIdentifierSource() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + REMOTE_IDENTIFIER_SOURCE_UUID);
        request.addParameter("v", "full");
        SimpleObject result = deserialize(handle(request));

        Assert.assertNotNull(result);
        Assert.assertEquals(REMOTE_IDENTIFIER_SOURCE_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Test Remote Source", PropertyUtils.getProperty(result, "name"));
    }

    @Test
    public void shouldCreateRemoteIdentifierSource() throws Exception {
        Assert.assertEquals((long) 7, Util.getResultsSize(deserialize(handle(request(RequestMethod.GET, getURI())))));

        SimpleObject remoteIdentifierSource = new SimpleObject();
        remoteIdentifierSource.add("type", IdentifierSourceResource.REMOTE_IDENTIFIER_SOURCE);
        remoteIdentifierSource.add("name", "BAH");
        remoteIdentifierSource.add("identifierType", IDENTIFIER_TYPE_UUID);
        remoteIdentifierSource.add("url", "http://foo.bar");
        remoteIdentifierSource.add("user", "blah");
        remoteIdentifierSource.add("password", "******");
        MockHttpServletRequest request = newPostRequest(getURI(), remoteIdentifierSource);
        SimpleObject result = deserialize(handle(request));

        Assert.assertEquals((long) 8, Util.getResultsSize(deserialize(handle(request(RequestMethod.GET, getURI())))));
    }

    @Test
    public void shouldUpdateRemoteIdentifierSource() throws Exception {

        SimpleObject remoteIdentifierSource = new SimpleObject();
        remoteIdentifierSource.add("type", IdentifierSourceResource.REMOTE_IDENTIFIER_SOURCE);
        remoteIdentifierSource.add("url", "http://baz.boom");
        remoteIdentifierSource.add("user", "foo");
        remoteIdentifierSource.add("password", "******");
        MockHttpServletRequest updateRequest = newPostRequest(getURI() + "/" + REMOTE_IDENTIFIER_SOURCE_UUID,
                remoteIdentifierSource);
        SimpleObject result = deserialize(handle(updateRequest));

        MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + REMOTE_IDENTIFIER_SOURCE_UUID);
        request.addParameter("v", "full");
        MockHttpServletResponse response = handle(request);
        Assert.assertEquals("foo", PropertyUtils.getProperty(deserialize(response), "user"));
        Assert.assertEquals("http://baz.boom", PropertyUtils.getProperty(deserialize(response), "url"));
    }

    @Test
    public void shouldGetAll() throws Exception {
        SimpleObject result = deserialize(handle(request(RequestMethod.GET, getURI())));

        Assert.assertNotNull(result);
        Assert.assertEquals((long) 7, Util.getResultsSize(result));
    }


}