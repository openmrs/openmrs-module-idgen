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
package org.openmrs.module.idgen.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.controller.patient.NewPatientFormController;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * This controller is here to override the core create/edit patient form
 * @see org.openmrs.web.controller.patient.NewPatientFormController
 */
public class PatientFormController extends NewPatientFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see SimpleFormController#referenceData(HttpServletRequest)
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = super.referenceData(request);
		
		Location defaultLocation = Context.getLocationService().getDefaultLocation();
		if (defaultLocation == null) {
			defaultLocation = Context.getLocationService().getAllLocations().get(0);
		}
		
		Set<PatientIdentifier> identifiers = (Set<PatientIdentifier>)map.get("identifiers");
		String idTypes = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_IDENTIFIER_IMPORTANT_TYPES);
		if (StringUtils.isNotEmpty(idTypes)) {
			for (String split : idTypes.split(",")) {
				String[] typeAndLoc = split.split(":");
				boolean found = false;
				if (identifiers != null) {
					for (PatientIdentifier pi : identifiers) {
						if (pi.getIdentifierType() != null && typeAndLoc[0].equalsIgnoreCase(pi.getIdentifierType().getName())) {
							found = true;
						}
					}
				}
				if (!found) {
					PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierTypeByName(typeAndLoc[0]);
					Location idLoc = defaultLocation;
					if (typeAndLoc.length == 2 && StringUtils.isNotEmpty(typeAndLoc[1])) {
						idLoc = Context.getLocationService().getLocation(typeAndLoc[1]);
					}
					identifiers.add(new PatientIdentifier("", idType, idLoc));
				}
			}
			map.put("identifiers", identifiers);
		}
		
		// Identifier Generation
		IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
		Map<PatientIdentifierType, AutoGenerationOption> autogen = new HashMap<PatientIdentifierType, AutoGenerationOption>();
 		for (PatientIdentifierType pit : Context.getPatientService().getAllPatientIdentifierTypes()) {
 			AutoGenerationOption option = iss.getAutoGenerationOption(pit);
 			if (option != null && option.isAutomaticGenerationEnabled()) {
 				autogen.put(pit, option);
 			}
 		}
 		map.put("autoGenerationOptions", autogen);

		return map;
	}
}
