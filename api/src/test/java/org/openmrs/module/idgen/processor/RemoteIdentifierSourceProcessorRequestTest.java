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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.RemoteIdentifierSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteIdentifierSourceProcessorRequestTest {

	private static final String SOURCE_UUID = "52250ca2-d25f-11e4-8dbf-54ee7513a7ff";

	private RemoteIdentifierSourceProcessor processor;

	private HttpServer server;

	private Map<String, String> receivedRequest;

	@Before
	public void setUp() {
		processor = new RemoteIdentifierSourceProcessor();
		receivedRequest = new HashMap<String, String>();
	}

	@After
	public void tearDown() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	public void buildHttpPost_shouldPostFormWithCredentialsForExportIdentifiersUrl() throws Exception {
		String url = "http://localhost/openmrs/module/idgen/exportIdentifiers.form?source=1&comment=Site";
		HttpPost post = processor.buildHttpPost(source(url, "user", "pass"), 5);

		Assert.assertEquals(url, post.getURI().toString());
		Assert.assertNull(post.getFirstHeader("Authorization"));
		Assert.assertEquals("numberToGenerate=5&username=user&password=pass", EntityUtils.toString(post.getEntity()));
	}

	@Test
	public void buildHttpPost_shouldPostJsonWithBasicAuthenticationForRestUrl() throws Exception {
		String url = "https://localhost/openmrs/ws/rest/v1/idgen/identifiersource?sourceUuid=" + SOURCE_UUID + "&comment=Site+One";
		HttpPost post = processor.buildHttpPost(source(url, "user", "pass"), 5);

		Assert.assertEquals("https://localhost/openmrs/ws/rest/v1/idgen/identifiersource", post.getURI().toString());
		Assert.assertEquals("Basic " + base64("user:pass"), post.getFirstHeader("Authorization").getValue());
		Assert.assertTrue(post.getEntity().getContentType().getValue().startsWith("application/json"));

		Map<String, Object> expectedBody = new HashMap<String, Object>();
		expectedBody.put("generateIdentifiers", true);
		expectedBody.put("sourceUuid", SOURCE_UUID);
		expectedBody.put("numberToGenerate", 5);
		expectedBody.put("comment", "Site One");
		Assert.assertEquals(expectedBody, new ObjectMapper().readValue(EntityUtils.toString(post.getEntity()), Map.class));
	}

	@Test
	public void buildHttpPost_shouldOmitCommentAndAuthorizationForRestUrlIfNotConfigured() throws Exception {
		String url = "https://localhost/openmrs/ws/rest/v1/idgen/identifiersource?sourceUuid=" + SOURCE_UUID;
		HttpPost post = processor.buildHttpPost(source(url, null, null), 5);

		Assert.assertNull(post.getFirstHeader("Authorization"));
		Map<?, ?> body = new ObjectMapper().readValue(EntityUtils.toString(post.getEntity()), Map.class);
		Assert.assertFalse(body.containsKey("comment"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildHttpPost_shouldFailForRestUrlWithoutSourceUuid() throws Exception {
		processor.buildHttpPost(source("https://localhost/openmrs/ws/rest/v1/idgen/identifiersource", "user", "pass"), 5);
	}

	@Test
	public void getIdentifiers_shouldGetIdentifiersFromRestEndpoint() throws Exception {
		String url = startServer(201, "{\"identifiers\":[\"1234A\",\"1235B\"]}") + "?sourceUuid=" + SOURCE_UUID + "&comment=Site";

		List<String> identifiers = processor.getIdentifiers(source(url, "user", "pass"), 2);

		Assert.assertEquals(Arrays.asList("1234A", "1235B"), identifiers);
		Assert.assertEquals("POST", receivedRequest.get("method"));
		Assert.assertEquals("Basic " + base64("user:pass"), receivedRequest.get("authorization"));
		Map<?, ?> body = new ObjectMapper().readValue(receivedRequest.get("body"), Map.class);
		Assert.assertEquals(SOURCE_UUID, body.get("sourceUuid"));
		Assert.assertEquals(2, body.get("numberToGenerate"));
	}

	@Test(expected = RuntimeException.class)
	public void getIdentifiers_shouldFailIfRemoteServerRespondsWithAnError() throws Exception {
		String url = startServer(401, "") + "?sourceUuid=" + SOURCE_UUID;
		processor.getIdentifiers(source(url, "user", "wrong"), 2);
	}

	/**
	 * Starts a server on a free port that records the request it receives and responds with the given status and body
	 * @return the url of its identifier source REST endpoint
	 */
	private String startServer(final int status, final String responseBody) throws Exception {
		String path = "/openmrs/ws/rest/v1/idgen/identifiersource";
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext(path, exchange -> {
			receivedRequest.put("method", exchange.getRequestMethod());
			receivedRequest.put("authorization", exchange.getRequestHeaders().getFirst("Authorization"));
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

	private String read(InputStream in) throws java.io.IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int n = in.read(buffer); n != -1; n = in.read(buffer)) {
			out.write(buffer, 0, n);
		}
		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}
}
