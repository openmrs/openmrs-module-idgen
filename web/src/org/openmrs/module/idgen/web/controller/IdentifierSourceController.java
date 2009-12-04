package org.openmrs.module.idgen.web.controller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("source")
public class IdentifierSourceController {

	protected static Log log = LogFactory.getLog(IdentifierSourceController.class);
	
	/**
	 * Default Constructor
	 */
	public IdentifierSourceController() { }
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(PatientIdentifierType.class, new PatientIdentifierTypeEditor());
		binder.registerCustomEditor(IdentifierSource.class, new IdentifierSourceEditor());
	}
	
    /**
     * Edit a new or existing IdentifierSource
     */
    @RequestMapping("/module/idgen/editIdentifierSource")
    public void editIdentifierSource(ModelMap model, HttpServletRequest request,
    							     @RequestParam(required=false, value="source") IdentifierSource source,
    							     @RequestParam(required=false, value="identifierType") PatientIdentifierType identifierType,
    							     @RequestParam(required=false, value="sourceType") String sourceType) {
    	
		if (Context.isAuthenticated()) {
			
			Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
			if (source == null) {
				try {
					Class<?> idSourceType = Context.loadClass(sourceType);
					model.addAttribute("sourceType", sourceType);
					source = (IdentifierSource)idSourceType.newInstance();
					source.setIdentifierType(identifierType);
				}
				catch (Exception e) {
					throw new RuntimeException("Unable to instantiate class " + sourceType, e);
				}
			}
			model.addAttribute("source", source);
			
			List<IdentifierSource> otherCompatibleSources = new ArrayList<IdentifierSource>();
			for (IdentifierSource s : Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false)) {
				if (!s.equals(source) && s.getIdentifierType().equals(source.getIdentifierType())) {
					otherCompatibleSources.add(s);			
				}
			}
			model.addAttribute("otherCompatibleSources", otherCompatibleSources);
		}
    }
    
    /**
     * Retrieves all IdentifierSources
     */
    @RequestMapping("/module/idgen/manageIdentifierSources")
    public void manageIdentifierSources(ModelMap model, 
    									@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
		if (Context.isAuthenticated()) {
			IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
			boolean ret = includeRetired == Boolean.TRUE;
			
			Map<PatientIdentifierType, List<IdentifierSource>> sourcesByType = iss.getIdentifierSourcesByType(ret);
			
			List<PatientIdentifierType> identiferTypes = new ArrayList<PatientIdentifierType>();
			for (Iterator<PatientIdentifierType> i = sourcesByType.keySet().iterator(); i.hasNext();) {
				PatientIdentifierType pit = i.next();
				if (sourcesByType.get(pit).isEmpty()) {
					i.remove();
				}
				identiferTypes.add(pit);
			}
			model.addAttribute("sourcesByType", sourcesByType);
			model.addAttribute("identiferTypes", identiferTypes);
			model.addAttribute("sourceTypes", iss.getIdentifierSourceTypes());
		}
    }
    
    /**
     * Deletes an IdentifierSource
     */
    @RequestMapping("/module/idgen/deleteIdentifierSource")
    public String deletePatientSearch(ModelMap model, @RequestParam(required=true, value="source") IdentifierSource source) {
    	Context.getService(IdentifierSourceService.class).purgeIdentifierSource(source);
    	return "redirect:/module/idgen/manageIdentifierSources.list";
    }
    
    /**
     * Saves an IdentifierSource
     */
    @RequestMapping("/module/idgen/saveIdentifierSource")
    public ModelAndView saveIdentifierSource(@ModelAttribute("source") IdentifierSource source, BindingResult result, SessionStatus status) {
		
    	// TODO: Implement validation here
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/idgen/editIdentifierSource");
		}
		
		// add/update the flag
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(source);
		
		// clears the command object from the session
		status.setComplete();
		
		// just display the edit page again
		return new ModelAndView("redirect:/module/idgen/manageIdentifierSources.form");
	}
    
    /**
     * Generate Identifiers Page
     */
    @RequestMapping("/module/idgen/viewIdentifierSource")
    public void viewIdentifierSource(ModelMap model, @RequestParam(required=true, value="source") IdentifierSource source) {
    	model.addAttribute("source", source);
    }
    
    /**
     * Export Identifiers To File
     */
    @RequestMapping("/module/idgen/exportIdentifiers")
    public void exportIdentifiers(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="numberToGenerate") Integer numberToGenerate) throws Exception {
    	
    	IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
    	
		response.setHeader("Content-Disposition", "attachment; filename=identifiers.csv");
		response.setHeader("Pragma", "no-cache");
    	response.setContentType("text/plain");
    	ServletOutputStream out = response.getOutputStream();
    	
    	List<String> batch = iss.generateIdentifiers(source, numberToGenerate);
    	for (Iterator<String> i = batch.iterator(); i.hasNext();) {
    		String identifier = i.next();
    		out.print("\"" + identifier + "\"" + (i.hasNext() ? "," : ""));
    	}
    }
    
    /**
     * Upload Identifiers From File
     */
    @RequestMapping("/module/idgen/addIdentifiersFromFile")
    public String addIdentifiersFromFile(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="inputFile") MultipartFile inputFile) throws Exception {
    	
    	IdentifierPool pool = (IdentifierPool)source;
    	InputStreamReader r = new InputStreamReader(inputFile.getInputStream());
		String[][] exportData = (new CSVParser(r, new CSVStrategy(',','"','#'))).getAllValues();
		List<String> ids = new ArrayList<String>();
		for (int i=0; i<exportData.length; i++) {
			for (int j=0; j<exportData[i].length; j++) {
				ids.add(exportData[i][j]);
			}
		}
		Context.getService(IdentifierSourceService.class).addIdentifiersToPool(pool, ids);
		return "redirect:/module/idgen/viewIdentifierSource.form?source="+source.getId();
    }
    
    /**
     * Upload Identifiers to Pool From Source
     */
    @RequestMapping("/module/idgen/addIdentifiersFromSource")
    public String addIdentifiersFromSource(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="batchSize") Integer batchSize) throws Exception {
    	
    	IdentifierPool pool = (IdentifierPool)source;
		Context.getService(IdentifierSourceService.class).addIdentifiersToPool(pool, batchSize);
		return "redirect:/module/idgen/viewIdentifierSource.form?source="+source.getId();
    }
}
