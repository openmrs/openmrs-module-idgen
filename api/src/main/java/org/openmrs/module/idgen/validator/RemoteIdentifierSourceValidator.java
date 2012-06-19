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
package org.openmrs.module.idgen.validator;

import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates an IdentifierSource
 */
public class RemoteIdentifierSourceValidator extends IdentifierSourceValidator {
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return RemoteIdentifierSource.class.isAssignableFrom(clazz);
	}

	/** 
	 * @see Validator#validate(Object, Errors)
	 */
	public void validate(Object o, Errors errors) {
		RemoteIdentifierSource source = (RemoteIdentifierSource)o;
		
		// Url is required
		if (!StringUtils.hasText(source.getUrl())) {
			errors.reject("Url is required");
		}
	}
}
