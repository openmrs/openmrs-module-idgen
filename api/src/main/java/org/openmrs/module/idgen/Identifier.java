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
 * This is a dummy wrapper class for an identifier {@link String}
 * It's required for the {@link IdentifierResource} to compile when extending {@link DelegatingSubResource} as required to include 
 * the {@link IdentifierResource} in the Swagger docs
 * 
 * @since 4.6.0
 */
public class Identifier {
	
	private String identifierValue;

	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}

}
