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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.LocationUtility;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.controller.patient.ShortPatientFormController;
import org.openmrs.web.controller.patient.ShortPatientModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * This controller is here to override the core create/edit patient form, it delegates to methods in
 * the core controller and plugs in support for auto generated patientIds
 * 
 * @see org.openmrs.web.controller.patient.ShortPatientFormController
 */
@Controller
@SessionAttributes({ "patientModel", "relationshipsMap", "identifierTypes", "locations", "personNameCache",
        "personAddressCache" })
public class PatientFormController {
	
	/** Logger for this class and subclasses */
	private static final Log log = LogFactory.getLog(PatientFormController.class);
	
	private static final String IDGEN_FORM_VIEW_PAGE = "module/idgen/editPatientForm";
	
	@Autowired
	ShortPatientFormController shortPatientFormController;
	
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(@RequestParam(value = "patientId", required = false) Integer patientId, ModelMap model,
	                       WebRequest request) {
		
		//delegate to the core controller so that the session data is added
		shortPatientFormController.showForm(patientId, model, request);
		
		ShortPatientModel patientModel = ((ShortPatientModel) model.get("patientModel"));
		List<PatientIdentifier> identifiers = patientModel.getIdentifiers();
		
		String idTypes = Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_IDENTIFIER_IMPORTANT_TYPES);
		//if this patient already has some identifiers, donot add the new empty one
		if (CollectionUtils.isEmpty(identifiers) && StringUtils.isNotEmpty(idTypes)) {
			for (String split : idTypes.split(",")) {
				String[] typeAndLoc = split.split(":");
				boolean found = false;
				if (identifiers != null) {
					for (PatientIdentifier pi : identifiers) {
						if (typeAndLoc[0].trim().equalsIgnoreCase(pi.getIdentifierType().getName())) {
							found = true;
						}
					}
				}
				if (!found) {
					PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierTypeByName(
					    typeAndLoc[0].trim());
					Location idLoc = (LocationUtility.getUserDefaultLocation() != null) ? LocationUtility
					        .getUserDefaultLocation() : LocationUtility.getDefaultLocation();
					
					if (identifiers == null)
						identifiers = new ArrayList<PatientIdentifier>();
					identifiers.add(new PatientIdentifier("", idType, idLoc));
				}
			}
			
			patientModel.setIdentifiers(identifiers);
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
		
		model.put("autoGenerationOptions", autogen);
		
		return IDGEN_FORM_VIEW_PAGE;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String saveShortPatient(WebRequest request, @ModelAttribute("personNameCache") PersonName personNameCache,
	                               @ModelAttribute("personAddressCache") PersonAddress personAddressCache,
	                               @ModelAttribute("patientModel") ShortPatientModel patientModel, BindingResult result,
	                               SessionStatus status) {
		
		String redirectFromCoreController = shortPatientFormController.saveShortPatient(request, personNameCache,
		    personAddressCache, patientModel, result, status);
		
		if (result.hasErrors())
			return IDGEN_FORM_VIEW_PAGE;
		
		//This is always expected to be the patientDashboard url
		return redirectFromCoreController;
	}
	
}
