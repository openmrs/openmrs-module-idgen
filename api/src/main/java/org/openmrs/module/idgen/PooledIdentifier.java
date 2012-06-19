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
import java.util.UUID;

/**
 * Component which encapsulates an identifier that has been allocated to an Identifier Pool
 */
public class PooledIdentifier {
	
	//***** PROPERTIES *****
	
    private Integer id;
    private String uuid;  // Mainly here to support random sorting of pooled identifiers
    private IdentifierPool pool;
    private String identifier;
    private Date dateUsed;
    private String comment;
	
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public PooledIdentifier() {
    	this.uuid = UUID.randomUUID().toString();
    }
    
	/**
	 * Identifier-only constructor
	 */
	public PooledIdentifier(IdentifierPool pool, String identifier) {
		this(pool, identifier, null, null);
	}
	
	/**
	 * Full constructor
	 */
	public PooledIdentifier(IdentifierPool pool, String identifier, Date dateUsed, String comment) {
		this();
		this.pool = pool;
		this.identifier = identifier;
		this.dateUsed = dateUsed;
		this.comment = comment;
	}
	
    //***** INSTANCE METHODS *****
	
	/**
	 * Boolean indicating whether this identifier is available
	 */
	public boolean isAvailable() {
		return dateUsed == null;
	}
    
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof PooledIdentifier) {
			PooledIdentifier that = (PooledIdentifier) obj;
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

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getPool().getName() + ": " + getIdentifier();
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
	 * @return the pool
	 */
	public IdentifierPool getPool() {
		return pool;
	}
	/**
	 * @param pool the pool to set
	 */
	public void setPool(IdentifierPool pool) {
		this.pool = pool;
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
	 * @return the dateUsed
	 */
	public Date getDateUsed() {
		return dateUsed;
	}
	/**
	 * @param dateUsed the dateUsed to set
	 */
	public void setDateUsed(Date dateUsed) {
		this.dateUsed = dateUsed;
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
