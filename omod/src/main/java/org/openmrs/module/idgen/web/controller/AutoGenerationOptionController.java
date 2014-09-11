package org.openmrs.module.idgen.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.propertyeditor.AutoGenerationOptionEditor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

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
        binder.registerCustomEditor(AutoGenerationOption.class, new AutoGenerationOptionEditor());
	}
	
    /**
     * Edit a new or existing AutoGeneration Option
     */
    @RequestMapping("/module/idgen/editAutoGenerationOption")
    public void editAutoGenerationOption(ModelMap model, HttpServletRequest request,
    							     @RequestParam(required=false, value="autoGenerationOption") AutoGenerationOption option,   // expects to get either an option or an identifier type
                                     @RequestParam(required=false, value="identifierType") PatientIdentifierType type) {
    	
		if (Context.isAuthenticated()) {
			Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());

            IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
			LocationService locationService = Context.getLocationService();

            if (option == null) {
                option = new AutoGenerationOption(type);
            }

			model.addAttribute("option", option);
			model.addAttribute("availableSources", iss.getIdentifierSourcesByType(true).get(option.getIdentifierType()));
		    model.addAttribute("availableLocations", locationService.getAllLocations());
        }
    }
    
    /**
     * Retrieves all AutoGenerationOptions
     */
    @RequestMapping("/module/idgen/manageAutoGenerationOptions")
    public void manageAutoGenerationOptions(ModelMap model) {
		if (Context.isAuthenticated()) {

            IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
			Map<PatientIdentifierType,List<AutoGenerationOption>> optionMap = new HashMap<PatientIdentifierType, List<AutoGenerationOption>>();
			List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
            Map<PatientIdentifierType, List<IdentifierSource>> availableSources = iss.getIdentifierSourcesByType(false);

			for (PatientIdentifierType pit : Context.getPatientService().getAllPatientIdentifierTypes()) {
				List<AutoGenerationOption> options = iss.getAutoGenerationOptions(pit);

                if (options != null && options.size() > 0) {
		            optionMap.put(pit, options);
                }

                if (availableSources.get(pit) != null && availableSources.get(pit).size() > 0) {
                    identifierTypes.add(pit);
                }
            }

			model.addAttribute("optionMap", optionMap);
			model.addAttribute("identifierTypes", identifierTypes);
		}
    }
    
    /**
     * Saves an AutoGenerationOption
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

    /**
     * Delete an existing AutoGeneration Option
     */
    @RequestMapping("/module/idgen/deleteAutoGenerationOption")
    public ModelAndView deleteAutoGenerationOption(ModelMap model, HttpServletRequest request,
                                         @RequestParam("autoGenerationOption") AutoGenerationOption option) {
        if (Context.isAuthenticated()) {
            Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
            Context.getService(IdentifierSourceService.class).purgeAutoGenerationOption(option);

        }

        // just display the edit page again
        return new ModelAndView("redirect:/module/idgen/manageAutoGenerationOptions.form");
    }
}
