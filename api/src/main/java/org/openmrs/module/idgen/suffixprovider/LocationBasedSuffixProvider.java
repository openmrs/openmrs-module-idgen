package org.openmrs.module.idgen.suffixprovider;

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
 * This scheme provides suffixes from the location in the current {@link UserContext}. From a
 * provided location, looks up through the locations hierarchy for the first location that carries
 * the suffix attribute. Then returns the location suffix found associated with that location.
 */
@Component("idgen.locationBasedSuffixProvider")
public class LocationBasedSuffixProvider implements GlobalPropertyListener, SuffixProvider {

	public final static String SUFFIX_LOCATION_ATTRIBUTE_TYPE_GP = "idgen.suffixLocationAttributeType";
	
	private static String suffixLocationAttributeType = null;
	
	@Override
	public String getValue() {
		if (Context.getUserContext().getLocation() != null) {
			return getLocationSuffix(Context.getUserContext().getLocation());
		} else {
			throw new RuntimeException("No Location found in current UserContext");
		}
		
	}

	/**
	 * @param location The starting point location
	 * @return suffix The suffix saved as an attribute of the first found location up the hierarchy.
	 */
	public String getLocationSuffix(Location location) {
		if (location != null) {
			for (Object ob : location.getActiveAttributes()) {
				LocationAttribute att = (LocationAttribute) ob;
				if (att.getAttributeType().getName().equalsIgnoreCase(getSuffixLocationAttributeType())) {
					String suffix = (String) att.getValue();
					if (StringUtils.isNotBlank(suffix)) {
						return suffix;
					}
				}
			}
		} else {
			// This means we either reached the top of the location without a valid suffix found or there is no parent Location
			// up the tree with a suffix set
			throw new APIException("No location suffix could be found up the location tree.");
		}
		return getLocationSuffix(location.getParentLocation());
	}
	
	public static String getSuffixLocationAttributeType() {
		if (suffixLocationAttributeType == null) {
			suffixLocationAttributeType = Context.getAdministrationService()
			        .getGlobalProperty(SUFFIX_LOCATION_ATTRIBUTE_TYPE_GP);
		}
		return suffixLocationAttributeType;
	}
	
	@Override
	public boolean supportsPropertyName(String propertyName) {
		return propertyName.equals(SUFFIX_LOCATION_ATTRIBUTE_TYPE_GP);
	}
	
	@Override
	public void globalPropertyChanged(GlobalProperty newValue) {
		suffixLocationAttributeType = newValue.getPropertyValue();
	}
	
	@Override
	public void globalPropertyDeleted(String propertyName) {
		suffixLocationAttributeType = null;
	}
	
}
