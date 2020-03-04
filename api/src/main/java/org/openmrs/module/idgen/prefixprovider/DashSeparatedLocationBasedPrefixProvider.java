package org.openmrs.module.idgen.prefixprovider;

/**
 * A {@link LocationBasedPrefixProvider} that generates a prefix and appends a dash(-) to it.
 * The dash character separates the prefix from the actual patient identifier.
 */
public class DashSeparatedLocationBasedPrefixProvider extends LocationBasedPrefixProvider {

	@Override
	public String getValue() {
		return super.getValue() + "-";
	}
}
