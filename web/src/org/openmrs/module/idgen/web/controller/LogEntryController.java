package org.openmrs.module.idgen.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.propertyeditor.IdentifierSourceEditor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.propertyeditor.UserEditor;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LogEntryController {

	protected static Log log = LogFactory.getLog(LogEntryController.class);
	
	/**
	 * Default Constructor
	 */
	public LogEntryController() { }
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(IdentifierSource.class, new IdentifierSourceEditor());
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true));
		binder.registerCustomEditor(User.class, new UserEditor());
	}
	
    /**
     * View / Search Log Entries
     */
    @RequestMapping("/module/idgen/viewLogEntries")
    public void viewLogEntries(ModelMap model, HttpServletRequest request,
    							     @RequestParam(required=false, value="source") IdentifierSource source,
    							     @RequestParam(required=false, value="fromDate") Date fromDate,
    							     @RequestParam(required=false, value="toDate") Date toDate,
    							     @RequestParam(required=false, value="identifier") String identifier,
    							     @RequestParam(required=false, value="generatedBy") User generatedBy,
    							     @RequestParam(required=false, value="comment") String comment,
    							     @RequestParam(required=false, value="action") String action) {
    	
    	model.addAttribute("source", source);
    	model.addAttribute("fromDate", fromDate);
    	model.addAttribute("toDate", toDate);
    	model.addAttribute("identifier", identifier);
    	model.addAttribute("generatedBy", generatedBy);
    	model.addAttribute("comment", comment);
    	
    	List<LogEntry> logEntries = new ArrayList<LogEntry>();
		if (Context.isAuthenticated() && StringUtils.hasText(action)) {
			Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
			IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
			logEntries = iss.getLogEntries(source, fromDate, toDate, identifier, generatedBy, comment);
		}
		model.addAttribute("logEntries", logEntries);
		
		model.addAttribute("identifierSources", Context.getService(IdentifierSourceService.class).getAllIdentifierSources(true));
    }
}
