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

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.patient.IdentifierValidator;

/**
 * Auto-generating Identifier Source, which returns Identifiers in sequence
 */
public class SequentialIdentifierGenerator extends BaseIdentifierSource {

	//***** PROPERTIES *****

    private String prefix; // Optional prefix
    private String suffix; // Optional suffix
    private String firstIdentifierBase; // First identifier to start at
    private Integer length; // If > 0, will always return identifiers with the given length
    private String baseCharacterSet; // Enables configuration in appropriate Base
	
    //***** INSTANCE METHODS *****

    /**
     * Returns a new identifier for the given seed.  This does not change the state of the source
     * @param seed the seed to use for generation of the identifier
     * @return a new identifier for the given seed
     */
    public String getIdentifierForSeed(long seed) {
    	
    	// Convert the next sequence integer into a String with the appropriate Base characters
    	int minLength = firstIdentifierBase == null ? 1 : firstIdentifierBase.length();
    	String identifier = IdgenUtil.convertToBase(seed, baseCharacterSet.toCharArray(), minLength);
    	
    	// Add optional prefix and suffix
    	identifier = (prefix == null ? identifier : prefix + identifier);
    	identifier = (suffix == null ? identifier : identifier + suffix);
    	
    	// Add check-digit, if required
    	if (getIdentifierType() != null && StringUtils.isNotEmpty(getIdentifierType().getValidator())) {
    		try {
	    		Class<?> c = Context.loadClass(getIdentifierType().getValidator());
	    		IdentifierValidator v = (IdentifierValidator)c.newInstance();
	    		identifier = v.getValidIdentifier(identifier);
    		}
    		catch (Exception e) {
    			throw new RuntimeException("Error generating check digit with " + getIdentifierType().getValidator(), e);
    		}
    	}

    	if (length != null && length > 0) {
    		if (identifier.length() != length) {
    			throw new RuntimeException("Invalid configuration for IdentifierSource. Length set to " + length + " but generated " + identifier);
    		}
    	}
    	return identifier;
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * @return the firstIdentifierBase
	 */
	public String getFirstIdentifierBase() {
		return firstIdentifierBase;
	}

	/**
	 * @param firstIdentifierBase the firstIdentifierBase to set
	 */
	public void setFirstIdentifierBase(String firstIdentifierBase) {
		this.firstIdentifierBase = firstIdentifierBase;
	}

	/**
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * @return the baseCharacterSet
	 */
	public String getBaseCharacterSet() {
		return baseCharacterSet;
	}

	/**
	 * @param baseCharacterSet the baseCharacterSet to set
	 */
	public void setBaseCharacterSet(String baseCharacterSet) {
		this.baseCharacterSet = baseCharacterSet;
	}
}
