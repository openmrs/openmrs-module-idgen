package org.openmrs.module.idgen.validator;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LuhnMod10IdentifierValidatorTest {
	
	LuhnMod10IdentifierValidator validator;
	
	@Before
	public void beforeEachTest() {
		validator = new LuhnMod10IdentifierValidator();
	}
	
	/**
	 * @see LuhnMod10IdentifierValidator
	 */
	@Test
	public void luhnMod10IdentifierValidator_shouldAppendCorrectCheckDigitWithoutDash() throws Exception {
		String base = "2468";
		String fullIdentifier = validator.getValidIdentifier(base);
		Assert.assertEquals("24687", fullIdentifier);
		Assert.assertTrue(validator.isValid(fullIdentifier));
	}

}