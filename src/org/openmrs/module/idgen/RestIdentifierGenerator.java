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

import java.util.List;

/**
 * Identifier Source which operates via REST access of a remote Identifier Source
 */
public class RestIdentifierGenerator extends BaseIdentifierSource {
	
	//***** PROPERTIES *****
	
    private String url; // The URL to connect to
    private String username; // The username, if required
    private String password; // The password, if required
	
    //***** INSTANCE METHODS *****
    
	/** 
	 * @see IdentifierSource#getIdentifier()
	 */
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see IdentifierSource#getIdentifiers(int)
	 */
	public List<String> getIdentifiers(int batchSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see IdentifierSource#isValid(java.lang.String)
	 */
	public boolean isValid(String identifier) {
		// TODO Auto-generated method stub
		return false;
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

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
