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
package org.openmrs.module.idgen.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.util.StringUtils;

/**
 * Property Editor Support for Identifier Sources
 */
public class IdentifierSourceEditor extends PropertyEditorSupport {
	
	protected static Log log = LogFactory.getLog(IdentifierSourceEditor.class);
	
	/**
	 * Default Constructor
	 */
	public IdentifierSourceEditor() { }
	
	/**
	 * @see PropertyEditorSupport#setAsText(String)
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
				setValue(iss.getIdentifierSource(Integer.valueOf(text)));
			}
			catch (Exception e) {
				throw new IllegalArgumentException("IdentifierSource not found for " + text, e);
			}
		} 
		else {
			setValue(null);
		}
	}
	
	/**
	 * @see PropertyEditorSupport#getAsText()
	 */
	public String getAsText() {
		IdentifierSource s = (IdentifierSource) getValue();
		if (s != null) {
			return s.getId().toString();
		}
		return null;
	}
}
