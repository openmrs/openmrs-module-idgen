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

import org.openmrs.api.context.Context;
import org.openmrs.patient.IdentifierValidator;

/**
 * Auto-generating Identifier Source, which returns Identifiers in sequence
 */
public class SequentialIdentifierGenerator extends BaseIdentifierSource {

	//***** PROPERTIES *****
	
	private int nextSequenceValue = -1;
    private String prefix; // Optional prefix
    private Integer initialSequenceValue; // Enables configuration of this generator to start at a number other than 1
    private Integer minSequenceLength; // If this is set, will pad sequential part of identifier with leading "0"s to this length
    private String validCharacters; // Enables configuration in appropriate Base
	
    //***** INSTANCE METHODS *****
    
    /**
     * Returns a boolean indicating whether this generator has already started producing identifiers
     */
    public boolean isInitialized() {
    	return nextSequenceValue > 0;
    }
    
	/** 
	 * @see IdentifierSource#nextIdentifier()
	 */
    public synchronized String nextIdentifier() {
    	
    	// Initialize sequence if needed
    	if (nextSequenceValue < 0) {
    		if (initialSequenceValue != null) {
    			nextSequenceValue = initialSequenceValue;
    		}
    		else {
    			nextSequenceValue = 1;
    		}
    	}
    	
    	// Convert the next sequence integer into a String with the appropriate Base characters
    	StringBuilder base = new StringBuilder();
    	char[] chars = validCharacters.toCharArray();
    	int n = nextSequenceValue++;
    	while (n > 0) {
    		base.insert(0, chars[n % chars.length]);
    		n = (int)(n / chars.length);
    	}
    	
    	// Pad the base, if needed, to reach a minimum required length
    	if (minSequenceLength != null && minSequenceLength > 0) {
	    	while (base.length() < minSequenceLength) {
	    		base.insert(0, "0");
	    	}
    	}

    	String identifier = base.toString();
    	
    	// Add a check-digit, if required
    	if (getIdentifierType().getValidator() != null) {
    		try {
	    		Class<?> c = Context.loadClass(getIdentifierType().getValidator());
	    		IdentifierValidator v = (IdentifierValidator)c.newInstance();
	    		identifier = v.getValidIdentifier(identifier);
    		}
    		catch (Exception e) {
    			throw new RuntimeException("Error generating check digit with " + getIdentifierType().getValidator(), e);
    		}
    	}
    	
    	// Return identifier with check-digit, prepended with prefix if required
    	return (prefix == null ? "" : prefix) + identifier;
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the nextSequenceValue
	 */
	protected int getNextSequenceValue() {
		return nextSequenceValue;
	}

	/**
	 * @param nextSequenceValue the nextSequenceValue to set
	 */
	protected void setNextSequenceValue(int nextSequenceValue) {
		this.nextSequenceValue = nextSequenceValue;
	}

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
	 * @return the initialSequenceValue
	 */
	public Integer getInitialSequenceValue() {
		return initialSequenceValue;
	}

	/**
	 * @param initialSequenceValue the initialSequenceValue to set
	 */
	public void setInitialSequenceValue(Integer initialSequenceValue) {
		this.initialSequenceValue = initialSequenceValue;
	}

	/**
	 * @return the minSequenceLength
	 */
	public Integer getMinSequenceLength() {
		return minSequenceLength;
	}

	/**
	 * @param minSequenceLength the minSequenceLength to set
	 */
	public void setMinSequenceLength(Integer minSequenceLength) {
		this.minSequenceLength = minSequenceLength;
	}

	/**
	 * @return the validCharacters
	 */
	public String getValidCharacters() {
		return validCharacters;
	}

	/**
	 * @param validCharacters the validCharacters to set
	 */
	public void setValidCharacters(String validCharacters) {
		this.validCharacters = validCharacters;
	}
}
