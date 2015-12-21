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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Identifier Source which contains a pre-generated pool of identifiers,
 * and which typically is used in conjunction with another IdentifierSource 
 * which populates the pool
 */
public class IdentifierPool extends BaseIdentifierSource {
	
	//***** PROPERTIES *****
	
    private IdentifierSource source;
    private int batchSize = 1000; // for requests to pool
    private int minPoolSize = 500; // request more when we go below this number
    private boolean sequential = false;
    private Set<PooledIdentifier> identifiers;
    private boolean refillWithScheduledTask = true; // if true, refill with a scheduled task, otherwise refill on demands for ids

    //***** INSTANCE METHODS *****
   
    /**
     * Returns all available PooledIdentifiers
     */
    public Set<PooledIdentifier> getAvailableIdentifiers() {
    	Set<PooledIdentifier> ret = new HashSet<PooledIdentifier>();
    	for (PooledIdentifier i : getIdentifiers()) {
    		if (i.isAvailable()) {
    			ret.add(i);
    		}
    	}
    	return Collections.unmodifiableSet(ret);
    }

	/**
     * Returns all used PooledIdentifiers
     */
    public Set<PooledIdentifier> getUsedIdentifiers() {
    	Set<PooledIdentifier> ret = new HashSet<PooledIdentifier>();
    	for (PooledIdentifier i : getIdentifiers()) {
    		if (!i.isAvailable()) {
    			ret.add(i);
    		}
    	}
    	return Collections.unmodifiableSet(ret);
    }
    
	/** 
	 * @see IdentifierSource#nextIdentifier()
	 */
	public synchronized String nextIdentifier() {
		for (PooledIdentifier p : getIdentifiers()) {
			if (p.isAvailable()) {
				p.setDateUsed(new Date());
				return p.getIdentifier();
			}
		}
		throw new RuntimeException("Not enough available identifiers in pool");
	}
	
	/**
	 * Adds a new identifier to the pool 
	 * @param identifier the identifier to add
	 */
	public synchronized void addIdentifierToPool(String identifier) {
		getIdentifiers().add(new PooledIdentifier(this, identifier));
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
	 * @return boolean describing whether this is sequential
	 */
	public boolean isSequential() {
		return sequential;
	}

	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(boolean sequential) {
		this.sequential = sequential;
	}

	/**
	 * @return the identifiers
	 */
	public Set<PooledIdentifier> getIdentifiers() {
		if (identifiers == null) {
			identifiers = new LinkedHashSet<PooledIdentifier>();
		}
		return identifiers;
	}

	/**
	 * @param identifiers the pooledIdentifiers to set
	 */
	public void setIdentifiers(Set<PooledIdentifier> identifiers) {
		this.identifiers = identifiers;
	}

    /**
     * @return the refillWithScheduledTask
     */
    public boolean isRefillWithScheduledTask() {
        return refillWithScheduledTask;
    }

    /**
     * @param refillWithScheduledTask the refillWithScheduledTask to set
     */
    public void setRefillWithScheduledTask(boolean refillWithScheduledTask) {
        this.refillWithScheduledTask = refillWithScheduledTask;
    }

}
