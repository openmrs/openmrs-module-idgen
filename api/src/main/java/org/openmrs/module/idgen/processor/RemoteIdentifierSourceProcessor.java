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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
        HttpPost post = buildHttpPost(source, batchSize);

        HttpClient client = new DefaultHttpClient();
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
        }

        // the REST API responds to a successful request with 201 Created
        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("Unexpected response: " + httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase() + "\n" + responseText);
        }

        return responseText;
    }

    /**
     * Builds the request for a batch of identifiers from the source's url.
     * A url to the REST API (eg. .../ws/rest/v1/idgen/identifiersource?sourceUuid=...&comment=...) is sent a JSON body,
     * taking the sourceUuid and comment from the url, with the user and password in a Basic Authorization header.
     * Any other url (eg. .../module/idgen/exportIdentifiers.form?source=...) is sent a form post, with the user and
     * password as parameters.
     */
    protected HttpPost buildHttpPost(RemoteIdentifierSource source, int batchSize) throws IOException {
        if (isRestUrl(source.getUrl())) {
            return buildRestHttpPost(source, batchSize);
        }

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("numberToGenerate", Integer.toString(batchSize)));
        if (StringUtils.isNotBlank(source.getUser())) {
            nameValuePairs.add(new BasicNameValuePair("username", source.getUser()));
            nameValuePairs.add(new BasicNameValuePair("password", source.getPassword()));
        }

        HttpPost post = new HttpPost(source.getUrl());
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        return post;
    }

    protected HttpPost buildRestHttpPost(RemoteIdentifierSource source, int batchSize) throws IOException {
        Map<String, String> parameters = new HashMap<String, String>();
        for (NameValuePair parameter : URLEncodedUtils.parse(URI.create(source.getUrl()), StandardCharsets.UTF_8)) {
            parameters.put(parameter.getName(), parameter.getValue());
        }
        String sourceUuid = parameters.get("sourceUuid");
        if (StringUtils.isBlank(sourceUuid)) {
            throw new IllegalArgumentException("The url of a remote identifier source using the REST API must include a sourceUuid parameter: " + source.getUrl());
        }

        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("generateIdentifiers", true);
        body.put("sourceUuid", sourceUuid);
        body.put("numberToGenerate", batchSize);
        if (StringUtils.isNotBlank(parameters.get("comment"))) {
            body.put("comment", parameters.get("comment"));
        }

        HttpPost post = new HttpPost(StringUtils.substringBefore(source.getUrl(), "?"));
        post.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(body), ContentType.APPLICATION_JSON));
        if (StringUtils.isNotBlank(source.getUser())) {
            String credentials = source.getUser() + ":" + StringUtils.defaultString(source.getPassword());
            post.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        }
        return post;
    }

    protected boolean isRestUrl(String url) {
        try {
            String path = URI.create(url).getPath();
            return path != null && path.contains("/ws/rest/");
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

}