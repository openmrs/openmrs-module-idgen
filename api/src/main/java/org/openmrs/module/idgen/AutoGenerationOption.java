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

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;

/**
 * Component which encapsulates the options for Auto-Generating a Patient Identifier
 */
public class AutoGenerationOption {
	
	//***** PROPERTIES *****
	
    private Integer id;
    private PatientIdentifierType identifierType;
    private Location location;
    private IdentifierSource source;
    private boolean manualEntryEnabled = true;
    private boolean automaticGenerationEnabled = false;
	
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public AutoGenerationOption() { }
    
    /**
     * Default Constructor for PatientIdentifierType
     */
    public AutoGenerationOption(PatientIdentifierType identifierType) {
    	this();
    	this.identifierType = identifierType;
    }
	
	/**
	 * Full constructor
	 */
	public AutoGenerationOption(PatientIdentifierType identifierType, IdentifierSource source, 
								boolean manualEntryEnabled, boolean automaticGenerationEnabled) {
		this(identifierType);
		this.source = source;
		this.manualEntryEnabled = manualEntryEnabled;
		this.automaticGenerationEnabled = automaticGenerationEnabled;
	}
	
    //***** INSTANCE METHODS *****
    
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof AutoGenerationOption) {
			AutoGenerationOption that = (AutoGenerationOption) obj;
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
	 * @return the identifierType
	 */
	public PatientIdentifierType getIdentifierType() {
		return identifierType;
	}

	/**
	 * @param identifierType the identifierType to set
	 */
	public void setIdentifierType(PatientIdentifierType identifierType) {
		this.identifierType = identifierType;
	}

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
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
	 * @return the manualEntryEnabled
	 */
	public boolean isManualEntryEnabled() {
		return manualEntryEnabled;
	}

	/**
	 * @param manualEntryEnabled the manualEntryEnabled to set
	 */
	public void setManualEntryEnabled(boolean manualEntryEnabled) {
		this.manualEntryEnabled = manualEntryEnabled;
	}

	/**
	 * @return the automaticGenerationEnabled
	 */
	public boolean isAutomaticGenerationEnabled() {
		return automaticGenerationEnabled;
	}

	/**
	 * @param automaticGenerationEnabled the automaticGenerationEnabled to set
	 */
	public void setAutomaticGenerationEnabled(boolean automaticGenerationEnabled) {
		this.automaticGenerationEnabled = automaticGenerationEnabled;
	}
}
