package org.openmrs.module.idgen.prefixprovider;

import org.apache.commons.lang.StringUtils;

/**
 * The default {@link PrefixProvider} scheme.
 * This is a simple scheme that actually returns the static prefix value as is.
 */
public class ConstantPrefixProvider implements PrefixProvider {
	
	private String prefix = "";
	
	public ConstantPrefixProvider(String prefix) {
		if (!StringUtils.isEmpty(prefix)) {
			this.prefix = prefix;
		}
	}
	
	@Override
	public String getValue() {
		return prefix;
	}
}