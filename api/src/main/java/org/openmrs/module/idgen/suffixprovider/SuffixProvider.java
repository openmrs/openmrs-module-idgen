package org.openmrs.module.idgen.suffixprovider;

/**
 * This interface allows adding custom schemes of how suffixes are generated
 * 
 * @since 4.6.0
 */
public interface SuffixProvider {
	
	/**
	 * Should return the suffix retrieved as per this scheme
	 * 
	 * @return suffix
	 */
	public String getValue();

}
