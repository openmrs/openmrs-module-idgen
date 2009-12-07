/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.validator;

import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;

/**
 * A IdentifierValidator based on the Luhn Mod-N Algorithm
 * See http://en.wikipedia.org/wiki/Luhn_mod_N_algorithm
 */
public abstract class LuhnModNIdentifierValidator implements IdentifierValidator {
	
	/**
	 * This is the primary method for determining the Base for this implementation.
	 * For example, if you want a Mod-30 implementation, excluding characters B, I, O, Q, S, Z
	 * then you would set this to "0123456789ACDEFGHJKLMNPRTUVWXY";
	 * 
	 */
	public abstract String getBaseCharacters();
	
	/**
	 * This method converts all identifiers to a standard format.  This may include things
	 * like converting to upper-case, adding hyphens, etc.
	 * By default, this converts to upper-case and removes leading and trailing spaces
	 */
	public String standardizeValidIdentifier(String validIdentifier) {
		if (validIdentifier != null) {
			validIdentifier = validIdentifier.toUpperCase().trim();
		}
		return validIdentifier;
	}
	
	/** 
	 * By default, this will return all characters returned from getBaseCharacters,
	 * in both upper- and lower-case variants
	 * 
	 * @see IdentifierValidator#getAllowedCharacters()
	 */
	public String getAllowedCharacters() {
		StringBuilder sb = new StringBuilder();
		for (char c : getBaseCharacters().toCharArray()) {
			sb.append(Character.toLowerCase(c));
			sb.append(Character.toUpperCase(c));
		}
		return sb.toString();
	}

	/** 
	 * @see IdentifierValidator#getName()
	 */
	public String getName() {
		return "Luhn Mod-" + getBaseCharacters().length() + " Check-Digit Validator";
	}

	/** 
	 * @see IdentifierValidator#getValidIdentifier(String)
	 */
	public String getValidIdentifier(String undecoratedIdentifier) throws UnallowedIdentifierException {
		String standardized = standardizeValidIdentifier(undecoratedIdentifier);
		return standardized + computeCheckDigit(standardized);
	}

	/** 
	 * @see IdentifierValidator#isValid(String)
	 */
	public boolean isValid(String identifier) throws UnallowedIdentifierException {
		try {
			identifier = standardizeValidIdentifier(identifier);
			String undecoratedIdentifier = identifier.substring(0, identifier.length()-1);
			return identifier.equals(getValidIdentifier(undecoratedIdentifier));
		}
		catch (Exception e) {
			throw new UnallowedIdentifierException("Invalid identifier specified for validator", e);
		}
	}
	
	/**
	 * Computes the check digit for the passed undecorated identifier
	 * @should compute a valid check digit
	 */
	public char computeCheckDigit(String undecoratedIdentifier) {
		int factor = 2;
		int sum = 0;
		char[] inputChars = standardizeValidIdentifier(undecoratedIdentifier).toCharArray();
		char[] baseChars = getBaseCharacters().toCharArray();
		int mod = baseChars.length;
	 
		// Starting from the right and working leftwards is easier since the initial "factor" will always be "2" 
		for (int i = inputChars.length - 1; i >= 0; i--) {
			int codePoint = -1;
			for (int j=0; j<baseChars.length; j++) {
				if (baseChars[j] == inputChars[i]) {
					codePoint = j;
				}
			}
			if (codePoint == -1) {
				throw new UnallowedIdentifierException("Invalid character specified for validator");
			}
			int addend = factor * codePoint;
	 
			// Alternate the "factor" that each "codePoint" is multiplied by
			factor = (factor == 2) ? 1 : 2;
	 
			// Sum the digits as expressed in base "n"
			addend = (addend / mod) + (addend % mod);
			sum += addend;
		}
	 
		// Calculate the number that must be added to the "sum" to make it divisible by "n"
		int remainder = sum % mod;
		int checkCodePoint = mod - remainder;
		checkCodePoint %= mod;
		
		return baseChars[checkCodePoint];
	}
	
	/**
	 * Validates the check digit for the passed identifier
	 * @should validate a correct check digit
	 */
	public boolean validateCheckDigit(String identifier) {
		int factor = 1;
		int sum = 0;
		char[] inputChars = standardizeValidIdentifier(identifier).toCharArray();
		char[] baseChars = getBaseCharacters().toCharArray();
		int mod = baseChars.length;

		for (int i = inputChars.length-1; i >= 0; i--) {
			int codePoint = -1;
			for (int j=0; j<baseChars.length; j++) {
				if (baseChars[j] == inputChars[i]) {
					codePoint = j;
				}
			}
			if (codePoint == -1) {
				throw new UnallowedIdentifierException("Invalid character specified for validator");
			}
			int addend = factor * codePoint;
			// Alternate the "factor" that each "codePoint" is multiplied by
			factor = (factor == 2) ? 1 : 2;
		 
			// Sum the digits as expressed in base "n"
			addend = (addend / mod) + (addend % mod);
			sum += addend;
		}
		int remainder = sum % mod;
		return (remainder == 0);
	}
}
