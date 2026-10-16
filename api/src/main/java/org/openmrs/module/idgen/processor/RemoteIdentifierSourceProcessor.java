/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.processor;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifiersMessage;
import org.springframework.stereotype.Component;

/**
 * Evaluates a RemoteIdentifierSource
 * By default, this expects an HTTP request to return a comma-separated String of identifiers.
 * This can be overridden in subclasses as needed
 */
@Component
public class RemoteIdentifierSourceProcessor implements IdentifierSourceProcessor {

    private static Log log = LogFactory.getLog(RemoteIdentifierSourceProcessor.class);

    private static final int CONNECT_TIMEOUT_MS = 30000;

    private static final int SOCKET_TIMEOUT_MS = 300000;

    /**
     * @see IdentifierSourceProcessor#getIdentifiers(IdentifierSource, int)
     */
    @Override
    public List<String> getIdentifiers(IdentifierSource source, int batchSize) {
        RemoteIdentifierSource remoteIdentifierSource = (RemoteIdentifierSource) source;
        String response;
        try {
            response = doHttpPost(remoteIdentifierSource, batchSize);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        try {
        	ObjectMapper mapper = new ObjectMapper();
        	RemoteIdentifiersMessage message = mapper.readValue(response, RemoteIdentifiersMessage.class);
        	return message.getIdentifiers();
        }
        catch (IOException ex) {
        	throw new RuntimeException("Unexpected response: " + response, ex);
        }
    }

    protected String doHttpPost(RemoteIdentifierSource source, int batchSize) throws IOException {
        if (isRestUrl(source.getUrl())) {
            return doRestHttpPost(source, batchSize);
        }

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("numberToGenerate", Integer.toString(batchSize)));
        if (StringUtils.isNotBlank(source.getUser())) {
            nameValuePairs.add(new BasicNameValuePair("username", source.getUser()));
            nameValuePairs.add(new BasicNameValuePair("password", source.getPassword()));
        }

        HttpPost post = new HttpPost(source.getUrl());
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNECT_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(client.getParams(), SOCKET_TIMEOUT_MS);
        HttpResponse httpResponse;
        String responseText;
        Integer statusCode;

        try {
            httpResponse = client.execute(post);
            responseText = EntityUtils.toString(httpResponse.getEntity());
            statusCode = httpResponse.getStatusLine().getStatusCode();
        }
        finally {
            // always release the connection!
            post.releaseConnection();
            client.getConnectionManager().shutdown();
        }

        if (statusCode != 200) {
            throw new IOException("Unexpected response: " + httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase() + "\n" + responseText);
        }

        return responseText;
    }

    /**
     * Requests the batch from the REST API (eg. .../ws/rest/v1/idgen/identifiersource?sourceUuid=...&comment=...),
     * posting a JSON body that takes the sourceUuid and comment from the url (any other query parameters are not sent),
     * with the user and password in a Basic Authorization header.
     * Unlike exportIdentifiers.form, this uses HttpClient's current defaults (eg. strict hostname verification)
     */
    protected String doRestHttpPost(RemoteIdentifierSource source, int batchSize) throws IOException {
        URI uri;
        try {
            uri = URI.create(source.getUrl());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The url of a remote identifier source is not valid (are its parameters url-encoded?): " + source.getUrl(), e);
        }
        Map<String, String> parameters = new HashMap<>();
        for (NameValuePair parameter : URLEncodedUtils.parse(uri, StandardCharsets.UTF_8)) {
            parameters.put(parameter.getName(), parameter.getValue());
        }
        String sourceUuid = parameters.get("sourceUuid");
        if (StringUtils.isBlank(sourceUuid)) {
            throw new IllegalArgumentException("The url of a remote identifier source using the REST API must include a sourceUuid parameter: " + source.getUrl());
        }
        String comment = parameters.get("comment");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("generateIdentifiers", true);
        body.put("sourceUuid", sourceUuid);
        body.put("numberToGenerate", batchSize);
        if (StringUtils.isNotBlank(comment)) {
            body.put("comment", comment);
        }

        HttpPost post = new HttpPost(StringUtils.substringBefore(source.getUrl(), "?"));
        post.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(body), ContentType.APPLICATION_JSON));
        post.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        if (StringUtils.isNotBlank(source.getUser())) {
            String credentials = source.getUser() + ":" + StringUtils.defaultString(source.getPassword());
            post.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        }

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT_MS)
                .setConnectionRequestTimeout(CONNECT_TIMEOUT_MS)
                .setSocketTimeout(SOCKET_TIMEOUT_MS)
                .build();

        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
             CloseableHttpResponse httpResponse = client.execute(post)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String responseText = httpResponse.getEntity() == null ? "" : EntityUtils.toString(httpResponse.getEntity());
            // the REST API responds to a successful request with 201 Created
            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("Unexpected response: " + statusCode + " " + httpResponse.getStatusLine().getReasonPhrase() + "\n" + responseText);
            }
            return responseText;
        }
    }

    /**
     * @return true if the url is to the REST API.  Checked on the url's text, before any query string, so that a REST
     * url that isn't valid fails with an error about that rather than being sent as a form post
     */
    protected boolean isRestUrl(String url) {
        return url != null && StringUtils.substringBefore(url, "?").contains("/ws/rest/");
    }

}