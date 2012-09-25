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
import java.util.HashSet;
import java.util.Set;

import org.openmrs.PatientIdentifierType;
import org.openmrs.User;

/**
 * An base implementation which provides all common property access
 */
public abstract class BaseIdentifierSource extends IdentifierSource {
	
	//***** PROPERTIES *****
	
	private Integer id;
	private String uuid;
	private String name;
	private String description;
	private PatientIdentifierType identifierType;
	private User creator;
	private Date dateCreated;
	private User changedBy;
	private Date dateChanged;
	private Boolean retired = Boolean.FALSE;
	private User retiredBy;
	private Date dateRetired;
	private String retireReason;
	private Set<String> reservedIdentifiers;
	
	//***** CONSTRUCTORS *****
	
	public BaseIdentifierSource() {}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Adds a reserved identifier
	 */
	public void addReservedIdentifier(String reservedIdentifier) {
		getReservedIdentifiers().add(reservedIdentifier);
	}
	
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof IdentifierSource) {
			IdentifierSource that = (IdentifierSource) obj;
			if (this.getId() != null) {
				return (this.getId().equals(that.getId()));
			}
            if (this.getUuid() != null) {
                return (this.getUuid().equals(that.getUuid()));
            }
		}
		return this == obj;
	}

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    /**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	//***** PROPERTY ACCESS ******
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
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the creator
	 */
	public User getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}
	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	/**
	 * @return the changedBy
	 */
	public User getChangedBy() {
		return changedBy;
	}
	/**
	 * @param changedBy the changedBy to set
	 */
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}
	/**
	 * @return the dateChanged
	 */
	public Date getDateChanged() {
		return dateChanged;
	}
	/**
	 * @param dateChanged the dateChanged to set
	 */
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	/**
	 * @return the retired
	 */
	public Boolean getRetired() {
		return retired;
	}
	/** 
	 * @return the retired
	 */
	public Boolean isRetired() {
		return retired;
	}
	/**
	 * @param retired the retired to set
	 */
	public void setRetired(Boolean retired) {
		this.retired = retired;
	}
	/**
	 * @return the retiredBy
	 */
	public User getRetiredBy() {
		return retiredBy;
	}
	/**
	 * @param retiredBy the retiredBy to set
	 */
	public void setRetiredBy(User retiredBy) {
		this.retiredBy = retiredBy;
	}
	/**
	 * @return the dateRetired
	 */
	public Date getDateRetired() {
		return dateRetired;
	}
	/**
	 * @param dateRetired the dateRetired to set
	 */
	public void setDateRetired(Date dateRetired) {
		this.dateRetired = dateRetired;
	}
	/**
	 * @return the retireReason
	 */
	public String getRetireReason() {
		return retireReason;
	}
	/**
	 * @param retireReason the retireReason to set
	 */
	public void setRetireReason(String retireReason) {
		this.retireReason = retireReason;
	}
	/**
	 * @return the reservedIdentifiers
	 */
	public Set<String> getReservedIdentifiers() {
		if (reservedIdentifiers == null) {
			reservedIdentifiers = new HashSet<String>();
		}
		return reservedIdentifiers;
	}
	/**
	 * @param reservedIdentifiers the reservedIdentifiers to set
	 */
	public void setReservedIdentifiers(Set<String> reservedIdentifiers) {
		this.reservedIdentifiers = reservedIdentifiers;
	}
}	
