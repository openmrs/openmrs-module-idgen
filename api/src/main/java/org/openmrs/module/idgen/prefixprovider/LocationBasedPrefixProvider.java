package org.openmrs.module.idgen.prefixprovider;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.api.APIException;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.springframework.stereotype.Component;

/**
 * This scheme provides prefixes from the location in the current {@link UserContext}. From a
 * provided location, looks up through the locations hierarchy for the first location that carries
 * the prefix attribute. Then returns the location prefix found associated with that location.
 */
@Component("idgen.locationBasedPrefixProvider")
public class LocationBasedPrefixProvider implements GlobalPropertyListener, PrefixProvider {

	public final static String PREFIX_LOCATION_ATTRIBUTE_TYPE_GP = "idgen.prefixLocationAttributeType";
	
	private static String prefixLocationAttributeType = null;
	
	@Override
	public String getValue() {
		if (Context.getUserContext().getLocation() != null) {
			return getLocationPrefix(Context.getUserContext().getLocation());
		} else {
			throw new RuntimeException("No Location found in current UserContext");
		}
		
	}

	/**
	 * @param location The starting point location
	 * @return prefix The prefix saved as an attribute of the first found location up the hierarchy.
	 */
	public String getLocationPrefix(Location location) {
		if (location != null) {
			for (Object ob : location.getActiveAttributes()) {
				LocationAttribute att = (LocationAttribute) ob;
				if (att.getAttributeType().getName().equalsIgnoreCase(getPrefixLocationAttributeType())) {
					String prefix = (String) att.getValue();
					if (StringUtils.isNotBlank(prefix)) {
						return prefix;
					}
				}
			}
		} else {
			// This means we either reached the top of the location without a valid prefix found or there is no parent Location 
			// up the tree with a prefix set
			throw new APIException("No location prefix could be found up the location tree.");
		}
		return getLocationPrefix(location.getParentLocation());
	}
	
	public static String getPrefixLocationAttributeType() {
		if (prefixLocationAttributeType == null) {
			prefixLocationAttributeType = Context.getAdministrationService()
			        .getGlobalProperty(PREFIX_LOCATION_ATTRIBUTE_TYPE_GP);
		}
		return prefixLocationAttributeType;
	}
	
	@Override
	public boolean supportsPropertyName(String propertyName) {
		return propertyName.equals(PREFIX_LOCATION_ATTRIBUTE_TYPE_GP);
	}
	
	@Override
	public void globalPropertyChanged(GlobalProperty newValue) {
		prefixLocationAttributeType = newValue.getPropertyValue();
	}
	
	@Override
	public void globalPropertyDeleted(String propertyName) {
		prefixLocationAttributeType = null;
	}
	
}
