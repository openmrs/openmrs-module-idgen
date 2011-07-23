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
package org.openmrs.module.idgen.web.extension;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.WebConstants;

/**
 * Provides additional Header Columns to the edit identifier table
 */
public class IdentifierTableHeaderExtension extends Extension {

	/**
	 * @see Extension#getMediaType()
	 */
	@Override
	public MEDIA_TYPE getMediaType() {
		return MEDIA_TYPE.html;
	}

	/**
	 * @see Extension#getOverrideContent()
	 */
	@Override
	public String getOverrideContent(String bodyContent) {
		
		IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
		Map<PatientIdentifierType, AutoGenerationOption> autogen = new HashMap<PatientIdentifierType, AutoGenerationOption>();
 		for (PatientIdentifierType pit : Context.getPatientService().getAllPatientIdentifierTypes()) {
 			AutoGenerationOption option = iss.getAutoGenerationOption(pit);
 			if (option != null && option.isAutomaticGenerationEnabled()) {
 				autogen.put(pit, option);
 			}
 		}

 		StringBuilder sb = new StringBuilder();
 		if (!autogen.isEmpty()) {
 	 		if (OpenmrsConstants.OPENMRS_VERSION_SHORT.compareTo("1.7") < 0) {
 	 			sb.append("<script src=\"/" + WebConstants.WEBAPP_NAME + "/moduleResources/idgen/jquery-1.3.2.min.js\" type=\"text/javascript\"></script>\n");
 	 		}
 	 		sb.append("<link href=\"/" + WebConstants.WEBAPP_NAME + "/moduleResources/idgen/editPatientIdentifiers.css\" type=\"text/css\" rel=\"stylesheet\"\n/>");
	 		sb.append("<script src=\"/" + WebConstants.WEBAPP_NAME + "/moduleResources/idgen/editPatientIdentifiers.js\" type=\"text/javascript\"></script>\n"); 		
	 		sb.append("<td id=\"idgenColumnHeader\">" + Context.getMessageSourceService().getMessage("idgen.autoGenerate") + "</td>");
 		}

 		return sb.toString();
	}
}
