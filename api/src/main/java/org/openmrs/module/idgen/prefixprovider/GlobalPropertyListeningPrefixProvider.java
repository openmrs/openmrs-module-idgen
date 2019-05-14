package org.openmrs.module.idgen.prefixprovider;

import org.openmrs.api.GlobalPropertyListener;

/**
 * {@link PrefixProvider}s that have properties depending on <code>GlobalProperties</code> should
 * implement this interface
 */
public interface GlobalPropertyListeningPrefixProvider extends PrefixProvider, GlobalPropertyListener {
	
}
