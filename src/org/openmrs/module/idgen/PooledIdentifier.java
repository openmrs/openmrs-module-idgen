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

/**
 * Component which encapsulates an identifier that has been allocated to an Identifier Pool
 */
public class PooledIdentifier {
	
	public static final String AVAILABLE = "AVAILABLE";
	public static final String RESERVED = "RESERVED";
	
	//***** PROPERTIES *****
	
    private Integer id;
    private IdentifierPool pool;
    private String identifier;
    private String status;
    private Date statusDate;
	
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public PooledIdentifier() {}
    
	/**
	 * Identifier-only constructor
	 */
	public PooledIdentifier(IdentifierPool pool, String identifier) {
		this(pool, identifier, AVAILABLE, new Date());
	}
	
	/**
	 * Full constructor
	 */
	public PooledIdentifier(IdentifierPool pool, String identifier, String status, Date statusDate) {
		this.pool = pool;
		this.identifier = identifier;
		this.status = status;
		this.statusDate = statusDate;
	}
	
    //***** INSTANCE METHODS *****
    
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}
	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
}
