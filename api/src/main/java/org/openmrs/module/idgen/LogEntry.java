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
package org.openmrs.module.idgen;

import java.util.Date;
import org.openmrs.User;

/**
 * Component which encapsulates a Log entry
 */
public class LogEntry {
	
	//***** PROPERTIES *****
	
    private Integer id;
    private IdentifierSource source;
    private String identifier;
    private Date dateGenerated;
    private User generatedBy;
    private String comment;
	
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public LogEntry() { }
    
    /**
     * Full Constructor
     */
    public LogEntry(IdentifierSource source, String identifier, Date dateGenerated, User generatedBy, String comment) { 
    	this.source = source;
    	this.identifier = identifier;
    	this.dateGenerated = dateGenerated;
    	this.generatedBy = generatedBy;
    	this.comment = comment;
    }
	
    //***** INSTANCE METHODS *****
    
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof LogEntry) {
			LogEntry that = (LogEntry) obj;
			if (this.getId() != null) {
				return (this.getId().equals(that.getId()));
			}
		}
		return this == obj;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (getId() != null) {
			return 31 * getId().hashCode();
		}
		return super.hashCode();
	}

	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the source
	 */
	public IdentifierSource getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(IdentifierSource source) {
		this.source = source;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the dateGenerated
	 */
	public Date getDateGenerated() {
		return dateGenerated;
	}

	/**
	 * @param dateGenerated the dateGenerated to set
	 */
	public void setDateGenerated(Date dateGenerated) {
		this.dateGenerated = dateGenerated;
	}

	/**
	 * @return the generatedBy
	 */
	public User getGeneratedBy() {
		return generatedBy;
	}

	/**
	 * @param generatedBy the generatedBy to set
	 */
	public void setGeneratedBy(User generatedBy) {
		this.generatedBy = generatedBy;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
