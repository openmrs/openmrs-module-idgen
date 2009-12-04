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
package org.openmrs.module.idgen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Identifier Source which operates via HTTP access of a remote Identifier Source.
 * By default, this expects an HTTP request to return a comma-separated String of identifiers.
 * This can be overridden in subclasses as needed
 */
public class RemoteIdentifierSource extends BaseIdentifierSource {

	//***** PROPERTIES *****
	
    private String url; // The URL to connect to
    
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @return the contents from the supplied URL as a String
	 */
	public String getContentsFromUrl() {
		StringBuilder contents = new StringBuilder();
		BufferedReader r = null;
		try {
			URL u = new URL(getUrl());
			r = new BufferedReader(new InputStreamReader(u.openStream()));
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				contents.append(line);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to connect to url: " + getUrl(), e);
		}
		finally {
			if (r != null) {
				try {
					r.close();
				}
				catch (Exception e) {
					log.warn("Error closing reader: ", e);
				}
			}
		}
		return contents.toString();
	}

	/** 
	 * @see IdentifierSource#nextIdentifier()
	 */
	public String nextIdentifier() {
		return nextIdentifiers(1).get(0);
	}

	/** 
	 * @see BaseIdentifierSource#nextIdentifiers(int)
	 */
	@Override
	public synchronized List<String> nextIdentifiers(int batchSize) {
		String[] split = getContentsFromUrl().split(",");
		return Arrays.asList(split);
    }

	//***** PROPERTY ACCESS *****

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
