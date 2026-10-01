package org.openmrs.module.idgen;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.idgen.prefixprovider.LocationBasedPrefixProvider;
import org.openmrs.module.idgen.prefixprovider.PrefixProvider;
import org.openmrs.module.idgen.suffixprovider.LocationBasedSuffixProvider;
import org.openmrs.module.idgen.suffixprovider.SuffixProvider;

/**
 * test class for {@link SequentialIdentifierGenerator}
 */
public class SequentialIdentifierGeneratorTest {

	private MockedStatic<Context> mockedContext;
	@BeforeEach
	public void setup() {
		mockedContext = mockStatic(Context.class);
	}

	@AfterEach
	public void teardown() {
		mockedContext.close();
	}

	/**
	 * @verifies generate an identifier within minLength and maxLength bounds
	 * @see SequentialIdentifierGenerator#getIdentifierForSeed(long)
	 */
	@Test
	public void getIdentifierForSeed_shouldGenerateAnIdentifierWithinMinLengthAndMaxLengthBounds() throws Exception {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();

		generator.setBaseCharacterSet("0123456789");
		generator.setPrefix("FOO-");
		generator.setSuffix("-ACK");
		generator.setFirstIdentifierBase("000");
		generator.setMinLength(11);
		generator.setMaxLength(13);

		assertThat(generator.getIdentifierForSeed(1), is("FOO-001-ACK"));
		assertThat(generator.getIdentifierForSeed(12), is("FOO-012-ACK"));
		assertThat(generator.getIdentifierForSeed(123), is("FOO-123-ACK"));
		assertThat(generator.getIdentifierForSeed(1234), is("FOO-1234-ACK"));
		assertThat(generator.getIdentifierForSeed(12345), is("FOO-12345-ACK"));
	}

	/**
	 * @verifies throw an error if generated identifier is shorter than minLength
	 * @see SequentialIdentifierGenerator#getIdentifierForSeed(long)
	 */
	@Test
	public void getIdentifierForSeed_shouldThrowAnErrorIfGeneratedIdentifierIsShorterThanMinLength() throws Exception {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();

		generator.setBaseCharacterSet("0123456789");
		generator.setPrefix("FOO-");
		generator.setMinLength(6);

		Assertions.assertThrows(RuntimeException.class, () -> generator.getIdentifierForSeed(1));
	}

	/**
	 * @verifies throw an error if generated identifier is longer than maxLength
	 * @see SequentialIdentifierGenerator#getIdentifierForSeed(long)
	 */
	@Test
	public void getIdentifierForSeed_shouldThrowAnErrorIfGeneratedIdentifierIsLongerThanMaxLength() throws Exception {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();

		generator.setBaseCharacterSet("0123456789");
		generator.setPrefix("FOO-");
		generator.setMaxLength(1);

		Assertions.assertThrows(RuntimeException.class, () -> generator.getIdentifierForSeed(1));
	}

	@Test
	public void shouldSetNextSequenceValueToNegative() throws Exception {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
		generator.setBaseCharacterSet("0123456789");
		generator.setPrefix("FOO-");
		generator.setSuffix("-ACK");
		generator.setFirstIdentifierBase("000");
		generator.setMinLength(11);
		generator.setMaxLength(13);
 		assertThat(generator.getNextSequenceValue(), is(-1l));
	}
	
	@Test
	public void getIdentifierForSeed_shouldGenerateLocationPrefixedIdFromLocationBasedPrefixProvider() {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
		generator.setBaseCharacterSet("0123456789");
		generator.setFirstIdentifierBase("000");
		generator.setName("Location Prefixed Sequential Identifier Source");
		generator.setPrefix(
		    SequentialIdentifierGenerator.CONFIGURATION_PREFIX + LocationBasedPrefixProvider.class.getSimpleName());
		
		UserContext userContext = mock(UserContext.class);
		AdministrationService as = mock(AdministrationService.class);
		when(Context.getAdministrationService()).thenReturn(as);
		when(Context.getUserContext()).thenReturn(userContext);
		when(as.getGlobalProperty(LocationBasedPrefixProvider.PREFIX_LOCATION_ATTRIBUTE_TYPE_GP))
		        .thenReturn("Location Code");
		when(userContext.getLocation()).thenReturn(createLocationTree(true));
		when(Context.getRegisteredComponent("LocationBasedPrefixProvider", PrefixProvider.class)).thenReturn(new LocationBasedPrefixProvider());
		
		assertThat(generator.getIdentifierForSeed(1L), is("LOC_2-001"));
	}

	@Test
	public void getIdentifierForSeed_shouldGenerateLocationSuffixedIdFromLocationBasedSuffixProvider() {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
		generator.setBaseCharacterSet("0123456789");
		generator.setFirstIdentifierBase("000");
		generator.setName("Location Suffixed Sequential Identifier Source");
		generator.setSuffix(
		    SequentialIdentifierGenerator.CONFIGURATION_PREFIX + LocationBasedSuffixProvider.class.getSimpleName());

		UserContext userContext = mock(UserContext.class);
		AdministrationService as = mock(AdministrationService.class);
		when(Context.getAdministrationService()).thenReturn(as);
		when(Context.getUserContext()).thenReturn(userContext);
		when(as.getGlobalProperty(LocationBasedSuffixProvider.SUFFIX_LOCATION_ATTRIBUTE_TYPE_GP))
		        .thenReturn("Location Code");
		when(userContext.getLocation()).thenReturn(createLocationTree(false));
		when(Context.getRegisteredComponent("LocationBasedSuffixProvider", SuffixProvider.class)).thenReturn(new LocationBasedSuffixProvider());

		assertThat(generator.getIdentifierForSeed(1L), is("001-LOC_2"));
	}
	
	private Location createLocationTree(boolean isPrefix) {
		LocationAttributeType prefixAttrType = new LocationAttributeType();
		prefixAttrType.setName("Location Code");
		prefixAttrType.setMinOccurs(0);
		prefixAttrType.setMaxOccurs(1);
		prefixAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		
		Location location1 = new Location();
		Location location2 = new Location();
		location2.setParentLocation(location1);
		
		LocationAttribute location2PrefixAtt = new LocationAttribute();
		location2PrefixAtt.setAttributeType(prefixAttrType);
		location2PrefixAtt.setValue(isPrefix ? "LOC_2-" : "-LOC_2");
		location2.addAttribute(location2PrefixAtt);
		
		Location location3 = new Location();
		location3.setParentLocation(location2);
		
		Location location4 = new Location();
		location4.setParentLocation(location3);
		
		return location4;
	}
	
	@Test
	public void getPrefixProvider_shouldDefaultProperly() {
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		
		Assertions.assertEquals("pre-", gen.getPrefixProvider("pre-").getValue());
		Assertions.assertEquals("  -", gen.getPrefixProvider("  -").getValue());
		
		Assertions.assertEquals("", gen.getPrefixProvider(null).getValue());
		Assertions.assertEquals("", gen.getPrefixProvider("").getValue());
		Assertions.assertEquals("", gen.getPrefixProvider(" ").getValue());
	}
}
