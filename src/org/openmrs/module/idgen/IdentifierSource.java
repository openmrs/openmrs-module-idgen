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

import org.openmrs.PatientIdentifierType;
import org.openmrs.User;

/**
 * An IdentifierSource is a construct which can supply identifiers of a particular type
 */
public interface IdentifierSource {
    
    //****** PROPERTY ACCESS ******
    
	/**
	 * @return id - The unique Identifier for the object
	 */
	public Integer getId();
	
	/**
	 * @param id - The unique Identifier for the object
	 */
	public void setId(Integer id);
	
	/**
	 * @return the universally unique id for this object
	 */
	public String getUuid();
	
	/**
	 * @param uuid a universally unique id for this object
	 */
	public void setUuid(String uuid);
    
	/**
	 * @return the name
	 */
	public String getName();
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name);
	
	/**
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description);
	
	/**
	 * @return the PatientIdentifierType that this source supplies
	 */
	public PatientIdentifierType getIdentifierType();
	
	/**
	 * @param the PatientIdentifierType that this source supplies
	 */
	public void setIdentifierType(PatientIdentifierType type);

	/**
	 * @return User - the user who created the object
	 */
	public User getCreator();
	
	/**
	 * @param creator - the user who created the object
	 */
	public void setCreator(User creator);
	
	/**
	 * @return Date - the date the object was created
	 */
	public Date getDateCreated();
	
	/**
	 * @param dateCreated - the date the object was created
	 */
	public void setDateCreated(Date dateCreated);
	
	/**
	 * @return User - the user who last changed the object
	 */
	public User getChangedBy();
	
	/**
	 * @param changedBy - the user who last changed the object
	 */
	public void setChangedBy(User changedBy);
	
	/**
	 * @return Date - the date the object was last changed
	 */
	public Date getDateChanged();
	
	/**
	 * @param dateChanged - the date the object was last changed
	 */
	public void setDateChanged(Date dateChanged);
	
	/**
	 * @return Boolean - whether of not this object is retired
	 */
	public Boolean isRetired();
	
	/**
	 * @param retired - whether of not this object is retired
	 */
	public void setRetired(Boolean retired);
	
	/**
	 * @return User - the user who retired the object
	 */
	public User getRetiredBy();
	
	/**
	 * @param retiredBy - the user who retired the object
	 */
	public void setRetiredBy(User retiredBy);
	
	/**
	 * @return Date - the date the object was retired
	 */
	public Date getDateRetired();
	
	/**
	 * @param dateRetired - the date the object was retired
	 */
	public void setDateRetired(Date dateRetired);
	
	/**
	 * @return String - the reason the object was retired
	 */
	public String getRetireReason();
	
	/**
	 * @param retireReason - the reason the object was retired
	 */
	public void setRetireReason(String retireReason);
}	
