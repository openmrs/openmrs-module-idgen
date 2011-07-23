package org.openmrs.module.idgen.web.controller;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IdgenEditPatientIdentifiersController {

	protected static Log log = LogFactory.getLog(LogEntryController.class);
	
	/**
	 * Default Constructor
	 */
	public IdgenEditPatientIdentifiersController() { }
	
	/**
	 * Edit Identifiers Section
	 */
    @RequestMapping("/module/idgen/editPatientIdentifiers.form")
    public void editPatientIdentifiers(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    						   @RequestParam(required=false, value="patientId") Integer patientId) throws Exception {
    	
    	Map<String, Object> m = new HashMap<String, Object>();
    	
    	Map<String, String> translations = new HashMap<String, String>();
    	translations.put("add", Context.getMessageSourceService().getMessage("idgen.addNewIdentifier"));
    	m.put("translations", translations);
    	
    	Map<PatientIdentifierType, Location> importantTypes = getImportantTypes();
    	
		// All Patient Identifiers and Generation Options
		Map<String, Map<String, Object>> allIdentifiers = new LinkedHashMap<String, Map<String, Object>>();
 		for (PatientIdentifierType type : Context.getPatientService().getAllPatientIdentifierTypes()) {
    		Map<String, Object> typeData = new HashMap<String, Object>();
    		typeData.put("typeId", type.getPatientIdentifierTypeId());
    		typeData.put("typeName", type.getName());
    		typeData.put("manualEntryEnabled", true);
    		typeData.put("autogenerationEnabled", false);
    		typeData.put("sourceId", "");
    		typeData.put("location", importantTypes.get(type) == null ? "" : importantTypes.get(type).getLocationId());
    		typeData.put("identifier", "");
    		typeData.put("preferred", false);
    		typeData.put("saved", false);

 			AutoGenerationOption option = Context.getService(IdentifierSourceService.class).getAutoGenerationOption(type);
 			if (option != null && option.isAutomaticGenerationEnabled()) {
 				typeData.put("manualEntryEnabled", option.isManualEntryEnabled());
 				typeData.put("autogenerationEnabled", option.isAutomaticGenerationEnabled());
 				typeData.put("sourceId", option.getSource() == null ? "" : option.getSource().getId());
 			}
 			allIdentifiers.put(type.getPatientIdentifierTypeId().toString(), typeData);
 		}
 		m.put("allIdentifiers", allIdentifiers);
    	
 		// Default Patient Identifiers To Display
    	List<Map<String, Object>> defaultIdentifiers = new ArrayList<Map<String, Object>>();
    	if (patientId != null) {
    		Patient patient = Context.getPatientService().getPatient(patientId);
    		for (PatientIdentifier pi : patient.getActiveIdentifiers()) {
        		Map<String, Object> rowData = new HashMap<String, Object>();
        		rowData.putAll(allIdentifiers.get(pi.getIdentifierType().getPatientIdentifierTypeId().toString()));
        		rowData.put("location", pi.getLocation() == null ? "" : pi.getLocation().getLocationId());
        		rowData.put("identifier", pi.getIdentifier());
        		rowData.put("preferred", pi.getPreferred());
        		rowData.put("saved", true);
        		defaultIdentifiers.add(rowData);
        		importantTypes.remove(pi.getIdentifierType());
    		}
    	}
		
		for (PatientIdentifierType type : importantTypes.keySet()) {
    		Map<String, Object> rowData = new HashMap<String, Object>(allIdentifiers.get(type.getPatientIdentifierTypeId().toString()));
    		defaultIdentifiers.add(rowData);
		}
		m.put("defaultIdentifiers", defaultIdentifiers);

 		response.setContentType("text/json");
		ObjectMapper mapper = new ObjectMapper();		
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, m);
		response.getWriter().write(sw.toString());
    }
    
    public Map<PatientIdentifierType, Location> getImportantTypes() {
		Map<PatientIdentifierType, Location> importantTypes = new LinkedHashMap<PatientIdentifierType, Location>();
		String idTypes = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_IDENTIFIER_IMPORTANT_TYPES);
		if (StringUtils.isNotEmpty(idTypes)) {
			Location defaultLocation = Context.getLocationService().getDefaultLocation();
			if (defaultLocation == null) {
				defaultLocation = Context.getLocationService().getAllLocations().get(0);
			}
			for (String split : idTypes.split(",")) {
				String[] typeAndLoc = split.split(":");
				PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierTypeByName(typeAndLoc[0]);
				Location idLoc = defaultLocation;
				if (typeAndLoc.length == 2 && StringUtils.isNotEmpty(typeAndLoc[1])) {
					idLoc = Context.getLocationService().getLocation(typeAndLoc[1]);
				}
				importantTypes.put(idType, idLoc);
			}
		}
		return importantTypes;
    }
}
