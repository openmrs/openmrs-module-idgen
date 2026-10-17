/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.RemoteIdentifierSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteIdentifierSourceProcessorRequestTest {

	private static final String SOURCE_UUID = "52250ca2-d25f-11e4-8dbf-54ee7513a7ff";

	private static final String REST_PATH = "/openmrs/ws/rest/v1/idgen/identifiersource";

	private static final String EXPORT_PATH = "/openmrs/module/idgen/exportIdentifiers.form";

	private static final String IDENTIFIERS_RESPONSE = "{\"identifiers\":[\"1234A\",\"1235B\"]}";

	private RemoteIdentifierSourceProcessor processor;

	private HttpServer server;

	private Map<String, String> receivedRequest;

	@Before
	public void setUp() {
		processor = new RemoteIdentifierSourceProcessor();
		// written by the server's thread and read by the test's thread; some values can be null, so not a ConcurrentHashMap
		receivedRequest = Collections.synchronizedMap(new HashMap<String, String>());
	}

	@After
	public void tearDown() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIdentifiers_shouldFailForRestUrlWithoutSourceUuid() throws Exception {
		processor.getIdentifiers(source("https://localhost" + REST_PATH, "user", "pass"), 5);
	}

	/**
	 * A post to a specific source's resource would update that source on the remote server
	 */
	@Test
	public void getIdentifiers_shouldNotPostToRestUrlForSpecificSource() throws Exception {
		String url = startServer(REST_PATH, 200, "{}") + "/" + SOURCE_UUID + "?sourceUuid=" + SOURCE_UUID;
		try {
			processor.getIdentifiers(source(url, "user", "pass"), 5);
			Assert.fail("Expected an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("must be to .../ws/rest/v1/idgen/identifiersource"));
		}
		Assert.assertNull("No request should be sent", receivedRequest.get("method"));
	}

	@Test
	public void getIdentifiers_shouldFailWithClearErrorForRestUrlThatIsNotValid() throws Exception {
		String url = "https://localhost" + REST_PATH + "?sourceUuid=" + SOURCE_UUID + "&comment=Site One";
		try {
			processor.getIdentifiers(source(url, "user", "pass"), 5);
			Assert.fail("Expected an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("is not valid") && e.getMessage().contains(url));
		}
	}

	@Test
	public void getIdentifiers_shouldGetIdentifiersFromRestEndpoint() throws Exception {
		String url = startServer(REST_PATH, 201, IDENTIFIERS_RESPONSE) + "?sourceUuid=" + SOURCE_UUID + "&comment=Site+One";

		List<String> identifiers = processor.getIdentifiers(source(url, "user", "pass"), 2);

		Assert.assertEquals(Arrays.asList("1234A", "1235B"), identifiers);
		Assert.assertEquals("POST", receivedRequest.get("method"));
		Assert.assertEquals(REST_PATH, receivedRequest.get("uri"));
		Assert.assertEquals("Basic " + base64("user:pass"), receivedRequest.get("authorization"));
		Assert.assertEquals("application/json", receivedRequest.get("accept"));
		Assert.assertTrue(receivedRequest.get("contentType").startsWith("application/json"));

		Map<String, Object> expectedBody = new HashMap<String, Object>();
		expectedBody.put("generateIdentifiers", true);
		expectedBody.put("sourceUuid", SOURCE_UUID);
		expectedBody.put("numberToGenerate", 2);
		expectedBody.put("comment", "Site One");
		Assert.assertEquals(expectedBody, new ObjectMapper().readValue(receivedRequest.get("body"), Map.class));
	}

	@Test
	public void getIdentifiers_shouldOmitCommentAndAuthorizationForRestUrlIfNotConfigured() throws Exception {
		String url = startServer(REST_PATH, 201, IDENTIFIERS_RESPONSE) + "?sourceUuid=" + SOURCE_UUID;

		processor.getIdentifiers(source(url, null, null), 2);

		Assert.assertNull(receivedRequest.get("authorization"));
		Assert.assertFalse(new ObjectMapper().readValue(receivedRequest.get("body"), Map.class).containsKey("comment"));
	}

	@Test
	public void getIdentifiers_shouldGetIdentifiersFromExportIdentifiersEndpoint() throws Exception {
		String url = startServer(EXPORT_PATH, 200, IDENTIFIERS_RESPONSE) + "?source=1";

		Assert.assertEquals(Arrays.asList("1234A", "1235B"), processor.getIdentifiers(source(url, "user", "pass"), 2));
		Assert.assertEquals("numberToGenerate=2&username=user&password=pass", receivedRequest.get("body"));
	}

	@Test
	public void getIdentifiers_shouldFailIfRemoteServerRespondsWithAnError() throws Exception {
		// a valid body, so that only the status check can cause the failure
		String url = startServer(REST_PATH, 401, IDENTIFIERS_RESPONSE) + "?sourceUuid=" + SOURCE_UUID;
		assertFailsWithCause(source(url, "user", "wrong"), IOException.class, "401");
	}

	@Test
	public void getIdentifiers_shouldOnlyAcceptStatus200FromExportIdentifiersEndpoint() throws Exception {
		String url = startServer(EXPORT_PATH, 201, IDENTIFIERS_RESPONSE) + "?source=1";
		assertFailsWithCause(source(url, "user", "pass"), IOException.class, "201");
	}

	private void assertFailsWithCause(RemoteIdentifierSource source, Class<? extends Exception> causeType, String messageContains) {
		try {
			processor.getIdentifiers(source, 2);
			Assert.fail("Expected getIdentifiers to fail");
		}
		catch (RuntimeException e) {
			Assert.assertTrue("Unexpected cause: " + e.getCause(), causeType.isInstance(e.getCause()));
			if (messageContains != null) {
				Assert.assertTrue(e.getCause().getMessage(), e.getCause().getMessage().contains(messageContains));
			}
		}
	}

	/**
	 * Starts a server on a free port that records the request it receives and responds with the given status and body
	 * @return the url of the given path on the server
	 */
	private String startServer(String path, final int status, final String responseBody) throws Exception {
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext(path, exchange -> {
			receivedRequest.put("method", exchange.getRequestMethod());
			receivedRequest.put("uri", exchange.getRequestURI().toString());
			receivedRequest.put("authorization", exchange.getRequestHeaders().getFirst("Authorization"));
			receivedRequest.put("accept", exchange.getRequestHeaders().getFirst("Accept"));
			receivedRequest.put("contentType", exchange.getRequestHeaders().getFirst("Content-Type"));
			receivedRequest.put("body", read(exchange.getRequestBody()));
			byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(status, response.length == 0 ? -1 : response.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(response);
			}
		});
		server.start();
		return "http://localhost:" + server.getAddress().getPort() + path;
	}

	private RemoteIdentifierSource source(String url, String user, String password) {
		RemoteIdentifierSource source = new RemoteIdentifierSource();
		source.setUrl(url);
		source.setUser(user);
		source.setPassword(password);
		return source;
	}

	private String base64(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private String read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int n = in.read(buffer); n != -1; n = in.read(buffer)) {
			out.write(buffer, 0, n);
		}
		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}
}
