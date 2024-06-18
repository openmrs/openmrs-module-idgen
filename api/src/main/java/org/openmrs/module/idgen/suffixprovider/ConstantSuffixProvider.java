package org.openmrs.module.idgen.suffixprovider;

import org.apache.commons.lang.StringUtils;

/**
 * The default {@link SuffixProvider} scheme.
 * This is a simple scheme that actually returns the static suffix value as is.
 */
public class ConstantSuffixProvider implements SuffixProvider {
	
	private String suffix = "";
	
	public ConstantSuffixProvider(String suffix) {
		if (!StringUtils.isEmpty(suffix)) {
			this.suffix = suffix;
		}
	}
	
	@Override
	public String getValue() {
		return suffix;
	}
}