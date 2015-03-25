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
package org.openmrs.module.idgen.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

/**
 * For all identifier pools that are configured to be refilled by a scheduled task, refill if necessary
 */
public class RefillIdentifierPoolsTask extends IdgenTask {

    private Log log = LogFactory.getLog(getClass());

    @Override
    public Runnable getRunnableTask() {
        return new RunnableTask();
    }

    private class RunnableTask implements Runnable {
        @Override
        public void run() {
            IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
            for (IdentifierSource source : service.getAllIdentifierSources(false)) {
                if (source instanceof IdentifierPool) {
                    IdentifierPool pool = (IdentifierPool) source;
                    if (pool.isRefillWithScheduledTask()) {
                        try {
                            service.checkAndRefillIdentifierPool(pool);
                        }
                        catch (Exception ex) {
                            log.warn("Failed to refill identifier pool: " + pool.getName(), ex);
                        }
                    }
                }
            }
        }
    }

}
