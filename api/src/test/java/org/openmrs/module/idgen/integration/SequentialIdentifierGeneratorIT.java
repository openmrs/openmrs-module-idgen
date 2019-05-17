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
package org.openmrs.module.idgen.integration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.APIException;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;

public class SequentialIdentifierGeneratorIT extends IdgenBaseTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void getPrefixProvider_shouldThrowWhenInvalidPrefixProviderBean() {
		// setup
		expectedException.expect(APIException.class);
		
		// replay
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.getPrefixProvider("provider:fooBarBean");
	}
	
	@Test
	public void getPrefixProvider_shouldThrowWhenMissingPrefixProviderBean() {
		// setup
		expectedException.expect(APIException.class);
		
		// replay
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.getPrefixProvider("provider:");
	}
	
	@Test
	public void getPrefixProvider_shouldThrowWhenBlankPrefixProviderBean() {
		// setup
		expectedException.expect(APIException.class);
		
		// replay
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.getPrefixProvider("provider:   ");
	}
}
