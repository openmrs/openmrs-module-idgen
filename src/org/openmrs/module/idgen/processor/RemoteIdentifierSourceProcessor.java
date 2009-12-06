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
package org.openmrs.module.idgen.processor;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenUtil;
import org.openmrs.module.idgen.RemoteIdentifierSource;

/**
 * Evaluates a RemoteIdentifierSource
 * By default, this expects an HTTP request to return a comma-separated String of identifiers.
 * This can be overridden in subclasses as needed
 */
public class RemoteIdentifierSourceProcessor implements IdentifierSourceProcessor {

	/** 
	 * @see IdentifierSourceProcessor#getIdentifiers(IdentifierSource, int)
	 */
	public synchronized List<String> getIdentifiers(IdentifierSource source, int batchSize) {
		RemoteIdentifierSource remote = (RemoteIdentifierSource)source;
		String url = remote.getUrl();
		url = url.replace("{batchSize}", Integer.toString(batchSize));
		String contents = IdgenUtil.getContentsFromUrl(url);
		List<String> r = new ArrayList<String>();
		for (String identifier : contents.split(",")) {
			r.add(identifier.replace("\"", ""));
		}
		return r;
	}
}