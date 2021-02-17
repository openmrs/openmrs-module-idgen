package org.openmrs.module.idgen.prefixprovider;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This scheme provides a prefix based on the current date.
 * This is configurable by class and configuration, for example, to have a prefix based on month and date,
 * you could configure the prefix as:  provider:org.openmrs.module.idgen.prefixprovider.DateBasedPrefixProvider:yyyyMM
 */
public class DateBasedPrefixProvider implements PrefixProvider {

	private String configuration;

	public DateBasedPrefixProvider(String configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public String getValue() {
		return new SimpleDateFormat(configuration).format(new Date());
	}
}
