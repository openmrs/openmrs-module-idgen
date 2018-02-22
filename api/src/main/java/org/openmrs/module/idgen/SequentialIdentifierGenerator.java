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
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.patient.IdentifierValidator;

/**
 * Auto-generating Identifier Source, which returns Identifiers in sequence
 */
public class SequentialIdentifierGenerator extends BaseIdentifierSource {

	//***** PROPERTIES *****
	private Long nextSequenceValue; //not used: declared only so that Hibernate creates the column when running tests
    private String prefix; // Optional prefix
    private String suffix; // Optional suffix
    private String firstIdentifierBase; // First identifier to start at
	private Integer minLength; // If > 0, will always return identifiers with a minimum of this length
	private Integer maxLength; // If > 0, will always return identifiers no longer than this length
    private String baseCharacterSet; // Enables configuration in appropriate Base

    //***** INSTANCE METHODS *****

    /**
     * Returns a boolean indicating whether this generator has already started producing identifiers
     */
    public boolean isInitialized() {
        Long nextSequenceValue = Context.getService(IdentifierSourceService.class).getSequenceValue(this);
        return nextSequenceValue != null && nextSequenceValue > 0;
    }

    /**
     * Returns a new identifier for the given seed.  This does not change the state of the source
     * @param seed the seed to use for generation of the identifier
     * @return a new identifier for the given seed
	 * @should generate an identifier within minLength and maxLength bounds
	 * @should throw an error if generated identifier is shorter than minLength
	 * @should throw an error if generated identifier is longer than maxLength
     */
    public String getIdentifierForSeed(long seed) {

    	// Convert the next sequence integer into a String with the appropriate Base characters
		int seqLength = firstIdentifierBase == null ? 1 : firstIdentifierBase.length();

		String identifier = IdgenUtil.convertToBase(seed, baseCharacterSet.toCharArray(), seqLength);

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

		if (this.minLength != null && this.minLength > 0) {
			if (identifier.length() < this.minLength) {
				throw new RuntimeException("Invalid configuration for IdentifierSource. Length minimum set to " + this.minLength + " but generated " + identifier);
			}
		}

		if (this.maxLength != null && this.maxLength > 0) {
			if (identifier.length() > this.maxLength) {
				throw new RuntimeException("Invalid configuration for IdentifierSource. Length maximum set to " + this.maxLength + " but generated " + identifier);
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
	 * @return the minLength
	 */
	public Integer getMinLength() {
		return minLength;
	}

	/**
	 * @param minLength the minLength to set
	 */
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	/**
	 * @return the maxLength
	 */
	public Integer getMaxLength() {
		return maxLength;
	}

	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
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
