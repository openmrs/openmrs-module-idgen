package org.openmrs.module.idgen.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.Verifies;

public class LuhnModNIdentifierValidatorTest {
	
	/**
	 * @see {@link LuhnModNIdentifierValidator#computeCheckDigit(String)}
	 */
	@Test
	@Verifies(value = "should compute a valid check digit", method = "computeCheckDigit(String)")
	public void computeCheckDigit_shouldComputeAValidCheckDigit() throws Exception {
		String testCase = "7V3TW4P";
		LuhnMod30IdentifierValidator v = new LuhnMod30IdentifierValidator();
		char checkDigit = v.computeCheckDigit(testCase);
		System.out.println("Computed check digit: " + checkDigit);
		Assert.assertTrue(v.validateCheckDigit(testCase + checkDigit));
	}
}