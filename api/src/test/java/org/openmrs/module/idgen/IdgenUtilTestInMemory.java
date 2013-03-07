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
package org.openmrs.module.idgen;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 * This test used to be in {@link IdgenUtilTest}. I've moved it here and @Ignored it. At some point
 * someone should consider replicating this in an in-memory test. 
 */
@Ignore
public class IdgenUtilTestInMemory extends BaseModuleContextSensitiveTest {
	
	@Override
    public Boolean useInMemoryDatabase() {
        return false;
    }
	
	@Test
	public void testStuff() throws Exception {
		IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
		IdentifierSource is = iss.getIdentifierSource(6);
		Assert.assertNotNull(is);
		Assert.assertTrue(is instanceof SequentialIdentifierGenerator);
		Assert.assertFalse(is instanceof IdentifierPool);
		
		SequentialIdentifierGenerator sig = (SequentialIdentifierGenerator) is;
		System.out.println(sig.getMinLength());
	}
	
}
