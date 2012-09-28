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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;

import javax.net.ssl.HttpsURLConnection;

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

        String urlFromRemoteSource = buildUrl(remoteIdentifierSource);
        String urlParameters = buildParameters(remoteIdentifierSource, batchSize);
        List<String>  idsList = new ArrayList<String>();

        BufferedReader bufferedReader = null;
        DataOutputStream outputStream = null;
        HttpsURLConnection connection = null;

        try {
            URL url = new URL(urlFromRemoteSource);
            connection = generateConnection(urlParameters, url);

            outputStream = createOutputStream(urlParameters, connection);

            bufferedReader = new BufferedReader(getInputStreamReaderFrom(connection));
            String line;

            while ((line = bufferedReader.readLine()) != null){
                idsList.add(line.trim());
            }

        } catch (IOException e) {
           log.error("Exception when trying to connect in the remote server to get more ids", e);
        } finally {
            try {
                bufferedReader.close();
                outputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                log.error("Exception when trying to connect in the remote server to get more ids", e);
            }

        }

        return Collections.unmodifiableList(idsList);
    }

    protected DataOutputStream createOutputStream(String urlParameters, HttpsURLConnection connection) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(urlParameters);
        outputStream.flush();
        return outputStream;
    }

    protected InputStreamReader getInputStreamReaderFrom(HttpsURLConnection connection) throws IOException {
        return new InputStreamReader(connection.getInputStream());
    }

    protected HttpsURLConnection generateConnection(String urlParameters, URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
        connection.setUseCaches (false);
        return connection;
    }

    private String buildUrl(RemoteIdentifierSource remoteSource) {
        return remoteSource.getUrl();
    }

    private String buildParameters(RemoteIdentifierSource remoteSource, Integer batchSize) {
        return "username=" + remoteSource.getUser() + "&password=" + remoteSource.getPassword() + "&numberToGenerate=" + batchSize;
    }


}