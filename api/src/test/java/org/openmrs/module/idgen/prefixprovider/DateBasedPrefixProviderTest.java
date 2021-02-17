package org.openmrs.module.idgen.prefixprovider;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class DateBasedPrefixProviderTest {

	@Before
	public void setup() throws Exception {
		mockStatic(Context.class);
		when(Context.loadClass(anyString())).thenAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocationOnMock) {
				try {
					return Class.forName(invocationOnMock.getArgumentAt(0, String.class));
				}
				catch (Exception e) {
					return null;
				}
			}
		});
	}
	
	@Test
	public void getValue_shouldReturnPrefixWithStandardDateFormats() {
		assertValidPrefix("yyyyMM");
		assertValidPrefix("yyyy-MM-dd");
		assertValidPrefix("yyyy/MM");

	}

	@Test
	public void getValue_shouldReturnPrefixWithLiteralStringsMixedWithDateFormats() {
		assertValidPrefix("'KGH'yyMM");
	}

	@Test(expected = RuntimeException.class)
	public void getValue_shouldThrowExceptionWithDateFormats() {
		DateBasedPrefixProvider provider = new DateBasedPrefixProvider("hello");
		provider.getValue();
	}

	protected void assertValidPrefix(String configuration) {
		Date d = new Date();
		String expected = new SimpleDateFormat(configuration).format(d);
		DateBasedPrefixProvider provider = new DateBasedPrefixProvider(configuration);
		Assert.assertThat(expected, is(provider.getValue()));
	}
}
