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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Useful utility methods
 */
public class IdgenUtil {
	
	protected static Log log = LogFactory.getLog(IdgenUtil.class);

	/**
	 * Converts a long to a String given the passed base characters
	 * @should convert from long to string in base character set
	 */
	public static String convertToBase(long n, char[] baseCharacters, int padToLength) {
    	StringBuilder base = new StringBuilder();
    	long numInBase = (long)baseCharacters.length;
    	while (n > 0) {
    		int index = (int)(n % numInBase);
    		base.insert(0, baseCharacters[index]);
    		n = (long)(n / numInBase);
    	}
    	while (base.length() < padToLength) {
    		base.insert(0, baseCharacters[0]);
    	}
    	return base.toString();
	}
	
	/**
	 * Converts a String back to an long based on the passed base characters
	 * @should convert from string in base character set to long
	 */
	public static long convertFromBase(String s, char[] baseCharacters) {
		long ret = 0;
		char[] inputChars = s.toCharArray();
		long multiplier = 1;
		for (int i = inputChars.length-1; i>=0; i--) {
			int index = -1;
			for (int j=0; j<baseCharacters.length; j++) {
				if (baseCharacters[j] == inputChars[i]) {
					index = j;
				}
			}
			if (index == -1) {
				throw new RuntimeException("Invalid character " + inputChars[i] + " found in " + s);
			}
			ret = ret + multiplier * index;
			multiplier *= baseCharacters.length;
		}
		return ret;
	}
	
	/**
	 * @return the contents from the supplied URL as a String
	 */
	public static List<String> getIdsFromStream(InputStream stream) {
		List<String> contents = new ArrayList<String>();
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(stream));
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				contents.add(line.trim());
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Error retrieving IDs from stream", e);
		}
		finally {
			if (r != null) {
				try {
					r.close();
				}
				catch (Exception e) {
					log.warn("Error closing reader: ", e);
				}
			}
		}
		return contents;
	}
}
