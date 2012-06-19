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

/**
 * A IdentifierValidator based on the Luhn Mod-N Algorithm, with "0123456789" as the legal characters.
 * See http://en.wikipedia.org/wiki/Luhn_mod_N_algorithm
 * 
 * This improves on the {@link org.openmrs.patient.impl.LuhnIdentifierValidator} class in the OpenMRS core
 * by not adding a hyphen between the base of the identifier and its check digit. ("24687" instead of "2468-7")
 */
public class LuhnMod10IdentifierValidator extends LuhnModNIdentifierValidator {
	
	/**
	 * @see org.openmrs.module.idgen.validator.LuhnModNIdentifierValidator#getBaseCharacters()
	 */
	@Override
	public String getBaseCharacters() {
		return "0123456789";
	}
	
}
