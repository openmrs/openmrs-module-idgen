package org.openmrs.module.idgen;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.idgen.prefixprovider.PrefixProvider;
import org.openmrs.module.idgen.suffixprovider.SuffixProvider;

/**
 * test class for {@link SequentialIdentifierGenerator}
 */
public class SequentialIdentifierGeneratorTest {

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
		SequentialIdentifierGenerator generator = spy(new SequentialIdentifierGenerator());
		generator.setBaseCharacterSet("0123456789");
		generator.setFirstIdentifierBase("000");

		PrefixProvider prefixProvider = mock(PrefixProvider.class);
		org.mockito.Mockito.when(prefixProvider.getValue()).thenReturn("LOC_2-");
		doReturn(prefixProvider).when(generator).getPrefixProvider(any());

		assertThat(generator.getIdentifierForSeed(1L), is("LOC_2-001"));
	}

	@Test
	public void getIdentifierForSeed_shouldGenerateLocationSuffixedIdFromLocationBasedSuffixProvider() {
		SequentialIdentifierGenerator generator = spy(new SequentialIdentifierGenerator());
		generator.setBaseCharacterSet("0123456789");
		generator.setFirstIdentifierBase("000");

		SuffixProvider suffixProvider = mock(SuffixProvider.class);
		org.mockito.Mockito.when(suffixProvider.getValue()).thenReturn("-LOC_2");
		doReturn(suffixProvider).when(generator).getSuffixProvider(any());

		assertThat(generator.getIdentifierForSeed(1L), is("001-LOC_2"));
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
}
