/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.idgen.rest.resource;

import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.rest.resource.AutoGenerationOptionResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AutoGenerationResourceTest extends BaseDelegatingResourceTest<AutoGenerationOptionResource, AutoGenerationOption> {
	@Override
	public AutoGenerationOption newObject() {
		return Context.getConceptService().getAutoGenerationOptionByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("location", getObject().getLoctaion());
		assertPropEquals("source", getObject().getSource());
		assertPropEquals("manualEntryEnabled", getObject().getManualEntryEnabled());
		assertPropEquals("automaticGenerationEnabled", getObject().AutomaticGenerationEnabled());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("location", getObject().getLoctaion());
		assertPropEquals("source", getObject().getSource());
		assertPropEquals("manualEntryEnabled", getObject().getManualEntryEnabled());
		assertPropEquals("automaticGenerationEnabled", getObject().AutomaticGenerationEnabled());
	}
	
	
}
