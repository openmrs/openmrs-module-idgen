package org.openmrs.module.idgen.web.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.propertyeditor.IdentifierSourceEditor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.propertyeditor.PatientIdentifierTypeEditor;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("option")
public class AutoGenerationOptionController {

	protected static Log log = LogFactory.getLog(AutoGenerationOptionController.class);
	
	/**
	 * Default Constructor
	 */
	public AutoGenerationOptionController() { }
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(PatientIdentifierType.class, new PatientIdentifierTypeEditor());
		binder.registerCustomEditor(IdentifierSource.class, new IdentifierSourceEditor());
	}
	
    /**
     * Edit a new or existing IdentifierSource
     */
    @RequestMapping("/module/idgen/editAutoGenerationOption")
    public void editIdentifierSource(ModelMap model, HttpServletRequest request,
    							     @RequestParam(required=true, value="identifierType") PatientIdentifierType identifierType) {
    	
		if (Context.isAuthenticated()) {
			
			Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
			IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
			AutoGenerationOption option = iss.getAutoGenerationOption(identifierType);
			if (option == null) {
				option = new AutoGenerationOption(identifierType);
			}
			model.addAttribute("option", option);
			model.addAttribute("availableSources", iss.getIdentifierSourcesByType(true).get(identifierType));
		}
    }
    
    /**
     * Retrieves all IdentifierSources
     */
    @RequestMapping("/module/idgen/manageAutoGenerationOptions")
    public void manageAutoGenerationOptions(ModelMap model) {
		if (Context.isAuthenticated()) {
			Map<PatientIdentifierType, AutoGenerationOption> m = new LinkedHashMap<PatientIdentifierType, AutoGenerationOption>();
			IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
			for (PatientIdentifierType pit : Context.getPatientService().getAllPatientIdentifierTypes()) {
				AutoGenerationOption option = iss.getAutoGenerationOption(pit);
				m.put(pit, option);
			}
			model.addAttribute("optionMap", m);
			model.addAttribute("availableSources", iss.getIdentifierSourcesByType(false));
		}
    }
    
    /**
     * Saves an IdentifierSource
     */
    @RequestMapping("/module/idgen/saveAutoGenerationOption")
    public ModelAndView saveAutoGenerationOption(@ModelAttribute("option") AutoGenerationOption option, BindingResult result, SessionStatus status) {
		
    	// TODO: Implement validation here
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/idgen/editAutoGenerationOption");
		}
		
		// add/update the option
		Context.getService(IdentifierSourceService.class).saveAutoGenerationOption(option);
		
		// clears the command object from the session
		status.setComplete();
		
		// just display the edit page again
		return new ModelAndView("redirect:/module/idgen/manageAutoGenerationOptions.form");
	}
}
