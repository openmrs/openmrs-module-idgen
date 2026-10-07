package org.openmrs.module.idgen.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.InvalidCheckDigitException;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.validator.PatientIdentifierValidator;

/**
 * Core accepts any identifier when it cannot find the validator of its type, so a broken registration of idgen's
 * validators in moduleApplicationContext.xml would otherwise go unnoticed
 */
public class IdentifierValidatorRegistrationTest extends IdgenBaseTest {

	@Test
	public void shouldEnforceIdgenValidatorsThroughCore() {
		IdentifierValidator[] validators = { new LuhnMod10IdentifierValidator(), new LuhnMod25IdentifierValidator(),
		        new LuhnMod30IdentifierValidator() };
		for (IdentifierValidator validator : validators) {
			PatientIdentifierType type = new PatientIdentifierType();
			type.setValidator(validator.getClass().getName());
			// 3, 4, 6 and 7 are allowed by all three validators, so the invalid identifier only has a wrong check digit
			String valid = validator.getValidIdentifier("3467");
			String invalid = valid.substring(0, valid.length() - 1) + (valid.endsWith("3") ? "4" : "3");
			PatientIdentifierValidator.validateIdentifier(valid, type);
			assertThrows(InvalidCheckDigitException.class, () -> PatientIdentifierValidator.validateIdentifier(invalid, type),
			    validator.getName());
		}
	}
}
