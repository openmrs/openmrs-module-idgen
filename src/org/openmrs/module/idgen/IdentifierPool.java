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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Identifier Source which contains a pre-generated pool of identifiers,
 * and which typically is used in conjunction with another IdentifierSource 
 * which generates these identifiers in batch
 */
public class IdentifierPool extends BaseIdentifierSource {
	
	//***** PROPERTIES *****
	
    private IdentifierSource source;
    private int batchSize = 1000; // for requests to pool
    private int minPoolSize = 500; // request more when we go below this number
    private Set<PooledIdentifier> usedIdentifiers;
    private Set<PooledIdentifier> availableIdentifiers;
	
    //***** INSTANCE METHODS *****
    
	/** 
	 * @see IdentifierSource#getIdentifier()
	 */
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see IdentifierSource#getIdentifiers(int)
	 */
	public List<String> getIdentifiers(int batchSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see IdentifierSource#isValid(java.lang.String)
	 */
	public boolean isValid(String identifier) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Adds a new identifier to the pool 
	 * @param identifier the identifier to add
	 */
	public void addIdentifierToPool(String identifier) {
		getAvailableIdentifiers().add(new PooledIdentifier(this, identifier));
	}
	
	//***** PROPERTY ACCESS *****

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
	 * @return the batchSize
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * @return the minPoolSize
	 */
	public int getMinPoolSize() {
		return minPoolSize;
	}

	/**
	 * @param minPoolSize the minPoolSize to set
	 */
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	/**
	 * @return the pooledIdentifiers
	 */
	public Set<PooledIdentifier> getUsedIdentifiers() {
		if (usedIdentifiers == null) {
			usedIdentifiers = new LinkedHashSet<PooledIdentifier>();
		}
		return usedIdentifiers;
	}

	/**
	 * @param pooledIdentifiers the pooledIdentifiers to set
	 */
	public void setUsedIdentifiers(Set<PooledIdentifier> usedIdentifiers) {
		this.usedIdentifiers = usedIdentifiers;
	}

	/**
	 * @return the availableIdentifiers
	 */
	public Set<PooledIdentifier> getAvailableIdentifiers() {
		if (availableIdentifiers == null) {
			availableIdentifiers = new LinkedHashSet<PooledIdentifier>();
		}
		return availableIdentifiers;
	}

	/**
	 * @param availableIdentifiers the availableIdentifiers to set
	 */
	public void setAvailableIdentifiers(Set<PooledIdentifier> availableIdentifiers) {
		this.availableIdentifiers = availableIdentifiers;
	}
}
