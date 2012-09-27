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

/**
 * Identifier Source which operates via HTTP access of a remote Identifier Source.
 */
public class RemoteIdentifierSource extends BaseIdentifierSource {

	//***** PROPERTIES *****
	
    private String url; // The URL to connect to
    private String user;
    private String password;

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
     *
     * @param user to connect in the remote source
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *
     * @param password to connect in the remote source
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return the user of the remote source
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @return the password of the remote source
     */
    public String getPassword() {
        return password;
    }
}
