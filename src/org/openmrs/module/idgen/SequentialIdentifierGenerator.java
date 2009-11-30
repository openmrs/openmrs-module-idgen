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

import java.util.List;

/**
 * Auto-generating Identifier Source, which returns Identifiers in sequence
 */
public class SequentialIdentifierGenerator extends BaseIdentifierSource {
	
	//***** PROPERTIES *****
	
	private long nextSequenceValue;
    private String prefix; // Optional prefix
    private String suffix; // Optional suffix
    private Integer length; // Stores the length of the identifier, if <= 0 or null, then variable length
    private String validCharacters; // Enables configuration in appropriate Base
	
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
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the nextSequenceValue
	 */
	protected long getNextSequenceValue() {
		return nextSequenceValue;
	}

	/**
	 * @param nextSequenceValue the nextSequenceValue to set
	 */
	protected void setNextSequenceValue(long nextSequenceValue) {
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
