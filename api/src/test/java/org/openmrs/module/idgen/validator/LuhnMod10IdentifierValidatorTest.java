package org.openmrs.module.idgen.validator;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LuhnMod10IdentifierValidatorTest {
	
	LuhnMod10IdentifierValidator validator;
	
	@BeforeEach
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
		Assertions.assertEquals("24687", fullIdentifier);
		Assertions.assertTrue(validator.isValid(fullIdentifier));
	}

}