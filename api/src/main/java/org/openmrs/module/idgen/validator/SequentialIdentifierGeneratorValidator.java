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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates a SequentialIdentifierGenerator
 */
public class SequentialIdentifierGeneratorValidator extends IdentifierSourceValidator {
	
	protected static Log log = LogFactory.getLog(SequentialIdentifierGeneratorValidator.class);
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return SequentialIdentifierGenerator.class.isAssignableFrom(clazz);
	}

	/** 
	 * @see Validator#validate(Object, Errors)
	 */
	public void validate(Object o, Errors errors) {
		
		super.validate(o, errors);
		
		SequentialIdentifierGenerator source = (SequentialIdentifierGenerator)o;
		
		// FirstIdentifierBase is required
		if (!StringUtils.hasText(source.getFirstIdentifierBase())) {
			errors.reject("First Identifier Base is required");
		}
		
		if (source.getIdentifierType() == null) {
			errors.reject("Identifier Type is required");
		}
		else {
			String prefix = (source.getPrefix() == null ? "" : source.getPrefix());
			String suffix = (source.getSuffix() == null ? "" : source.getSuffix());
			String firstId = prefix + source.getFirstIdentifierBase() + suffix;
			if (StringUtils.hasText(source.getIdentifierType().getValidator())) {
				try {
					Class<?> validatorClass = Context.loadClass(source.getIdentifierType().getValidator());
					IdentifierValidator v = (IdentifierValidator) validatorClass.newInstance();
					firstId = v.getValidIdentifier(firstId);
				}
				catch (UnallowedIdentifierException uie) {
					errors.reject("Invalid identifier. " + uie.getMessage() + "");
				}
				catch (Exception e) {
					log.error("Error loading validator class " + source.getIdentifierType().getValidator(), e);
					errors.reject("Validator named " + source.getIdentifierType().getValidator() + " cannot be loaded");
				}
			}
			if (source.getMinLength() != null && source.getMinLength() > 0) {
				if (source.getMinLength() > firstId.length()) {
					errors.reject("Invalid configuration. First identifier generated would be '" + firstId + "' which is shorter than minimum length of " + source.getMinLength());
				}
			}
			if (source.getMaxLength() != null && source.getMaxLength() > 0) {
				if (source.getMaxLength() < firstId.length()) {
					errors.reject("Invalid configuration. First identifier generated would be '" + firstId + "' which exceeds maximum length of " + source.getMaxLength());
				}
			}
		}
	}
}
