/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.web.extension;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.WebConstants;
import org.springframework.util.StringUtils;

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
 			// solving IDGEN-11: when webapp_name is empty, double slashes cause wrong url
 			// in 1.10+, use WebUtil.getContextPath(); See TRUNK-
 			String contextPath = "";
 			if (StringUtils.hasLength(WebConstants.WEBAPP_NAME)) {
 				contextPath += "/" + WebConstants.WEBAPP_NAME;
 			}
 			
 	 		if (ModuleUtil.compareVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, "1.7") < 0) {
 	 			sb.append("<script src=\"" + contextPath + "/moduleResources/idgen/jquery-1.3.2.min.js\" type=\"text/javascript\"></script>\n");
 	 		}
 	 		if (ModuleUtil.compareVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, "1.8") < 0) {
 	 			sb.append("<script src=\"" + contextPath + "/moduleResources/idgen/newPatientFormExtensions.js\" type=\"text/javascript\"></script>\n"); 		
 	 		}
 	 		else {
 	 			sb.append("<script src=\"" + contextPath + "/moduleResources/idgen/shortPatientFormExtensions.js\" type=\"text/javascript\"></script>\n");			
 	 		}
	 		sb.append("<link href=\"" + contextPath + "/moduleResources/idgen/editPatientIdentifiers.css\" type=\"text/css\" rel=\"stylesheet\"\n/>");
 	 		sb.append("<td id=\"idgenColumnHeader\">" + Context.getMessageSourceService().getMessage("idgen.autoGenerate") + "</td>");
 		}

 		return sb.toString();
	}
}
