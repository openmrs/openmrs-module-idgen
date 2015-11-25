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
import java.util.Set;

import org.openmrs.PatientIdentifierType;
import org.openmrs.User;

/**
 * An IdentifierSource is a construct which can supply identifiers of a particular type
 */
public abstract class IdentifierSource {
    
    //****** PROPERTY ACCESS ******
    
	/**
	 * @return id - The unique Identifier for the object
	 */
	public abstract Integer getId();
	
	/**
	 * @param id - The unique Identifier for the object
	 */
	public abstract void setId(Integer id);
	
	/**
	 * @return the universally unique id for this object
	 */
	public abstract String getUuid();
	
	/**
	 * @param uuid a universally unique id for this object
	 */
	public abstract void setUuid(String uuid);
    
	/**
	 * @return the name
	 */
	public abstract String getName();
	
	/**
	 * @param name the name to set
	 */
	public abstract void setName(String name);
	
	/**
	 * @return the description
	 */
	public abstract String getDescription();
	
	/**
	 * @param description the description to set
	 */
	public abstract void setDescription(String description);
	
	/**
	 * @return the PatientIdentifierType that this source supplies
	 */
	public abstract PatientIdentifierType getIdentifierType();
	
	/**
	 * @param the PatientIdentifierType that this source supplies
	 */
	public abstract void setIdentifierType(PatientIdentifierType type);

	/**
	 * @return User - the user who created the object
	 */
	public abstract User getCreator();
	
	/**
	 * @param creator - the user who created the object
	 */
	public abstract void setCreator(User creator);
	
	/**
	 * @return Date - the date the object was created
	 */
	public abstract Date getDateCreated();
	
	/**
	 * @param dateCreated - the date the object was created
	 */
	public abstract void setDateCreated(Date dateCreated);
	
	/**
	 * @return User - the user who last changed the object
	 */
	public abstract User getChangedBy();
	
	/**
	 * @param changedBy - the user who last changed the object
	 */
	public abstract void setChangedBy(User changedBy);
	
	/**
	 * @return Date - the date the object was last changed
	 */
	public abstract Date getDateChanged();
	
	/**
	 * @param dateChanged - the date the object was last changed
	 */
	public abstract void setDateChanged(Date dateChanged);
	
	/**
	 * @return Boolean - whether of not this object is retired
	 */
	public abstract Boolean isRetired();
	
	/**
	 * @param retired - whether of not this object is retired
	 */
	public abstract void setRetired(Boolean retired);
	
	/**
	 * @return User - the user who retired the object
	 */
	public abstract User getRetiredBy();
	
	/**
	 * @param retiredBy - the user who retired the object
	 */
	public abstract void setRetiredBy(User retiredBy);
	
	/**
	 * @return Date - the date the object was retired
	 */
	public abstract Date getDateRetired();
	
	/**
	 * @param dateRetired - the date the object was retired
	 */
	public abstract void setDateRetired(Date dateRetired);
	
	/**
	 * @return String - the reason the object was retired
	 */
	public abstract String getRetireReason();
	
	/**
	 * @param retireReason - the reason the object was retired
	 */
	public abstract void setRetireReason(String retireReason);
	
	/**
	 * @return Set of reserved identifiers
	 */
	public abstract Set<String> getReservedIdentifiers();
	
	/**
	 * @param - the reserved identifiers to set
	 */
	public abstract void setReservedIdentifiers(Set<String> reservedIdentifiers);
	
	/**
	 * @param - the reserved identifier to add
	 */
	public abstract void addReservedIdentifier(String reservedIdentifier);
}	
