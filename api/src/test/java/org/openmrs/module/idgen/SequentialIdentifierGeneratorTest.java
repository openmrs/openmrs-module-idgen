package org.openmrs.module.idgen;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.idgen.prefixprovider.LocationBasedPrefixProvider;
import org.openmrs.module.idgen.prefixprovider.PrefixProvider;
import org.openmrs.module.idgen.processor.SequentialIdentifierGeneratorProcessor;
import org.openmrs.module.idgen.service.BaseIdentifierSourceService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * test class for {@link SequentialIdentifierGenerator}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, DateUtil.class})
public class SequentialIdentifierGeneratorTest {
		
	@Before
	public void setup() {
		mockStatic(Context.class);
		mockStatic(DateUtil.class);
		when(DateUtil.getDate(anyString())).thenCallRealMethod();
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
	@Test(expected = RuntimeException.class)
	public void getIdentifierForSeed_shouldThrowAnErrorIfGeneratedIdentifierIsShorterThanMinLength() throws Exception {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();

		generator.setBaseCharacterSet("0123456789");
		generator.setPrefix("FOO-");
		generator.setMinLength(6);

		generator.getIdentifierForSeed(1);
	}

	/**
	 * @verifies throw an error if generated identifier is longer than maxLength
	 * @see SequentialIdentifierGenerator#getIdentifierForSeed(long)
	 */
	@Test(expected = RuntimeException.class)
	public void getIdentifierForSeed_shouldThrowAnErrorIfGeneratedIdentifierIsLongerThanMaxLength() throws Exception {
		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();

		generator.setBaseCharacterSet("0123456789");
		generator.setPrefix("FOO-");
		generator.setMaxLength(1);

		generator.getIdentifierForSeed(1);
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
		when(userContext.getLocation()).thenReturn(createLocationTree());
		when(Context.getRegisteredComponent("LocationBasedPrefixProvider", PrefixProvider.class)).thenReturn(new LocationBasedPrefixProvider());
		
		assertThat(generator.getIdentifierForSeed(1L), is("LOC_2-001"));
	}
	
	private Location createLocationTree() {
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
		location2PrefixAtt.setValue("LOC_2-");
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
		
		Assert.assertEquals("pre-", gen.getPrefixProvider("pre-").getValue());
		Assert.assertEquals("  -", gen.getPrefixProvider("  -").getValue());
		
		Assert.assertEquals("", gen.getPrefixProvider(null).getValue());
		Assert.assertEquals("", gen.getPrefixProvider("").getValue());
		Assert.assertEquals("", gen.getPrefixProvider(" ").getValue());
	}

	@Test
	public void getMinimumSequenceValue_shouldSupportDynamicDateEvaluation() {

		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
		generator.setName("KGH ID Identifier Generator");
		generator.setPrefix("KGH");
		generator.setBaseCharacterSet("1234567890");
		generator.setFirstIdentifierBase("0001");

		Date endOfJan = DateUtil.getDate("2020-01-31");
		when(DateUtil.getCurrentDate()).thenReturn(endOfJan);

		generator.setFirstIdentifierBase("${currentDate:yyyyMMdd}1");
		String firstIdentifierBase = generator.evaluateFirstIdentifierBase();
		Assert.assertThat(firstIdentifierBase, Matchers.is("202001311"));

		generator.setFirstIdentifierBase("888${currentDate:yyyyMM}55");
		firstIdentifierBase = generator.evaluateFirstIdentifierBase();
		Assert.assertThat(firstIdentifierBase, Matchers.is("88820200155"));

		generator.setFirstIdentifierBase("${currentDate:yyyy}111${currentDate:MM}");
		firstIdentifierBase = generator.evaluateFirstIdentifierBase();
		Assert.assertThat(firstIdentifierBase, Matchers.is("202011101"));
	}

	@Test
	public void shouldSwitchToNewPrefixAndResetSequenceOnNewDate() {

		SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
		generator.setName("KGH ID Identifier Generator");
		generator.setPrefix("KGH");
		generator.setBaseCharacterSet("1234567890");
		generator.setFirstIdentifierBase("0001");

		SequentialIdentifierGeneratorProcessor processor = new SequentialIdentifierGeneratorProcessor();
		processor.setIdentifierSourceService(new BaseIdentifierSourceService() {
			long nextSequenceNum = 1L;
			public void saveSequenceValue(SequentialIdentifierGenerator sequentialIdentifierGenerator, long l) {
				nextSequenceNum = l;
			}
			public Long getSequenceValue(SequentialIdentifierGenerator sequentialIdentifierGenerator) {
				return nextSequenceNum;
			}
		});

		generator.setFirstIdentifierBase("${currentDate:yyMM}0001");

		Date endOfJan = DateUtil.getDate("2020-01-31");
		when(DateUtil.getCurrentDate()).thenReturn(endOfJan);
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20010001"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20010002"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20010003"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20010004"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20010005"));

		Date startOfFeb = DateUtil.getDate("2020-02-01");
		when(DateUtil.getCurrentDate()).thenReturn(startOfFeb);
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20020001"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20020002"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20020003"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20020004"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), Matchers.is("KGH20020005"));
	}
}
