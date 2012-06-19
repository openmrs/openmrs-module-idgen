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
import org.junit.Test;
import org.openmrs.test.Verifies;

public class IdgenUtilTest {
	
	/**
	 * @see {@link IdgenUtil#convertFromBase(String,char[])}
	 */
	@Test
	@Verifies(value = "should convert from string in base character set to long", method = "convertFromBase(String,char[])")
	public void convertFromBase_shouldConvertFromStringInBaseCharacterSetToLong() throws Exception {
		char[] hexChars = "0123456789ABCDEF".toCharArray();
		long numericValue = 43804337214L;
		String hexValue = IdgenUtil.convertToBase(numericValue, hexChars, 0);
		System.out.println("Converted from numeric: " + numericValue + " to hex: " + hexValue);
		Assert.assertEquals("A32F1243E", hexValue);
		long back = IdgenUtil.convertFromBase(hexValue, hexChars);
		Assert.assertEquals(numericValue, back);	
	}

}

