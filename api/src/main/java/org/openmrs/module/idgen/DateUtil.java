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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Useful date utility methods
 */
public class DateUtil {
	
	protected static Log log = LogFactory.getLog(DateUtil.class);

	/**
	 * @return the current date.  This wrapper method mainly exists for mocking during testing
	 */
	public static Date getCurrentDate() {
		return new Date();
	}

	public static Date getDate(String ymd) {
		if (StringUtils.isNotBlank(ymd)) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd").parse(ymd);
			}
			catch (Exception e) {
				throw new IllegalStateException("Unable to parse " + ymd + " into a date using yyyy-MM-dd format");
			}
		}
		return null;
	}
}
