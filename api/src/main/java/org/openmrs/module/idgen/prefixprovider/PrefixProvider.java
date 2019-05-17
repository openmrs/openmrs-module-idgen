package org.openmrs.module.idgen.prefixprovider;

/**
 * This interface allows adding custom schemes of how prefixes are generated
 * 
 * @since 4.6.0
 */
public interface PrefixProvider {
	
	/**
	 * Should return the prefix retrieved as per this scheme
	 * 
	 * @return prefix
	 */
	public String getValue();

}
