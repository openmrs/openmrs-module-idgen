package org.openmrs.module.idgen.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifiersMessage;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.propertyeditor.IdentifierSourceEditor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.IdentifierSourceValidator;
import org.openmrs.module.idgen.validator.RemoteIdentifierSourceValidator;
import org.openmrs.module.idgen.validator.SequentialIdentifierGeneratorValidator;
import org.openmrs.propertyeditor.PatientIdentifierTypeEditor;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
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

	@Autowired
	private IdentifierSourceService iss;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public IdentifierSourceController() { }
	
	//***** INSTANCE METHODS *****
	
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
			
			List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
			for (Iterator<PatientIdentifierType> i = sourcesByType.keySet().iterator(); i.hasNext();) {
				PatientIdentifierType pit = i.next();
				if (sourcesByType.get(pit).isEmpty()) {
					i.remove();
				}
				identifierTypes.add(pit);
			}
			model.addAttribute("sourcesByType", sourcesByType);
			model.addAttribute("identifierTypes", identifierTypes);
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
		
    	// Validate input
    	Validator v = new IdentifierSourceValidator();
    	if (source instanceof SequentialIdentifierGenerator) {
    		v = new SequentialIdentifierGeneratorValidator();
    	}
    	else if (source instanceof RemoteIdentifierSource) {
    		v = new RemoteIdentifierSourceValidator();
    	}
    	v.validate(source, result);
    	
		if (result.hasErrors()) {
			return new ModelAndView("/module/idgen/editIdentifierSource");
		}
		
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(source);
		status.setComplete();
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
     * Generate and Output a Single new Identifier
     */
    @RequestMapping("/module/idgen/generateIdentifier")
    public void generateIdentifier(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=false, value="comment") String comment,
                                   @RequestParam(required=false, value="username") String username,
                                   @RequestParam(required=false, value="password") String password) throws Exception {
    	exportIdentifiers(model, request, response, source, 1, comment, username, password);
    }
    
    /**
     * Export Identifiers To File
     */
    @RequestMapping("/module/idgen/exportIdentifiers")
    public void exportIdentifiers(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="numberToGenerate") Integer numberToGenerate,
    							   @RequestParam(required=false, value="comment") String comment,
                                   @RequestParam(required=false, value="username") String username,
                                   @RequestParam(required=false, value="password") String password) throws Exception {

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            Context.authenticate(username, password);
        }
    	

        if (StringUtils.isEmpty(comment)) {
            comment = "Batch Export of " + numberToGenerate + " to file";
        }
        List<String> batch = iss.generateIdentifiers(source, numberToGenerate, comment);

        response.setHeader("Content-Disposition", "attachment; filename=identifiers.txt");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        
        new ObjectMapper().writeValue(out, new RemoteIdentifiersMessage(batch));
    }
    
    /**
     * Upload Identifiers From File
     */
    @RequestMapping("/module/idgen/addIdentifiersFromFile")
    public String addIdentifiersFromFile(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam(required=true, value="source") IdentifierSource source,
                                         @RequestParam(required=true, value="inputFile") MultipartFile inputFile) throws Exception {

        IdentifierPool pool = (IdentifierPool)source;
        List<String> ids = new ArrayList<String>();
        InputStream streamReader = null;
        if(inputFile != null){
            try {
                streamReader = inputFile.getInputStream();
                if(streamReader != null){
                    try{
                        ObjectMapper mapper = new ObjectMapper();
                        RemoteIdentifiersMessage remoteIdentifiersMessage = mapper.readValue(streamReader, RemoteIdentifiersMessage.class);
                        if(remoteIdentifiersMessage != null){
                            ids = remoteIdentifiersMessage.getIdentifiers();
                            iss.addIdentifiersToPool(pool, ids);
                            request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Success: Identifiers successfully uploaded.");
                        }
                    }catch (IOException ex){
                        log.error("Unexpected response: " , ex);
                        throw new Exception(ex);
                    }
                }
            }catch (Exception e){
                log.error("failed to read uploaded file", e);
                throw new Exception(e);
            }finally {
                if(streamReader != null){
                    streamReader.close();
                }
            }
        }
        return "redirect:/module/idgen/viewIdentifierSource.form?source=" + source.getId();
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
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Success: Identifiers successfully imported.");
		return "redirect:/module/idgen/viewIdentifierSource.form?source="+source.getId();
    }
    
    /**
     * Reserve Identifiers From File
     */
    @RequestMapping("/module/idgen/reserveIdentifiersFromFile")
    public String reserveIdentifiersFromFile(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="inputFile") MultipartFile inputFile) throws Exception {
    	
    	BufferedReader r = null;
    	try {
    		r = new BufferedReader(new InputStreamReader(inputFile.getInputStream()));
    		for (String s = r.readLine(); s != null; s = r.readLine()) {
    			if (StringUtils.isNotBlank(s)) {
    				source.addReservedIdentifier(s);
    			}
    		}
    	}
    	finally {
    		if (r != null) {
    			r.close();
    		}
    	}
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(source);
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Success: Identifiers successfully uploaded.");
		return "redirect:/module/idgen/viewIdentifierSource.form?source="+source.getId();
    }
    
    /**
     * Export Identifiers To File
     */
    @RequestMapping("/module/idgen/exportReservedIdentifiers")
    public void exportReservedIdentifiers(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source) throws Exception {

		response.setHeader("Content-Disposition", "attachment; filename=reservedIdentifiers.txt");
		response.setHeader("Pragma", "no-cache");
    	response.setContentType("text/plain");
    	ServletOutputStream out = response.getOutputStream();
    	String separator = System.getProperty("line.separator");
    	
    	for (Iterator<String> i = source.getReservedIdentifiers().iterator(); i.hasNext();) {
    		String identifier = i.next();
    		out.print(identifier + (i.hasNext() ? separator : ""));
    	}
    }
}
