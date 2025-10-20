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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.idgen.processor.IdentifierPoolProcessor;
import org.openmrs.module.idgen.processor.RemoteIdentifierSourceProcessor;
import org.openmrs.module.idgen.processor.SequentialIdentifierGeneratorProcessor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.task.IdgenTask;

/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
public class IdgenModuleActivator extends BaseModuleActivator implements DaemonTokenAware {

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void started() {
		IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
		service.registerProcessor(
				SequentialIdentifierGenerator.class,
				Context.getRegisteredComponents(SequentialIdentifierGeneratorProcessor.class).get(0)
		);
		service.registerProcessor(
				RemoteIdentifierSource.class,
				Context.getRegisteredComponents(RemoteIdentifierSourceProcessor.class).get(0)
		);
		service.registerProcessor(
				IdentifierPool.class,
				Context.getRegisteredComponents(IdentifierPoolProcessor.class).get(0)
		);
		IdgenTask.setEnabled(true);
		log.info("Idgen Module Started...");
	}

	@Override
	public void stopped() {
		log.info("Idgen Module Stopped...");
	}

	@Override
	public void setDaemonToken(DaemonToken token) {
		IdgenTask.setDaemonToken(token);
	}
}
