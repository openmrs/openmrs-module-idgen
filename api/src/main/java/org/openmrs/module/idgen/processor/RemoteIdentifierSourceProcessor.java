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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifiersMessage;

/**
 * Evaluates a RemoteIdentifierSource
 * By default, this expects an HTTP request to return a comma-separated String of identifiers.
 * This can be overridden in subclasses as needed
 */
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
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("numberToGenerate", Integer.toString(batchSize)));
        if (StringUtils.isNotBlank(source.getUser())) {
            nameValuePairs.add(new BasicNameValuePair("username", source.getUser()));
            nameValuePairs.add(new BasicNameValuePair("password", source.getPassword()));
        }

        HttpPost post = new HttpPost(source.getUrl());
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

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

        if (statusCode != 200) {
            throw new IOException("Unexpected response: " + httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase() + "\n" + responseText);
        }

        return responseText;
    }

}