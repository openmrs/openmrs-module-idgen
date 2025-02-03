package org.openmrs.module.idgen.prefixprovider;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.idgen.prefixprovider.LocationBasedPrefixProvider;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class LocationBasedPrefixProviderTest {

	LocationBasedPrefixProvider locationPrefixProvider;
	UserContext userContext;

	// user context locations
	Location locationB3;
	Location locationA3;
	// Locations with prefix attribute
	Location location3;
	Location location5;
	Location locationA2;

	@Before
	public void setup() {
		locationPrefixProvider = new LocationBasedPrefixProvider();

		mockStatic(Context.class);
		userContext = mock(UserContext.class);
		when(Context.getUserContext()).thenReturn(userContext);

		LocationService ls = mock(LocationService.class);
		AdministrationService as = mock(AdministrationService.class);
		when(Context.getLocationService()).thenReturn(ls);
		when(Context.getAdministrationService()).thenReturn(as);
		when(ls.getAllLocationAttributeTypes()).thenReturn(Collections.<LocationAttributeType>emptyList());
		when(as.getGlobalProperty(LocationBasedPrefixProvider.PREFIX_LOCATION_ATTRIBUTE_TYPE_GP))
		        .thenReturn("Location Code");
		setupLocationTree();
	}

	@Test
	public void getValue_shouldReturnPrefixDependingOnLocationInUserContext() {
		when(userContext.getLocation()).thenReturn(locationB3);
		Assert.assertThat(locationPrefixProvider.getValue(), is("LOC-5"));
		// Change to location A3
		when(userContext.getLocation()).thenReturn(locationA3);
		Assert.assertThat(locationPrefixProvider.getValue(), is("LOC-A2"));

	}

	@Test
	public void getLocationPrefix_shouldPickTheNearestValidPrefixUpTheTree() {
		Assert.assertEquals("LOC-A2", locationPrefixProvider.getLocationPrefix(locationA3));
	}

	@Test
	public void getLocationPrefix_shouldClimbToTopOfTheTreeAndPickValidPrefixIfOneIsSet() {
		LocationAttribute locationA2PrefixAtt = locationA2.getActiveAttributes().iterator().next();
		LocationAttribute location5PrefixAtt = location5.getActiveAttributes().iterator().next();
		// Invalidate prefix attributes for locations found in the middle of the tree
		locationA2PrefixAtt.setValue(" ");
		location5PrefixAtt.setValue(" ");
		Assert.assertEquals("LOC-3", locationPrefixProvider.getLocationPrefix(locationA3));
	}

	@Test(expected = RuntimeException.class)
	public void getLocationPrefix_throwAnExceptionIfNoValidPrefixIsFound() {
		// Invalidate valid prefixes
		LocationAttribute location2PrefixAtt = location3.getActiveAttributes().iterator().next();
		location2PrefixAtt.setValue(" ");
		locationPrefixProvider.getLocationPrefix(location3);
	}

	@Test
	public void getLocationPrefixRecursively_shouldPickThePrefixFromCurrentLocationIfOneIsSet() {
		Assert.assertEquals("LOC-5", locationPrefixProvider.getLocationPrefix(location5));
	}

	private void setupLocationTree() {
		LocationAttributeType locationPrefixType = createPrefixAttributeType();

		Location location1 = new Location();
		Location location2 = new Location();
		location2.setParentLocation(location1);

		location3 = new Location();
		location3.setParentLocation(location2);
		LocationAttribute location3PrefixAtt = new LocationAttribute();
		location3PrefixAtt.setAttributeType(locationPrefixType);
		location3PrefixAtt.setValue("LOC-3");
		location3.addAttribute(location3PrefixAtt);

		Location location4 = new Location();
		location4.setParentLocation(location3);

		location5 = new Location();
		location5.setParentLocation(location4);
		LocationAttribute location5PrefixAtt = new LocationAttribute();
		location5PrefixAtt.setAttributeType(locationPrefixType);
		location5PrefixAtt.setValue("LOC-5");
		location5.addAttribute(location5PrefixAtt);

		// First branch
		Location locationA1 = new Location();
		location5.addChildLocation(locationA1);

		locationA2 = new Location();
		locationA2.setParentLocation(locationA1);
		LocationAttribute locationA2PrefixAtt = new LocationAttribute();
		locationA2PrefixAtt.setAttributeType(locationPrefixType);
		locationA2PrefixAtt.setValue("LOC-A2");
		locationA2.addAttribute(locationA2PrefixAtt);

		locationA3 = new Location();
		locationA3.setParentLocation(locationA2);

		// Second Branch
		Location locationB1 = new Location();
		location5.addChildLocation(locationB1);

		Location locationB2 = new Location();
		locationB2.setParentLocation(locationB1);

		locationB3 = new Location();
		locationB3.setParentLocation(locationB2);
	}

	private LocationAttributeType createPrefixAttributeType() {
		LocationAttributeType prefixAttrType = new LocationAttributeType();
		prefixAttrType.setName(LocationBasedPrefixProvider.getPrefixLocationAttributeType());
		prefixAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		return prefixAttrType;
	}
}