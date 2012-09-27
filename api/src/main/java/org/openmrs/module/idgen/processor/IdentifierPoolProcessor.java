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
import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.service.IdentifierSourceService;

/**
 * Evaluates a RemoteIdentifierSource
 */
public class IdentifierPoolProcessor implements IdentifierSourceProcessor {

	/** 
	 * @see IdentifierSourceProcessor#getIdentifiers(IdentifierSource, int)
	 */
	public synchronized List<String> getIdentifiers(IdentifierSource source, int batchSize) {
		IdentifierPool pool = (IdentifierPool) source;
		IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
        if (!pool.isRefillWithScheduledTask()) {
		    iss.checkAndRefillIdentifierPool(pool);
        }
		List<PooledIdentifier> available = iss.getAvailableIdentifiers(pool, batchSize);
		List<String> ret = new ArrayList<String>();
		Date now = new Date();
		for (PooledIdentifier pi : available) {
			ret.add(pi.getIdentifier());
			pi.setDateUsed(now);
		}
		iss.saveIdentifierSource(source);
		return ret;
	}
}