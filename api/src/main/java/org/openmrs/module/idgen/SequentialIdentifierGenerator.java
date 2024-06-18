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
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.prefixprovider.ConstantPrefixProvider;
import org.openmrs.module.idgen.prefixprovider.PrefixProvider;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.module.idgen.suffixprovider.ConstantSuffixProvider;
import org.openmrs.module.idgen.suffixprovider.SuffixProvider;

/**
 * Auto-generating Identifier Source, which returns Identifiers in sequence
 */
public class SequentialIdentifierGenerator extends BaseIdentifierSource {

	//***** PROPERTIES *****
	private Long nextSequenceValue; //not used: declared only so that Hibernate creates the column when running tests
	
	/**
	 * A prefix can either be configurable or static. Configurable prefixes provide the
	 * {@link PrefixProvider} bean name which will be used as a prefix source; Example:
	 * <blockquote><pre>
	 * "provider:LocationBasedPrefixProvider" // Configurable prefix
	 * "LOC-" // Static prefix
	 * </pre></blockquote>
	 */
	private String prefix;
	/**
	 * A suffix can either be configurable or static. Configurable suffixes provide the
	 * {@link SuffixProvider} bean name which will be used as a suffix source; Example:
	 * <blockquote><pre>
	 * "provider:LocationBasedSuffixProvider" // Configurable suffix
	 * "LOC-" // Static suffix
	 * </pre></blockquote>
	 */
    private String suffix;
    private String firstIdentifierBase; // First identifier to start at
	private Integer minLength; // If > 0, will always return identifiers with a minimum of this length
	private Integer maxLength; // If > 0, will always return identifiers no longer than this length
    private String baseCharacterSet; // Enables configuration in appropriate Base
	
	/**
	 * A prefix expected in a configured {@code prefix}. If found in the prefix, it means it entails
	 * a {@link PrefixProvider} Bean name.
	 */
	public static final String CONFIGURATION_PREFIX = "provider:";
	
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
		
		PrefixProvider prefixProvider = getPrefixProvider(prefix);
		if (prefixProvider == null) {
			prefixProvider = new ConstantPrefixProvider(prefix);
		}
		identifier = prefixProvider.getValue() + identifier;


		SuffixProvider sufixProvider = getSuffixProvider(suffix);
		if (sufixProvider == null) {
			sufixProvider = new ConstantSuffixProvider(suffix);
		}
		identifier = identifier + sufixProvider.getValue();

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
	
	/**
	 * @return the nextSequenceValue
	 */
	public Long getNextSequenceValue() {
		if(nextSequenceValue == null) return -1l;
		return nextSequenceValue;
	}
 	/**
	 * @param nextSequenceValue : set the next identifier to be generated for this SequentialIdentifierGenerator
	 */
	public void setNextSequenceValue(Long nextSequenceValue) {
		this.nextSequenceValue = nextSequenceValue;
	}
	
	/**
	 * Gets the {@link PrefixProvider} from a configured {@code prefix}
	 * 
	 * @should return {@link ConstantPrefixProvider} based on {@code prefix} if a static prefix is provided.
	 * @should return {@link ConstantPrefixProvider} based on a blank prefix if an null or blank prefix is provided
	 * @should return {@link ConstantPrefixProvider} if the configured prefix provider cannot be Spring wired.
	 * @param prefix A string representing a prefix configuration or a static prefix value.
	 * @return the {@link PrefixProvider}
	 */
	public PrefixProvider getPrefixProvider(String prefix) throws APIException {
		
		if (StringUtils.isBlank(prefix)) {
			return new ConstantPrefixProvider("");
		}
		
		if (prefix.startsWith(CONFIGURATION_PREFIX)) {
			
			String beanName = StringUtils.substringAfter(prefix, ":");
			
			try {
				return Context.getRegisteredComponent(beanName, PrefixProvider.class);
			}
			catch (Exception e) {
				throw new APIException(
				    "Invalid prefix configuration. The " + PrefixProvider.class.getSimpleName() + " bean name ('" + beanName + "') could not be resolved.",
				    e);
			}
			
		}
		
		return new ConstantPrefixProvider(prefix);
	}

	/**
	 * Gets the {@link SuffixProvider} from a configured {@code suffix}
	 *
	 * @should return {@link ConstantSuffixProvider} based on {@code suffix} if a static suffix is provided.
	 * @should return {@link ConstantSuffixProvider} based on a blank suffix if an null or blank suffix is provided
	 * @should return {@link ConstantSuffixProvider} if the configured suffix provider cannot be Spring wired.
	 * @param suffix A string representing a suffix configuration or a static suffix value.
	 * @return the {@link SuffixProvider}
	 */
	public SuffixProvider getSuffixProvider(String suffix) throws APIException {

		if (StringUtils.isBlank(suffix)) {
			return new ConstantSuffixProvider("");
		}

		if (suffix.startsWith(CONFIGURATION_PREFIX)) {

			String beanName = StringUtils.substringAfter(suffix, ":");

			try {
				return Context.getRegisteredComponent(beanName, SuffixProvider.class);
			}
			catch (Exception e) {
				throw new APIException(
				    "Invalid suffix configuration. The " + SuffixProvider.class.getSimpleName() + " bean name ('" + beanName + "') could not be resolved.",
				    e);
			}

		}

		return new ConstantSuffixProvider(suffix);
	}

}
